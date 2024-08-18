package org.smojol.ast;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.lsp.cobol.core.CobolParser;
import org.smojol.common.ast.*;
import org.smojol.common.vm.stack.StackFrames;

import java.util.List;

public class CallFlowNode extends CobolFlowNode implements ControlFlowNode {
    @Getter
    private final CallTarget callTarget;

    public CallFlowNode(ParseTree parseTree, FlowNode scope, FlowNodeService nodeService, StackFrames stackFrames) {
        super(parseTree, scope, nodeService, stackFrames);
        CobolParser.CallStatementContext callStmt = new SyntaxIdentity<CobolParser.CallStatementContext>(getExecutionContext()).get();
        callTarget = CallTarget.target(callStmt);
    }

    @Override
    public String label() {
        return originalText();
    }

    @Override
    public FlowNodeType type() {
        return FlowNodeType.CALL;
    }

    @Override
    public CallTarget callTarget() {
        return callTarget;
    }

    @Override
    public List<FlowNodeCategory> categories() {
        return ImmutableList.of(FlowNodeCategory.CONTROL_FLOW);
    }
}
