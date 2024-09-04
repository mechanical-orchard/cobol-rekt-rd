package org.smojol.toolkit.intermediate.generators;

import org.smojol.common.pseudocode.*;

public class DivideQuadGeneration extends QuadGeneration {
    public DivideQuadGeneration(PseudocodeGraph graph, SmojolSymbolTable symbolTable, SymbolReferenceBuilder symbolReferenceBuilder) {
        super(graph, symbolTable, symbolReferenceBuilder);
    }

    @Override
    public QuadSequence body(PseudocodeInstruction instruction) {
        return new QuadSequence(symbolTable);
    }
}
