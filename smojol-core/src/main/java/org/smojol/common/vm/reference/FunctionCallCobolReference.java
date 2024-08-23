package org.smojol.common.vm.reference;

import org.eclipse.lsp.cobol.core.CobolParser;
import org.smojol.common.vm.expression.ArithmeticExpressionVisitor;
import org.smojol.common.vm.expression.CobolExpression;
import org.smojol.common.vm.structure.CobolDataStructure;
import org.smojol.common.vm.type.CobolDataType;
import org.smojol.common.vm.type.TypedRecord;

import java.util.List;

public class FunctionCallCobolReference implements CobolReference {
    private final DetachedDataStructure proxyReturnValue;
    private final String functionName;
    private final List<CobolExpression> arguments;

    public FunctionCallCobolReference(CobolParser.FunctionCallContext functionCallContext) {
        functionName = functionCallContext.functionName().getText();
        arguments = functionCallContext.argument().stream().map(arg -> {
            ArithmeticExpressionVisitor arithmeticExpressionVisitor = new ArithmeticExpressionVisitor();
            arg.arithmeticExpression().accept(arithmeticExpressionVisitor);
            return arithmeticExpressionVisitor.getExpression();
        }).toList();
        proxyReturnValue = new DetachedDataStructure(TypedRecord.typedNumber(1));
    }

    @Override
    public TypedRecord resolveAs(CobolDataType type) {
        return proxyReturnValue.getValue();
    }

    @Override
    public CobolDataStructure resolve() {
        return proxyReturnValue;
    }
}
