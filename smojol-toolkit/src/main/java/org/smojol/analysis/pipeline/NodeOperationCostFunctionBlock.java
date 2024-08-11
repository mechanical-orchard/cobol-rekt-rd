package org.smojol.analysis.pipeline;

public record NodeOperationCostFunctionBlock(
        java.util.function.ToDoubleFunction<org.smojol.analysis.graph.graphml.TypedGraphVertex> add,
        java.util.function.ToDoubleFunction<org.smojol.analysis.graph.graphml.TypedGraphVertex> remove,
        java.util.function.ToDoubleBiFunction<org.smojol.analysis.graph.graphml.TypedGraphVertex, org.smojol.analysis.graph.graphml.TypedGraphVertex> change) {

    public static NodeOperationCostFunctionBlock GENERIC = new NodeOperationCostFunctionBlock(
            v -> 5,
            v1 -> 5,
            (v2, w) -> v2.type().equals(w.type()) ? 0 : 1);
}
