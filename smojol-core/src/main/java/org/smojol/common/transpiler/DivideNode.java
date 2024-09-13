package org.smojol.common.transpiler;

public class DivideNode extends TranspilerNode {
    private final TranspilerNode dividend;
    private final TranspilerNode divisor;

    public DivideNode(TranspilerNode dividend, TranspilerNode divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }

    @Override
    public String description() {
        return String.format("divide(%s, %s)", dividend.description(), divisor.description());
    }
}
