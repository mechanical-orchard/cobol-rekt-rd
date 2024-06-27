package org.smojol.ast;

import org.antlr.v4.runtime.tree.ParseTree;
import org.smojol.common.flowchart.FlowNode;
import org.smojol.common.flowchart.FlowNodeService;
import org.smojol.common.flowchart.FlowNodeType;
import org.smojol.common.vm.interpreter.CobolInterpreter;
import org.smojol.common.vm.interpreter.CobolVmSignal;
import org.smojol.common.vm.interpreter.FlowControl;
import org.smojol.common.vm.stack.StackFrames;

public class SectionFlowNode extends CompositeCobolFlowNode {
    @Override
    public FlowNodeType type() {
        return FlowNodeType.SECTION;
    }

    public SectionFlowNode(ParseTree parseTree, FlowNode scope, FlowNodeService nodeService, StackFrames stackFrames) {
        super(parseTree, scope, nodeService, stackFrames);
    }

    public CobolVmSignal acceptInterpreter(CobolInterpreter interpreter, FlowControl forwardFlowControl) {
        CobolVmSignal signal = executeInternalRoot(interpreter, nodeService);
        if (signal == CobolVmSignal.EXIT_SCOPE)
            return forwardFlowControl.apply(() -> continueOrAbort(signal, interpreter, nodeService), CobolVmSignal.CONTINUE);
        return forwardFlowControl.apply(() -> continueOrAbort(signal, interpreter, nodeService), signal);
    }

    @Override
    protected CobolVmSignal continueOrAbort(CobolVmSignal defaultSignal, CobolInterpreter interpreter, FlowNodeService nodeService) {
        if (defaultSignal == CobolVmSignal.TERMINATE || defaultSignal == CobolVmSignal.EXIT_PERFORM) return defaultSignal;
        return next(defaultSignal, interpreter, nodeService);
    }
}
