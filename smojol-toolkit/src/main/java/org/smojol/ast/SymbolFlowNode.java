package org.smojol.ast;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.ParseTree;
import org.smojol.common.ast.FlowNode;
import org.smojol.common.ast.FlowNodeCategory;
import org.smojol.common.ast.FlowNodeService;
import org.smojol.common.ast.FlowNodeType;
import org.smojol.common.vm.stack.StackFrames;

import java.util.List;

public class SymbolFlowNode extends CobolFlowNode {
    public SymbolFlowNode(ParseTree parseTree, FlowNode scope, FlowNodeService nodeService, StackFrames stackFrames) {
        super(parseTree, scope, nodeService, stackFrames);
    }

    @Override
    public FlowNodeType type() {
        return FlowNodeType.SYMBOL;
    }

    @Override
    public boolean isPassthrough() {
        return true;
    }

    @Override
    public FlowNode passthrough() {
        return outgoingNodes.getFirst();
    }

    @Override
    public List<FlowNodeCategory> categories() {
        return ImmutableList.of(FlowNodeCategory.SYMBOL);
    }
}
