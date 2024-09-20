package org.smojol.common.transpiler;

import com.google.common.collect.ImmutableList;
import org.smojol.common.ast.SemanticCategory;

public class NegativeNode extends TranspilerNode {
    private final TranspilerNode expression;

    public NegativeNode(TranspilerNode expression) {
        super(ImmutableList.of(SemanticCategory.COMPUTATIONAL));
        this.expression = expression;
    }

    @Override
    public String description() {
        return String.format("negative(%s)", expression.description());
    }
}
