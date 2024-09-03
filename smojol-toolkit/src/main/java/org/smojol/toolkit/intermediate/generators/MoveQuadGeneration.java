package org.smojol.toolkit.intermediate.generators;

import com.google.common.collect.ImmutableList;
import org.smojol.common.pseudocode.*;
import org.smojol.toolkit.ast.MoveFlowNode;

import java.util.List;

public class MoveQuadGeneration {
    private final PseudocodeGraph graph;
    private final SmojolSymbolTable symbolTable;
    private final SymbolReferenceBuilder symbolReferenceBuilder;

    public MoveQuadGeneration(PseudocodeGraph graph, SmojolSymbolTable symbolTable, SymbolReferenceBuilder symbolReferenceBuilder) {
        this.graph = graph;
        this.symbolTable = symbolTable;
        this.symbolReferenceBuilder = symbolReferenceBuilder;
    }

    public QuadSequence run(MoveFlowNode n) {
        GeneralIdentifierQuadGeneration generalIdentifierQuadGeneration = new GeneralIdentifierQuadGeneration(graph, symbolTable, symbolReferenceBuilder);
        QuadSequence fromSequence = buildFromSequence(n);
        List<QuadSequence> toSequences = n.getTos().stream().map(generalIdentifierQuadGeneration::run).toList();
        List<InstructionQuad> assignmentInstructions = toSequences.stream().map(t -> new InstructionQuad(t.lastResult(), AbstractOperator.ASSIGNMENT, fromSequence.lastResult())).toList();
        QuadSequence moveSequence = new QuadSequence(symbolTable);
        moveSequence.add(fromSequence);
        toSequences.forEach(moveSequence::add);
        moveSequence.add(new QuadSequence(symbolTable, assignmentInstructions));
        return moveSequence;
    }

    private QuadSequence buildFromSequence(MoveFlowNode n) {
        if (n.getFrom().literal() != null) return new QuadSequence(symbolTable, ImmutableList.of(new InstructionQuad(symbolReferenceBuilder.intermediateSymbolReference(), AbstractOperator.ASSIGNMENT, symbolReferenceBuilder.literalReference(n.getFrom().literal()))));
        GeneralIdentifierQuadGeneration generalIdentifierQuadGeneration = new GeneralIdentifierQuadGeneration(graph, symbolTable, symbolReferenceBuilder);
        return generalIdentifierQuadGeneration.run(n.getFrom().generalIdentifier());
    }
}
