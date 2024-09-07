package org.smojol.common.vm.type;

import java.util.Objects;
import java.util.function.Supplier;

// TODO: Might need polymorphic types at some point, addition of two strings, for example
public final class TypedRecord {
    public static final TypedRecord NULL = new TypedRecord("NULL", CobolDataType.NULL);
    public static final TypedRecord TRUE = new TypedRecord(true, CobolDataType.BOOLEAN);
    public static final TypedRecord FALSE = new TypedRecord(false, CobolDataType.BOOLEAN);
    private final Object value;
    private final CobolDataType dataType;

    public TypedRecord(Object value, CobolDataType dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    private final ValueBasedComparator valueBasedComparator = new ValueBasedComparator();

    public static TypedRecord demo(CobolDataType cobolDataType) {
        if (cobolDataType == CobolDataType.STRING) return TypedRecord.typedString("SOME DEMO");
        else if (cobolDataType == CobolDataType.NUMBER) return TypedRecord.typedNumber(250.);
        else if (cobolDataType == CobolDataType.BOOLEAN) return TypedRecord.TRUE;
        else if (cobolDataType == CobolDataType.GROUP) return TypedRecord.NULL;
        throw new IllegalArgumentException("This is not a valid data type to generate data for: " + cobolDataType);
    }

    public static TypedRecord typedNumber(String text) {
        return typedNumber(Double.parseDouble(text));
    }

    public static TypedRecord typedBoolean(boolean b) {
        return b ? TRUE : FALSE;
    }

    public static TypedRecord typedString(String text) {
        return new TypedRecord(text, CobolDataType.STRING);
    }

    public static TypedRecord pointer(long address) {
        return new TypedRecord(address, CobolDataType.POINTER);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TypedRecord other = (TypedRecord) obj;
        return (this.dataType == other.dataType && this.value.equals(other.value));
    }

    public Double asNumber() {
        if (dataType == CobolDataType.NUMBER) return (Double) value;
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    public Boolean asBoolean() {
        if (dataType == CobolDataType.BOOLEAN) return (Boolean) value;
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    public String asString() {
        if (dataType == CobolDataType.STRING) return (String) value;
        throw new IllegalArgumentException("Unsupported data type: " + dataType);
    }

    public TypedRecord equalTo(TypedRecord other) {
        return ifCompatible(other, () -> valueBasedComparator.equalTo(value, other.value, dataType));
    }

    public TypedRecord greaterThan(TypedRecord other) {
        return ifCompatible(other, () -> valueBasedComparator.greaterThan(value, other.value, dataType));
    }

    public TypedRecord lessThan(TypedRecord other) {
        return ifCompatible(other, () -> valueBasedComparator.lessThan(value, other.value, dataType));
    }

    public TypedRecord lessThanOrEqualTo(TypedRecord other) {
        return ifCompatible(other, () -> valueBasedComparator.lessThanOrEqualTo(value, other.value, dataType));
    }

    public TypedRecord greaterThanOrEqualTo(TypedRecord other) {
        return ifCompatible(other, () -> valueBasedComparator.greaterThanOrEqualTo(value, other.value, dataType));
    }

    public TypedRecord and(TypedRecord other) {
        return ifCompatible(other, () -> typedBoolean((Boolean) value && (Boolean) other.value));
    }

    public TypedRecord or(TypedRecord other) {
        return ifCompatible(other, () -> typedBoolean((Boolean) value || (Boolean) other.value));
    }

    public TypedRecord subtract(TypedRecord other) {
        return ifCompatible(other, () -> typedNumber((Double) value - (Double) other.value));
    }

    public static TypedRecord typedNumber(double v) {
        return new TypedRecord(v, CobolDataType.NUMBER);
    }

    public TypedRecord divide(TypedRecord other) {
        return ifCompatible(other, () -> typedNumber((Double) value / (Double) other.value));
    }

    public TypedRecord multiply(TypedRecord other) {
        return ifCompatible(other, () -> typedNumber((Double) value * (Double) other.value));
    }

    public TypedRecord exponent(TypedRecord other) {
        return ifCompatible(other, () -> typedNumber(Math.pow((Double) value, (Double) other.value)));
    }

    public TypedRecord negative() {
        return typedNumber(-(Double) value);
    }

    public TypedRecord not() {
        return typedBoolean(!((Boolean) value));
    }

    public TypedRecord add(TypedRecord other) {
        return ifCompatible(other, () -> typedNumber((Double) value + (Double) other.value));
    }

    private TypedRecord ifCompatible(TypedRecord other, Supplier<TypedRecord> operation) {
        if (!isCompatibleWith(other))
            throw new ClassCastException(String.format("Incompatible types found: %s and %s", dataType, other.dataType));
        return operation.get();
    }

    public boolean isCompatibleWith(TypedRecord other) {
        return true;
//        return dataType == other.dataType;
//        return dataType == other.dataType ||
//                dataType == CobolDataType.STRING && other.dataType == CobolDataType.NUMBER;
    }

    public Object value() {
        return value;
    }

    public CobolDataType dataType() {
        return dataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, dataType);
    }

    @Override
    public String toString() {
        return "TypedRecord["
                + "value=" + value + ", "
                + "dataType=" + dataType + ']';
    }

}
