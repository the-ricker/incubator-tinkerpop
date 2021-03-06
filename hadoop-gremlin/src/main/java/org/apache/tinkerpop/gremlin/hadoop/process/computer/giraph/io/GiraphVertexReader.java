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
package org.apache.tinkerpop.gremlin.hadoop.process.computer.giraph.io;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.VertexReader;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.tinkerpop.gremlin.hadoop.process.computer.giraph.GiraphComputeVertex;
import org.apache.tinkerpop.gremlin.hadoop.structure.io.VertexWritable;

import java.io.IOException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GiraphVertexReader extends VertexReader {

    private RecordReader<NullWritable, VertexWritable> recordReader;

    public GiraphVertexReader(final RecordReader<NullWritable, VertexWritable> recordReader) {
        this.recordReader = recordReader;
    }

    @Override
    public void initialize(final InputSplit inputSplit, final TaskAttemptContext context) throws IOException, InterruptedException {
        this.recordReader.initialize(inputSplit, context);
    }

    @Override
    public boolean nextVertex() throws IOException, InterruptedException {
        return this.recordReader.nextKeyValue();
    }

    @Override
    public Vertex getCurrentVertex() throws IOException, InterruptedException {
        return new GiraphComputeVertex(this.recordReader.getCurrentValue());
    }

    @Override
    public void close() throws IOException {
        this.recordReader.close();
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return this.recordReader.getProgress();
    }
}
