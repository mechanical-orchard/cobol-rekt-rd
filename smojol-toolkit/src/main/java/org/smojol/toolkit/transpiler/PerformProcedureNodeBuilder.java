package org.smojol.toolkit.transpiler;

import org.smojol.common.id.UUIDProvider;
import org.smojol.common.pseudocode.CodeSentinelType;
import org.smojol.common.pseudocode.PseudocodeInstruction;
import org.smojol.common.transpiler.TranspilerNode;
import org.smojol.common.vm.expression.FlowIteration;
import org.smojol.common.vm.structure.CobolDataStructure;
import org.smojol.toolkit.analysis.defined.BuildPseudocodeTask;
import org.smojol.toolkit.ast.PerformInlineFlowNode;
import org.smojol.toolkit.ast.PerformProcedureFlowNode;

import java.util.List;

public class PerformProcedureNodeBuilder {
    public static TranspilerNode build(PerformProcedureFlowNode n, CobolDataStructure dataStructures) {
        List<FlowIteration> nestedLoops = n.getNestedLoops();
        return recurse(nestedLoops, body(n), dataStructures);
    }

    private static TranspilerNode recurse(List<FlowIteration> loops, TranspilerNode body, CobolDataStructure dataStructures) {
        if (loops.isEmpty()) return body;
        if (loops.size() == 1) return toTranspilerLoop(loops.getFirst(), body, dataStructures);
        return toTranspilerLoop(loops.getFirst(), recurse(loops.subList(1, loops.size()), body, dataStructures), dataStructures);
    }

    private static TranspilerNode toTranspilerLoop(FlowIteration loop, TranspilerNode body, CobolDataStructure dataStructures) {
        TranspilerExpressionBuilder expressionBuilder = new TranspilerExpressionBuilder(dataStructures);
        return new TranspilerLoop(expressionBuilder.build(loop.loopVariable()),
                expressionBuilder.build(loop.initialValue()),
                expressionBuilder.build(loop.maxValue()),
                expressionBuilder.build(loop.condition()),
                new TranspilerLoopUpdate(expressionBuilder.build(loop.loopUpdate().updateDelta())),
                loop.conditionTestTime(), body
        );
    }

    public static TranspilerNode build(PerformInlineFlowNode n, CobolDataStructure dataStructures) {
        List<FlowIteration> nestedLoops = n.getNestedLoops();
        return recurse(nestedLoops, body(n, dataStructures), dataStructures);
    }

    private static TranspilerNode body(PerformInlineFlowNode n, CobolDataStructure dataStructures) {
        List<PseudocodeInstruction> instructions = new BuildPseudocodeTask(n, new UUIDProvider()).run();
        return new TranspilerCodeBlock(instructions.stream().map(instr -> TranspilerTreeBuilder.flowToTranspiler(instr.getNode(), dataStructures, CodeSentinelType.BODY)).toList());
    }

    private static TranspilerNode body(PerformProcedureFlowNode n) {
        return new TranspilerCodeBlock();
    }
}
