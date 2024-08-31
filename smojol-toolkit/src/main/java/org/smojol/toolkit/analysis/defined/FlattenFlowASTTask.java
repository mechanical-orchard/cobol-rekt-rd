package org.smojol.toolkit.analysis.defined;

import com.mojo.woof.EdgeType;
import com.mojo.woof.GraphSDK;
import com.mojo.woof.Neo4JDriverBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.driver.Record;
import org.smojol.common.ast.*;
import org.smojol.common.id.UUIDProvider;
import org.smojol.common.navigation.PseudocodeNavigator;
import org.smojol.common.pseudocode.CodeSentinelType;
import org.smojol.common.pseudocode.PseudocodeInstruction;
import org.smojol.toolkit.analysis.graph.NamespaceQualifier;
import org.smojol.toolkit.analysis.graph.NodeSpecBuilder;
import org.smojol.toolkit.analysis.graph.NodeToWoof;
import org.smojol.toolkit.analysis.pipeline.config.FlowASTOutputConfig;
import org.smojol.toolkit.task.*;

import java.util.ArrayList;
import java.util.List;

public class FlattenFlowASTTask implements AnalysisTask {
    private final FlowNode astRoot;
    private final FlowASTOutputConfig flowASTOutputConfig;
    private final Neo4JDriverBuilder neo4JDriverBuilder;

    public FlattenFlowASTTask(FlowNode astRoot, FlowASTOutputConfig flowASTOutputConfig, Neo4JDriverBuilder neo4JDriverBuilder) {
        this.astRoot = astRoot;
        this.flowASTOutputConfig = flowASTOutputConfig;
        this.neo4JDriverBuilder = neo4JDriverBuilder;
    }

    @Override
    public AnalysisTaskResult run() {
        AnalysisTaskResult result = new BuildPseudocodeTask(astRoot, new UUIDProvider()).run();
        return switch (result) {
            case AnalysisTaskResultOK o -> updateGraph(o.getDetail());
            case AnalysisTaskResultError e -> new AnalysisTaskResultError(e.getException(), CommandLineAnalysisTask.FLATTEN_FLOW_AST);
        };
    }

    private AnalysisTaskResult updateGraph(List<PseudocodeInstruction> instructions) {
        PseudocodeNavigator navigator = new PseudocodeNavigator(astRoot);
        List<InstructionEdge> edges = new ArrayList<>();
        List<FlowNodeLike> nodes = new ArrayList<>(instructions);

        for (int i = 0; i < instructions.size() - 1; i++) {
            PseudocodeInstruction current = instructions.get(i);
            PseudocodeInstruction next = instructions.get(i + 1);
            if (current.isJump() && current.codeSentinelType() == CodeSentinelType.BODY) {
                Pair<List<PseudocodeInstruction>, List<PseudocodeInstruction>> callTargets = navigator.findCallTargets(current, i, instructions);
                PseudocodeInstruction returnJoinPoint = navigator.findSingleByCondition(instruction -> instruction.getNode() == current.getNode() && instruction.getSentinelType() == CodeSentinelType.EXIT, instructions);
                List<PseudocodeInstruction> entries = callTargets.getLeft();
                List<PseudocodeInstruction> exits = callTargets.getRight();
                entries.forEach(ct -> edges.add(new InstructionEdge(current, ct, InstructionEdgeType.JUMP)));
                exits.forEach(ct -> edges.add(new InstructionEdge(ct, returnJoinPoint, InstructionEdgeType.RETURN)));
                edges.add(new InstructionEdge(current, next, InstructionEdgeType.SYNTANTICALLY_FOLLOWED_BY));
                int returnJoinPointIndex = instructions.indexOf(returnJoinPoint);
                if (returnJoinPointIndex + 1 < instructions.size()) edges.add(new InstructionEdge(returnJoinPoint, instructions.get(returnJoinPointIndex + 1), InstructionEdgeType.FOLLOWED_BY));
            } else if (current.isCondition() && current.codeSentinelType() == CodeSentinelType.BODY) {
                List<PseudocodeInstruction> branchEntries = current.getNode().astChildren().stream().map(ins -> navigator.findSingleByCondition(in -> in.getNode() == ins && in.codeSentinelType() == CodeSentinelType.ENTER, instructions)).toList();
                List<PseudocodeInstruction> branchExits = current.getNode().astChildren().stream().map(ins -> navigator.findSingleByCondition(in -> in.getNode() == ins && in.codeSentinelType() == CodeSentinelType.EXIT, instructions)).toList();
                PseudocodeInstruction convergencePoint = navigator.findSingleByCondition(instruction -> instruction.getNode() == current.getNode() && instruction.getSentinelType() == CodeSentinelType.EXIT, instructions);
                branchEntries.forEach(c -> edges.add(new InstructionEdge(current, c, InstructionEdgeType.BRANCHES_TO)));
                branchExits.forEach(c -> edges.add(new InstructionEdge(c, convergencePoint, InstructionEdgeType.FOLLOWED_BY)));
                int convergencePointIndex = instructions.indexOf(convergencePoint);
                if (branchEntries.size() == 1) edges.add(new InstructionEdge(current, convergencePoint, InstructionEdgeType.BRANCHES_TO));
                if (convergencePointIndex + 1 < instructions.size()) edges.add(new InstructionEdge(convergencePoint, instructions.get(convergencePointIndex + 1), InstructionEdgeType.FOLLOWED_BY));
            } else {
                edges.add(new InstructionEdge(current, next, InstructionEdgeType.FOLLOWED_BY));
            }
        }

        GraphSDK graphSDK = new GraphSDK(neo4JDriverBuilder.fromEnv());
        NodeSpecBuilder specBuilder = new NodeSpecBuilder(new NamespaceQualifier("SOME"));
        nodes.forEach(n -> graphSDK.createNode(NodeToWoof.toWoofNode(n, specBuilder)));
        edges.forEach(e -> {
            PseudocodeInstruction from = e.getFrom();
            PseudocodeInstruction to = e.getTo();
            Record recordFrom = NodeToWoof.existingCFGNode(from, specBuilder, graphSDK);
            Record recordTo = NodeToWoof.existingCFGNode(to, specBuilder, graphSDK);
            graphSDK.connect(recordFrom, recordTo, e.getEdgeType().name(), EdgeType.FLOW);
        });

        return new AnalysisTaskResultOK(CommandLineAnalysisTask.FLATTEN_FLOW_AST.name());
    }
}
