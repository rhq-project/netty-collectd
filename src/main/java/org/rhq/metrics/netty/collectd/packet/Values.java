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

import static org.rhq.metrics.netty.collectd.util.Assert.assertEquals;
import static org.rhq.metrics.netty.collectd.util.Assert.assertNotNull;

import java.util.Arrays;

import org.rhq.metrics.netty.collectd.event.DataType;

/**
 * @author Thomas Segismont
 */
public final class Values {
    private final DataType[] dataTypes;
    private final Number[] data;

    public Values(DataType[] dataTypes, Number[] data) {
        assertNotNull(dataTypes, "dataTypes is null");
        assertNotNull(data, "data is null");
        assertEquals(dataTypes.length, data.length, "dataTypes and data arrays have different sizes: %d, %d",
            dataTypes.length, data.length);
        this.dataTypes = dataTypes;
        this.data = data;
    }

    public int getCount() {
        return data.length;
    }

    public DataType[] getDataTypes() {
        return dataTypes;
    }

    public Number[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Values[" + "dataTypes=" + Arrays.asList(dataTypes) + ", data=" + Arrays.asList(data) + ']';
    }
}
