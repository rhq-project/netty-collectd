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

package org.rhq.metrics.netty.collectd.packet;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates <a href="https://collectd.org/wiki/index.php/Binary_protocol#Part_types">Part Types</a>.
 *
 * @author Thomas Segismont
 */
public enum PartType {
    /** The name of the host to associate with subsequent data values. */
    HOST(PartTypeId.HOST),
    /** The timestamp to associate with subsequent data values, unix time format (seconds since epoch). **/
    TIME(PartTypeId.TIME),
    /** The timestamp to associate with subsequent data values. Time is defined in 2<sup>-30</sup> seconds since epoch. **/
    TIME_HIGH_RESOLUTION(PartTypeId.TIME_HIGH_RESOLUTION),
    /** The plugin name to associate with subsequent data values, e.g. "cpu". **/
    PLUGIN(PartTypeId.PLUGIN),
    /** The plugin instance name to associate with subsequent data values, e.g. "1". **/
    PLUGIN_INSTANCE(PartTypeId.PLUGIN_INSTANCE),
    /** The type name to associate with subsequent data values, e.g. "cpu". **/
    TYPE(PartTypeId.TYPE),
    /** The type instance name to associate with subsequent data values, e.g. "idle". **/
    INSTANCE(PartTypeId.INSTANCE),
    /** Data values. **/
    VALUES(PartTypeId.VALUES),
    /** The interval in which subsequent data values are collected, unix time format (seconds since epoch). **/
    INTERVAL(PartTypeId.INTERVAL),
    /** The interval in which subsequent data values are collected. The interval is given in 2<sup>-30</sup> seconds. **/
    INTERVAL_HIGH_RESOLUTION(PartTypeId.INTERVAL_HIGH_RESOLUTION);

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

    /**
     * @param id part type id
     * @return the {@link PartType} which id is <code>id</code>, null otherwise
     */
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
