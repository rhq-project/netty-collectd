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

package org.rhq.metrics.netty.collectd.parts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Segismont
 */
public enum PartType {
    HOST(PartTypeId.HOST), TIME(PartTypeId.TIME), TIME_HIGH_RESOLUTION(PartTypeId.TIME_HIGH_RESOLUTION), PLUGIN(
        PartTypeId.PLUGIN), PLUGIN_INSTANCE(PartTypeId.PLUGIN_INSTANCE), TYPE(PartTypeId.TYPE), INSTANCE(
        PartTypeId.INSTANCE), VALUES(PartTypeId.VALUES), INTERVAL(PartTypeId.INTERVAL), INTERVAL_HIGH_RESOLUTION(
        PartTypeId.INTERVAL_HIGH_RESOLUTION);

    private short id;

    PartType(short id) {
        this.id = id;
    }

    private static final Map<Short, PartType> TYPE_BY_ID = new HashMap<Short, PartType>();

    static {
        for (PartType partType : PartType.values()) {
            TYPE_BY_ID.put(partType.id, partType);
        }
    }

    public static PartType findById(short id) {
        return TYPE_BY_ID.get(id);
    }

    private static class PartTypeId {
        private static final short HOST = 0x0000;
        private static final short TIME = 0x0001;
        private static final short TIME_HIGH_RESOLUTION = 0x0008;
        private static final short PLUGIN = 0x0002;
        private static final short PLUGIN_INSTANCE = 0x0003;
        private static final short TYPE = 0x0004;
        private static final short INSTANCE = 0x0005;
        private static final short VALUES = 0x0006;
        private static final short INTERVAL = 0x0007;
        private static final short INTERVAL_HIGH_RESOLUTION = 0x0009;
    }
}
