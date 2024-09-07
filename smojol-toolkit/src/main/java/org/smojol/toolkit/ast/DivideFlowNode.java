package org.smojol.toolkit.ast;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp.cobol.core.CobolParser;
import org.smojol.common.ast.*;
import org.smojol.common.pseudocode.SmojolSymbolTable;
import org.smojol.common.vm.expression.CobolExpression;
import org.smojol.common.vm.expression.CobolExpressionBuilder;
import org.smojol.common.vm.interpreter.CobolInterpreter;
import org.smojol.common.vm.interpreter.CobolVmSignal;
import org.smojol.common.vm.interpreter.FlowControl;
import org.smojol.common.vm.stack.StackFrames;

import java.util.List;

@Getter
public class DivideFlowNode extends CobolFlowNode {
    private CobolParser.DivisorContext intoDivisor;
    private List<CobolParser.DivideIntoContext> dividends;
    private CobolParser.DivisorContext givingDividend;
    private CobolExpression divisorExpression;
    private List<CobolExpression> dividendExpressions;

    public DivideFlowNode(ParseTree parseTree, FlowNode scope, FlowNodeService nodeService, StackFrames stackFrames) {
        super(parseTree, scope, nodeService, stackFrames);
    }

    @Override
    public void buildInternalFlow() {
        CobolParser.DivideStatementContext divideStatement = new SyntaxIdentity<CobolParser.DivideStatementContext>(executionContext).get();
        CobolExpressionBuilder expressionBuilder = new CobolExpressionBuilder();
        if (divideStatement.divideIntoStatement() != null) {
            intoDivisor = divideStatement.divisor();
            dividends = divideStatement.divideIntoStatement().divideInto();
        }
        else if (divideStatement.divideByGivingStatement() != null) {
            intoDivisor = divideStatement.divideByGivingStatement().divisor();
            givingDividend = divideStatement.divisor();
        }
        if (dividends == null) {
            System.out.println(executionContext.getText());
            throw new RuntimeException("STOP");
        }
        super.buildInternalFlow();
    }

    @Override
    public FlowNodeType type() {
        return FlowNodeType.DIVIDE;
    }

    @Override
    public CobolVmSignal acceptInterpreter(CobolInterpreter interpreter, FlowControl flowControl) {
        CobolVmSignal signal = interpreter.scope(this).executeDivide(this, nodeService);
        return flowControl.apply(() -> continueOrAbort(signal, interpreter, nodeService), signal);
    }

    @Override
    public boolean isMergeable() {
        return true;
    }

    @Override
    public List<FlowNodeCategory> categories() {
        return ImmutableList.of(FlowNodeCategory.COMPUTATIONAL, FlowNodeCategory.DATA_FLOW);
    }

    @Override
    public void resolve(SmojolSymbolTable symbolTable) {
        CobolExpressionBuilder builder = new CobolExpressionBuilder();
        divisorExpression = builder.literalOrIdentifier(intoDivisor.literal(), intoDivisor.generalIdentifier());
        dividendExpressions = dividends != null ? dividends.stream().map(dividend -> builder.identifier(dividend.generalIdentifier())).toList()
                : ImmutableList.of(builder.literalOrIdentifier(givingDividend.literal(), givingDividend.generalIdentifier()));
    }
}
