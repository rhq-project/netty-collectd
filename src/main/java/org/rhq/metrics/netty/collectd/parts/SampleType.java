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
public enum SampleType {
    COUNTER(SampleTypeId.COUNTER), GAUGE(SampleTypeId.GAUGE), DERIVE(SampleTypeId.DERIVE), ABSOLUTE(
        SampleTypeId.ABSOLUTE);

    private byte id;

    SampleType(byte id) {
        this.id = id;
    }

    private static final Map<Byte, SampleType> TYPE_BY_ID = new HashMap<Byte, SampleType>();

    static {
        for (SampleType partType : SampleType.values()) {
            TYPE_BY_ID.put(partType.id, partType);
        }
    }

    public static SampleType findById(byte id) {
        return TYPE_BY_ID.get(id);
    }

    private static class SampleTypeId {
        private static final byte COUNTER = 0x0;
        private static final byte GAUGE = 0x1;
        private static final byte DERIVE = 0x2;
        private static final byte ABSOLUTE = 0x3;
    }
}
