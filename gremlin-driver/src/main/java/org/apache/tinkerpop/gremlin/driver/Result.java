/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.driver;

import org.apache.tinkerpop.gremlin.driver.message.ResponseResult;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * An {@code Item} represents an result value from the server.
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class Result {
    final Object resultObject;

    /**
     * Constructs a "result" from data found in {@link ResponseResult#getData()}.
     */
    public Result(final Object responseData) {
        this.resultObject = responseData;
    }

    public String getString() {
        return resultObject.toString();
    }

    public int getInt() {
        return Integer.parseInt(resultObject.toString());
    }

    public byte getByte() {
        return Byte.parseByte(resultObject.toString());
    }

    public short getShort() {
        return Short.parseShort(resultObject.toString());
    }

    public long getLong() {
        return Long.parseLong(resultObject.toString());
    }

    public float getFloat() {
        return Float.parseFloat(resultObject.toString());
    }

    public double getDouble() {
        return Double.parseDouble(resultObject.toString());
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(resultObject.toString());
    }

    public boolean isNull() {
        return null == resultObject;
    }

    public Vertex getVertex() {
        return (Vertex) resultObject;
    }

    public Edge getEdge() {
        return (Edge) resultObject;
    }

    public Element getElement() {
        return (Element) resultObject;
    }

    public <T> T get(final Class<? extends T> clazz) {
        return clazz.cast(this.resultObject);
    }

    public Object getObject() {
        return this.resultObject;
    }

    @Override
    public String toString() {
        final String c = resultObject != null ? resultObject.getClass().getCanonicalName() : "null";
        return "result{" +
                "object=" + resultObject + " " +
                "class=" + c +
                '}';
    }
}
