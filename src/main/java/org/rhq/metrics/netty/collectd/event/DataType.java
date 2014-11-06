/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rhq.metrics.netty.collectd.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Segismont
 */
public enum DataType {
    COUNTER(DataTypeId.COUNTER), GAUGE(DataTypeId.GAUGE), DERIVE(DataTypeId.DERIVE), ABSOLUTE(DataTypeId.ABSOLUTE);

    private byte id;

    DataType(byte id) {
        this.id = id;
    }

    private static final Map<Byte, DataType> TYPE_BY_ID = new HashMap<Byte, DataType>();

    static {
        for (DataType dataType : DataType.values()) {
            TYPE_BY_ID.put(dataType.id, dataType);
        }
    }

    public static DataType findById(byte id) {
        return TYPE_BY_ID.get(id);
    }

    private static class DataTypeId {
        private static final byte COUNTER = 0x0;
        private static final byte GAUGE = 0x1;
        private static final byte DERIVE = 0x2;
        private static final byte ABSOLUTE = 0x3;
    }
}
