/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.network.clientpackets;

import l2server.Config;
import l2server.gameserver.datatables.AdminCommandAccessRights;
import l2server.gameserver.handler.AdminCommandHandler;
import l2server.gameserver.handler.IAdminCommandHandler;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.util.GMAudit;
import l2server.log.Log;

/**
 * @author Dezmond_snz
 *         Format: cddd
 */
public final class DlgAnswer extends L2GameClientPacket
{

	private int messageId;
	private int answer;
	private int requesterId;

	@Override
	protected void readImpl()
	{
		messageId = readD();
		answer = readD();
		requesterId = readD();
	}

	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (Config.DEBUG)
		{
			Log.fine(getType() + ": Answer accepted. Message ID " + messageId + ", answer " + answer +
					", Requester ID " + requesterId);
		}
		if (messageId == SystemMessageId.RESSURECTION_REQUEST_BY_C1_FOR_S2_XP.getId() ||
				messageId == SystemMessageId.RESURRECT_USING_CHARM_OF_COURAGE.getId())
		{
			activeChar.reviveAnswer(answer);
		}
		else if (messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
		{
			activeChar.teleportAnswer(answer, requesterId);
		}
		else if (messageId == SystemMessageId.S1.getId())
		{
			if (Config.L2JMOD_ALLOW_WEDDING && activeChar.isEngageRequest())
			{
				activeChar.engageAnswer(answer);
			}
			else if (activeChar.isMobSummonRequest())
			{
				activeChar.mobSummonAnswer(answer);
			}
			else if (activeChar.isMobSummonExchangeRequest())
			{
				activeChar.mobSummonExchangeAnswer(answer);
			}
			else if (activeChar.isChessChallengeRequest())
			{
				activeChar.chessChallengeAnswer(answer);
			}
			else
			{
				String fullCommand = activeChar.getAdminConfirmCmd();
				activeChar.setAdminConfirmCmd(null);
				if (answer == 0)
				{
					return;
				}
				String command = fullCommand.split(" ")[0];
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				if (AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					if (Config.GMAUDIT)
					{
						GMAudit.auditGMAction(activeChar.getName(), fullCommand,
								activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
					}
					ach.useAdminCommand(fullCommand, activeChar);
				}
			}
		}
		else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
		{
			activeChar.gatesAnswer(answer, 1);
		}
		else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
		{
			activeChar.gatesAnswer(answer, 0);
		}
	}
}
