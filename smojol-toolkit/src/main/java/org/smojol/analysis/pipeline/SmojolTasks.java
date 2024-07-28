package org.smojol.analysis.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojo.woof.Advisor;
import com.mojo.woof.GraphSDK;
import com.mojo.woof.OpenAICredentials;
import org.antlr.v4.runtime.tree.ParseTree;
import org.neo4j.driver.Record;
import org.smojol.analysis.ParsePipeline;
import org.smojol.analysis.graph.DataStructureSummariseAction;
import org.smojol.analysis.graph.NamespaceQualifier;
import org.smojol.analysis.graph.NodeSpecBuilder;
import org.smojol.analysis.graph.SummariseAction;
import org.smojol.analysis.graph.graphml.JGraphTGraphBuilder;
import org.smojol.analysis.graph.neo4j.*;
import org.smojol.common.ast.*;
import org.smojol.common.flowchart.*;
import org.smojol.common.id.IdProvider;
import org.smojol.common.navigation.CobolEntityNavigator;
import org.smojol.common.vm.structure.CobolDataStructure;
import org.smojol.interpreter.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.mojo.woof.NodeProperties.TYPE;
import static com.mojo.woof.NodeRelations.CONTAINS;

public class SmojolTasks {
    private final NodeReferenceStrategy astNodeReferenceStrategy;
    private final NodeReferenceStrategy dataDependencyAttachmentStrategy;
    private final SourceConfig sourceConfig;
    private final FlowchartOutputConfig flowchartOutputConfig;
    private final RawASTOutputConfig rawAstOutputConfig;
    private final GraphSDK graphSDK;
    private final GraphMLExportConfig graphMLOutputConfig;
    private final FlowASTOutputConfig flowASTOutputConfig;
    private final CFGOutputConfig cfgOutputConfig;
    private final IdProvider idProvider;
    private final ParsePipeline pipeline;
    private CobolEntityNavigator navigator;
    private CobolDataStructure dataStructures;
    private final NodeSpecBuilder qualifier;
    private FlowNode astRoot;


    public Runnable INJECT_INTO_NEO4J = new Runnable() {
        @Override
        public void run() {
            exportToNeo4J(astRoot, dataStructures, qualifier, graphSDK);
        }
    };

    public Runnable EXPORT_TO_GRAPHML = new Runnable() {
        @Override
        public void run() {
            try {
                Files.createDirectories(graphMLOutputConfig.outputDir());
                String graphMLOutputPath = graphMLOutputConfig.outputDir().resolve(graphMLOutputConfig.outputPath()).toAbsolutePath().normalize().toString();
                exportToGraphML(astRoot, dataStructures, qualifier, graphMLOutputPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public Runnable WRITE_RAW_AST = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.printf("AST Output Dir is: %s%n", rawAstOutputConfig.astOutputDir());
                Files.createDirectories(rawAstOutputConfig.astOutputDir());
                rawAstOutputConfig.visualiser().writeCobolAST(pipeline.getTree(), sourceConfig.cobolParseTreeOutputPath(), false, navigator);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public Runnable WRITE_FLOW_AST = this::writeFlowAST;

    private void writeFlowAST() {
        SerialisableASTFlowNode serialisableASTFlowRoot = new SerialiseFlowASTTask().serialisedFlowAST(astRoot);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(serialisableASTFlowRoot);
        try {
            Files.createDirectories(flowASTOutputConfig.outputDir());
            PrintWriter out = new PrintWriter(flowASTOutputConfig.outputPath());
            out.println(json);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Runnable DRAW_FLOWCHART = new Runnable() {
        @Override
        public void run() {
            ParseTree root = navigator.procedureBodyRoot();
            try {
                Files.createDirectories(flowchartOutputConfig.dotFileOutputDir());
                Files.createDirectories(flowchartOutputConfig.imageOutputDir());
                flowchartOutputConfig.flowchartGenerationStrategy().draw(navigator, root, pipeline, flowchartOutputConfig.dotFileOutputDir(), flowchartOutputConfig.imageOutputDir(), sourceConfig.programName());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public Runnable WRITE_CFG = new Runnable() {
        @Override
        public void run() {
            SerialisableCFGGraphCollector cfgGraphCollector = new SerialisableCFGGraphCollector(idProvider);
            astRoot.accept(cfgGraphCollector, -1);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(cfgGraphCollector);
            try {
                Files.createDirectories(cfgOutputConfig.outputDir());
                PrintWriter out = new PrintWriter(cfgOutputConfig.outputPath());
                out.println(json);
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    };

    public Runnable SUMMARISE_THROUGH_LLM = new Runnable() {
        @Override
        public void run() {
            summariseThroughLLM(qualifier, graphSDK);
        }
    };

    public SmojolTasks(ParsePipeline pipeline, NodeReferenceStrategy astNodeReferenceStrategy, NodeReferenceStrategy dataDependencyAttachmentStrategy, SourceConfig sourceConfig, FlowchartOutputConfig flowchartOutputConfig, RawASTOutputConfig rawAstOutputConfig, GraphSDK graphSDK, GraphMLExportConfig graphMLOutputConfig, FlowASTOutputConfig flowASTOutputConfig, CFGOutputConfig cfgOutputConfig, IdProvider idProvider) {
        this.pipeline = pipeline;
        this.astNodeReferenceStrategy = astNodeReferenceStrategy;
        this.dataDependencyAttachmentStrategy = dataDependencyAttachmentStrategy;
        this.sourceConfig = sourceConfig;
        this.flowchartOutputConfig = flowchartOutputConfig;
        this.rawAstOutputConfig = rawAstOutputConfig;
        this.graphSDK = graphSDK;
        this.graphMLOutputConfig = graphMLOutputConfig;
        this.flowASTOutputConfig = flowASTOutputConfig;
        this.cfgOutputConfig = cfgOutputConfig;
        this.idProvider = idProvider;
        qualifier = new NodeSpecBuilder(new NamespaceQualifier("NEW-CODE"));
    }

    public SmojolTasks build() throws IOException {
        navigator = pipeline.parse();
        dataStructures = pipeline.getDataStructures();

        ParseTree procedure = navigator.procedureBodyRoot();
        FlowchartBuilder flowcharter = pipeline.flowcharter();
        flowcharter.buildFlowAST(procedure).buildControlFlow().buildOverlay();
        astRoot = flowcharter.getRoot();

        return this;
    }

    public void run(List<AnalysisTask> analysisTasks) throws IOException {
        tasks(analysisTasks).forEach(Runnable::run);
    }

    private Stream<Runnable> tasks(List<AnalysisTask> analysisTasks) {
        return analysisTasks.stream().map(t -> switch (t) {
            case INJECT_INTO_NEO4J -> INJECT_INTO_NEO4J;
            case EXPORT_TO_GRAPHML -> EXPORT_TO_GRAPHML;
            case WRITE_RAW_AST -> WRITE_RAW_AST;
            case DRAW_FLOWCHART -> DRAW_FLOWCHART;
            case WRITE_FLOW_AST -> WRITE_FLOW_AST;
            case WRITE_CFG -> WRITE_CFG;
        });
    }

    private static void exportToGraphML(FlowNode astRoot, CobolDataStructure dataStructures, NodeSpecBuilder qualifier, String outputPath) {
        JGraphTGraphBuilder graphMLExporter = new JGraphTGraphBuilder(dataStructures, astRoot, qualifier);
        graphMLExporter.buildAST();
        graphMLExporter.buildCFG();
        graphMLExporter.buildDataStructures();
        graphMLExporter.write(new File(outputPath));
    }

    private void exportToNeo4J(FlowNode root, CobolDataStructure dataStructures, NodeSpecBuilder qualifier, GraphSDK sdk) {
        // Builds Control Flow Graph
        root.accept(new Neo4JFlowCFGVisitor(sdk, qualifier), -1);

        // Builds AST
        Neo4JGraphBuilder neo4JExporter = new Neo4JGraphBuilder(sdk, dataStructures, qualifier, this.astNodeReferenceStrategy, this.dataDependencyAttachmentStrategy);
        neo4JExporter.buildAST(root);

        // Builds data structures
        dataStructures.accept(new Neo4JDataStructureVisitor(sdk, qualifier), null, n -> false, dataStructures);
        dataStructures.accept(new Neo4JRedefinitionVisitor(sdk, qualifier), null, n -> false, dataStructures);

        // Builds data dependencies
        neo4JExporter.buildDataDependencies(root);
    }

    private static void summariseThroughLLM(NodeSpecBuilder qualifier, GraphSDK sdk) {
        Record neo4jProgramRoot = sdk.findNodes(qualifier.astNodeCriteria(Map.of(TYPE, FlowNodeType.PROCEDURE_DIVISION_BODY.toString()))).getFirst();
        Record neo4jDataStructuresRoot = sdk.findNodes(qualifier.dataNodeSearchCriteria(Map.of(TYPE, "ROOT"))).getFirst();
        Advisor advisor = new Advisor(OpenAICredentials.fromEnv());
        // Summarises AST bottom-up
        sdk.traverse(neo4jProgramRoot, new SummariseAction(advisor, sdk), CONTAINS);
        // Summarises data structures
        sdk.traverse(neo4jDataStructuresRoot, new DataStructureSummariseAction(advisor, sdk), CONTAINS);
    }
}
