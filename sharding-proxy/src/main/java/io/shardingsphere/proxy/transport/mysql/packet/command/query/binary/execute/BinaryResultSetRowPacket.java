/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.proxy.transport.mysql.packet.command.query.binary.execute;

import io.shardingsphere.proxy.transport.mysql.constant.ColumnType;
import io.shardingsphere.proxy.transport.mysql.packet.MySQLPacket;
import io.shardingsphere.proxy.transport.mysql.packet.MySQLPacketPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Binary result set row packet.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/binary-protocol-resultset-row.html">Binary Protocol Resultset Row</a>
 *
 * @author zhangyonglun
 */
@RequiredArgsConstructor
@Getter
public final class BinaryResultSetRowPacket implements MySQLPacket {
    
    private static final int PACKET_HEADER = 0x00;
    
    private static final int NULL_BITMAP_OFFSET = 2;
    
    private final int sequenceId;
    
    private final int columnsCount;
    
    private final List<Object> data;
    
    private final List<ColumnType> columnTypes;
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeInt1(PACKET_HEADER);
        NullBitmap nullBitmap = new NullBitmap(columnsCount, NULL_BITMAP_OFFSET);
        for (int columnIndex = 0; columnIndex < columnsCount; columnIndex++) {
            if (null == data.get(columnIndex)) {
                nullBitmap.setNullBit(columnIndex);
            }
        }
        for (int each : nullBitmap.getNullBitmap()) {
            payload.writeInt1(each);
        }
        for (int i = 0; i < columnsCount; i++) {
            new BinaryProtocolValue(columnTypes.get(i), payload).write(data.get(i));
        }
    }
}
