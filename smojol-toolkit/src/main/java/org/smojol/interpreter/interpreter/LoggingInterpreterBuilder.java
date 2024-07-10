package org.smojol.interpreter.interpreter;

import org.smojol.common.vm.interpreter.*;

import java.util.List;

public class LoggingInterpreterBuilder implements InterpreterBuilder {
    private final ExecutionListeners listeners;
    private final List<ExecutionInterceptor> interceptors;

    public LoggingInterpreterBuilder(ExecutionListeners listeners, List<ExecutionInterceptor> interceptors) {
        this.listeners = listeners;
        this.interceptors = interceptors;
    }

    public CobolInterpreter wrap(CobolInterpreter interpreter) {
        return CobolInterpreterFactory.breakpointingInterpreter(interpreter, this.interceptors, this.listeners);
    }
}
