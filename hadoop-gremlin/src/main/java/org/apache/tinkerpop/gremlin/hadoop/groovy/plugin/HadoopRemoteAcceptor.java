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
package org.apache.tinkerpop.gremlin.hadoop.groovy.plugin;

import org.apache.tinkerpop.gremlin.groovy.loaders.SugarLoader;
import org.apache.tinkerpop.gremlin.groovy.plugin.RemoteAcceptor;
import org.apache.tinkerpop.gremlin.groovy.plugin.RemoteException;
import org.apache.tinkerpop.gremlin.hadoop.structure.HadoopGraph;
import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.traversal.TraversalVertexProgram;
import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.ComputerResultStep;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.util.DefaultTraversal;
import org.codehaus.groovy.tools.shell.Groovysh;

import java.io.IOException;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class HadoopRemoteAcceptor implements RemoteAcceptor {

    private static final String USE_SUGAR = "useSugar";
    private static final String SPACE = " ";

    protected HadoopGraph hadoopGraph;
    private Groovysh shell;
    private boolean useSugarPlugin = false;

    public HadoopRemoteAcceptor(final Groovysh shell) {
        this.shell = shell;
    }

    @Override
    public Object connect(final List<String> args) throws RemoteException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("The variable name of the graph object must be provided");
        }
        this.hadoopGraph = (HadoopGraph) this.shell.getInterp().getContext().getVariable(args.get(0));
        return this.hadoopGraph;
    }

    @Override
    public Object configure(final List<String> args) throws RemoteException {
        for (int i = 0; i < args.size(); i = i + 2) {
            if (args.get(i).equals(USE_SUGAR))
                this.useSugarPlugin = Boolean.valueOf(args.get(i + 1));
            else
                throw new IllegalArgumentException("The provided configuration is unknown: " + args);
        }
        return this.hadoopGraph;
    }

    @Override
    public Object submit(final List<String> args) throws RemoteException {
        try {
            String script = RemoteAcceptor.getScript(String.join(SPACE, args), this.shell);
            if (this.useSugarPlugin)
                script = SugarLoader.class.getCanonicalName() + ".load()\n" + script;
            final TraversalVertexProgram program = TraversalVertexProgram.build().traversal(GraphTraversalSource.computer(), "gremlin-groovy", script).create(this.hadoopGraph);
            final ComputerResult computerResult = this.hadoopGraph.compute().program(program).submit().get();
            this.shell.getInterp().getContext().setVariable(RESULT, computerResult);


            final Traversal.Admin<?, ?> traversal = new DefaultTraversal<>(computerResult.graph());
            traversal.addStep(new ComputerResultStep<>(traversal, computerResult, false));
            return traversal;
        } catch (final Exception e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public void close() throws IOException {
        this.hadoopGraph.close();
    }

    public HadoopGraph getGraph() {
        return this.hadoopGraph;
    }

    public String toString() {
        return "HadoopRemoteAcceptor[" + this.hadoopGraph + "]";
    }
}
