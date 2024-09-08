package org.smojol.toolkit.interpreter.interpreter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.lsp.cobol.core.CobolParser;
import org.smojol.common.vm.expression.CobolExpression;
import org.smojol.common.vm.reference.CobolReference;
import org.smojol.toolkit.ast.DivideFlowNode;
import org.smojol.common.vm.structure.CobolDataStructure;
import org.smojol.common.vm.reference.DeepReferenceBuilder;
import org.smojol.common.vm.structure.CobolOperation;

import java.util.List;

import static com.google.common.collect.Streams.zip;

public class DivideOperation implements CobolOperation {
    private final DivideFlowNode divide;

    public DivideOperation(DivideFlowNode divide) {
        this.divide = divide;
    }

    public void run(CobolDataStructure cobolDataStructure) {
//        CobolParser.DivisorContext divisor = divide.getIntoDivisor();
//        List<CobolParser.DivideIntoContext> dividends = divide.getDividends();
        DeepReferenceBuilder builder = new DeepReferenceBuilder();
        List<CobolExpression> quotients = divide.getDividendExpressions().stream().map(dividend -> dividend.evaluate(cobolDataStructure).divide(divide.getDivisorExpression(), cobolDataStructure)).toList();
        List<CobolExpression> destinations = divide.getDestinationExpression();
        if (quotients.size() == 1)
            destinations.forEach(dst -> builder.getReference(dst, cobolDataStructure).set(builder.getReference(quotients.getFirst(), cobolDataStructure)));
        else {
            List<Pair<CobolReference, CobolReference>> srcDestPairs = zip(quotients.stream(), destinations.stream(), (src, dest) -> (Pair<CobolReference, CobolReference>) ImmutablePair.of(builder.getReference(src, cobolDataStructure), builder.getReference(dest, cobolDataStructure))).toList();
            srcDestPairs.forEach(p -> p.getRight().set(p.getLeft()));
        }
//        dividends.forEach(dividend -> builder.getReference(dividend.generalIdentifier(), cobolDataStructure).resolve().divide(builder.getReference(divisor, cobolDataStructure)));
//        dividends.forEach(dividend -> cobolDataStructure.divide(dividend.generalIdentifier().getText(), builder.getReference(divisor, cobolDataStructure)));
    }
}
