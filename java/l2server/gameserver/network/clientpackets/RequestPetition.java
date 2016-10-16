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
import l2server.gameserver.GmListTable;
import l2server.gameserver.instancemanager.PetitionManager;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.SystemMessage;

/**
 * <p>Format: (c) Sd
 * <ul>
 * <li>S: content</li>
 * <li>d: type</li>
 * </ul></p>
 *
 * @author -Wooden-, TempyIncursion
 */
public final class RequestPetition extends L2GameClientPacket
{
	//

	private String content;
	private int type; // 1 = on : 0 = off;

	@Override
	protected void readImpl()
	{
		content = readS();
		type = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (!GmListTable.getInstance().isGmOnline(false))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NO_GM_PROVIDING_SERVICE_NOW));
			return;
		}

		if (!PetitionManager.getInstance().isPetitioningAllowed())
		{
			activeChar.sendPacket(
					SystemMessage.getSystemMessage(SystemMessageId.GAME_CLIENT_UNABLE_TO_CONNECT_TO_PETITION_SERVER));
			return;
		}

		if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_ONE_ACTIVE_PETITION_AT_TIME));
			return;
		}

		if (PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_SYSTEM_CURRENT_UNAVAILABLE));
			return;
		}

		int totalPetitions = PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar) + 1;

		if (totalPetitions > Config.MAX_PETITIONS_PER_PLAYER)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.WE_HAVE_RECEIVED_S1_PETITIONS_TODAY);
			sm.addNumber(totalPetitions);
			activeChar.sendPacket(sm);
			sm = null;
			return;
		}

		if (content.length() > 255)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PETITION_MAX_CHARS_255));
			return;
		}

		int petitionId = PetitionManager.getInstance().submitPetition(activeChar, content, type);

		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PETITION_ACCEPTED_RECENT_NO_S1);
		sm.addNumber(petitionId);
		activeChar.sendPacket(sm);

		sm = SystemMessage.getSystemMessage(SystemMessageId.SUBMITTED_YOU_S1_TH_PETITION_S2_LEFT);
		sm.addNumber(totalPetitions);
		sm.addNumber(Config.MAX_PETITIONS_PER_PLAYER - totalPetitions);
		activeChar.sendPacket(sm);

		sm = SystemMessage.getSystemMessage(SystemMessageId.S1_PETITION_ON_WAITING_LIST);
		sm.addNumber(PetitionManager.getInstance().getPendingPetitionCount());
		activeChar.sendPacket(sm);
		sm = null;
	}
}
