package org.smojol.common.vm.expression;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.smojol.common.vm.structure.CobolDataStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CobolExpression {
    @Getter protected final List<CobolExpression> children = new ArrayList<>();
    @Getter private final String id;

    public CobolExpression() {
        this(ImmutableList.of());
    }

    public CobolExpression(List<CobolExpression> children) {
        this.id = UUID.randomUUID().toString();
        this.children.addAll(children);
    }

    public abstract CobolExpression evaluate(CobolDataStructure data);
    public CobolDataStructure reference(CobolDataStructure data) {
        throw new UnsupportedOperationException("Cannot resolve to references of intermediate expressions");
    }

    public double evalAsNumber(CobolDataStructure data) {
        return evaluate(data).evalAsNumber(data);
    }

    public boolean evalAsBoolean(CobolDataStructure data) {
        return evaluate(data).evalAsBoolean(data);
    }
    public String evalAsString(CobolDataStructure data) {
        return evaluate(data).evalAsString(data);
    }

    public CobolExpression equalTo(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).equalTo(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression lessThan(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).lessThan(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression greaterThan(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).greaterThan(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression lessThanOrEqualTo(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).lessThanOrEqualTo(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression greaterThanOrEqualTo(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).greaterThanOrEqualTo(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression add(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).add(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression and(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).and(other.evaluate(dataStructures), dataStructures);
    }

    public CobolExpression or(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).or(other.evaluate(dataStructures), dataStructures);
    }

    protected CobolExpression subtract(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).subtract(other.evaluate(dataStructures), dataStructures);
    }

    protected CobolExpression divide(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).divide(other.evaluate(dataStructures), dataStructures);
    }

    protected CobolExpression multiply(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).multiply(other.evaluate(dataStructures), dataStructures);
    }

    protected CobolExpression exponent(CobolExpression other, CobolDataStructure dataStructures) {
        return evaluate(dataStructures).exponent(other.evaluate(dataStructures), dataStructures);
    }

    protected CobolExpression negative(CobolDataStructure dataStructures) {
        return evaluate(dataStructures).negative(dataStructures);
    }

    protected CobolExpression not(CobolDataStructure dataStructures) {
        return evaluate(dataStructures).not(dataStructures);
    }

    public void accept(CobolExpressionVisitor visitor) {
        CobolExpressionVisitor scopedVisitor = visitor.visit(this);
        children.forEach(child -> child.accept(scopedVisitor));
    }

    public void acceptDepthFirst(CobolExpressionVisitor visitor) {
        CobolExpressionVisitor scopedVisitor = visitor.visit(this);
        children.forEach(child -> child.acceptDepthFirst(scopedVisitor));
    }
}
