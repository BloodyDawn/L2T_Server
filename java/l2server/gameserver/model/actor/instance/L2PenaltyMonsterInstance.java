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

package l2server.gameserver.model.actor.instance;

import l2server.gameserver.ai.CtrlEvent;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.network.clientpackets.Say2;
import l2server.gameserver.network.serverpackets.CreatureSay;
import l2server.gameserver.templates.chars.L2NpcTemplate;
import l2server.util.Rnd;

public class L2PenaltyMonsterInstance extends L2MonsterInstance
{
	private L2PcInstance ptk;

	public L2PenaltyMonsterInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2PenaltyMonsterInstance);
	}

	@Override
	public L2Character getMostHated()
	{
		if (ptk != null)
		{
			return ptk; //always attack only one person
		}
		else
		{
			return super.getMostHated();
		}
	}

	public void setPlayerToKill(L2PcInstance ptk)
	{
		if (Rnd.get(100) <= 80)
		{
			CreatureSay cs =
					new CreatureSay(getObjectId(), Say2.ALL_NOT_RECORDED, getName(), "mmm your bait was delicious");
			broadcastPacket(cs);
		}
		this.ptk = ptk;
		addDamageHate(ptk, 0, 10);
		getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, ptk);
		addAttackerToAttackByList(ptk);
	}

	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
		{
			return false;
		}

		if (Rnd.get(100) <= 75)
		{
			CreatureSay cs = new CreatureSay(getObjectId(), Say2.ALL_NOT_RECORDED, getName(),
					"I will tell fishes not to take your bait");
			broadcastPacket(cs);
		}
		return true;
	}
}
