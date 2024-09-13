package org.smojol.common.transpiler;

public class LocationNode extends TranspilerNode {
    public static LocationNode END = new LocationNode("END");
    private final String name;

    public LocationNode(String name) {
        this.name = name;
    }

    @Override
    public String description() {
        return String.format("loc(%s)", name);
    }
}
