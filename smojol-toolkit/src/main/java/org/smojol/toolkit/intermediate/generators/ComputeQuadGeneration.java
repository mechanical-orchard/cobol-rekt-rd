package org.smojol.toolkit.intermediate.generators;

import org.smojol.common.pseudocode.*;
import org.smojol.common.vm.expression.ArithmeticExpressionVisitor;
import org.smojol.common.vm.expression.CobolExpression;
import org.smojol.toolkit.ast.ComputeFlowNode;

import java.util.List;

public class ComputeQuadGeneration extends QuadGeneration {
    public ComputeQuadGeneration(PseudocodeGraph graph, SmojolSymbolTable symbolTable, SymbolReferenceBuilder symbolReferenceBuilder) {
        super(graph, symbolTable, symbolReferenceBuilder);
    }

    @Override
    public QuadSequence body(PseudocodeInstruction instruction) {
        ComputeFlowNode n = instruction.typedNode(ComputeFlowNode.class);
        ArithmeticExpressionVisitor arithmeticExpressionVisitor = new ArithmeticExpressionVisitor();
        n.getRhs().accept(arithmeticExpressionVisitor);
        CobolExpression expression = arithmeticExpressionVisitor.getExpression();
        ExpressionQuadGenerator generator = new ExpressionQuadGenerator(symbolTable, symbolReferenceBuilder);
        SymbolReference rhsReference = generator.build(expression);
        QuadSequence fromSequence = generator.getQuads();
        GeneralIdentifierQuadGeneration generalIdentifierQuadGeneration = new GeneralIdentifierQuadGeneration(graph, symbolTable, symbolReferenceBuilder);
        List<QuadSequence> toSequences = n.getDestinations().stream().map(d -> generalIdentifierQuadGeneration.run(d.generalIdentifier())).toList();
        List<InstructionQuad> assignmentInstructions = toSequences.stream().map(t -> new InstructionQuad(t.lastResult(), AbstractOperator.ASSIGNMENT, rhsReference)).toList();
        QuadSequence moveSequence = new QuadSequence(symbolTable);
        moveSequence.add(fromSequence);
        toSequences.forEach(moveSequence::add);
        moveSequence.add(new QuadSequence(symbolTable, assignmentInstructions));
        return moveSequence;
    }
}
