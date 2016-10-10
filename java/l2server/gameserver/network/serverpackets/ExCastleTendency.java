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

package l2server.gameserver.network.serverpackets;

/**
 * @author Pere
 */
public class ExCastleTendency extends L2GameServerPacket
{
    private int _castleId;
    private int _tendency;

    public ExCastleTendency(int castleId, int tendency)
    {
        _castleId = castleId;
        _tendency = tendency;
    }

    /**
     * @see l2server.util.network.BaseSendablePacket.ServerBasePacket#writeImpl()
     */
    @Override
    protected final void writeImpl()
    {
        writeD(_castleId);
        writeD(_tendency);
    }
}