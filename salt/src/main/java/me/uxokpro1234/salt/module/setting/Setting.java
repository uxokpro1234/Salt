package me.uxokpro1234.salt.module.setting;

@SuppressWarnings("unchecked")
public class Setting<T> {

    private final String name;
    private T value;

    private final T min;
    private final T max;

    private final Class<? extends Enum<?>> enumClass;

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
        this.min = null;
        this.max = null;

        if (value instanceof Enum) {
            this.enumClass = ((Enum<?>) value).getDeclaringClass();
        } else {
            this.enumClass = null;
        }
    }

    public Setting(String name, T value, T min, T max) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.enumClass = null;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setEnumValue(String name) {
        if (!(value instanceof Enum)) return;

        Enum<?>[] constants = (Enum<?>[]) ((Enum) value).getDeclaringClass().getEnumConstants();
        for (Enum<?> constant : constants) {
            if (constant.name().equalsIgnoreCase(name)) {
                this.value = (T) constant;
                return;
            }
        }
    }

    public void setValue(T value) {
        if (value instanceof Number && min != null && max != null) {
            double val = ((Number) value).doubleValue();
            double minVal = ((Number) min).doubleValue();
            double maxVal = ((Number) max).doubleValue();

            val = Math.max(minVal, Math.min(maxVal, val));

            if (this.value instanceof Integer)
                this.value = (T) Integer.valueOf((int) val);
            else if (this.value instanceof Float)
                this.value = (T) Float.valueOf((float) val);
            else if (this.value instanceof Double)
                this.value = (T) Double.valueOf(val);

        } else {
            this.value = value;
        }
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isEnum() {
        return enumClass != null;
    }

    public void cycleEnum() {
        if (!isEnum()) return;

        Enum<?>[] values = enumClass.getEnumConstants();
        Enum<?> current = (Enum<?>) value;

        int next = (current.ordinal() + 1) % values.length;
        value = (T) values[next];
    }

    public void cycleEnumBackwards() {
        if (!isEnum()) return;

        Enum<?>[] values = enumClass.getEnumConstants();
        Enum<?> current = (Enum<?>) value;

        int prev = current.ordinal() - 1;
        if (prev < 0) prev = values.length - 1;

        value = (T) values[prev];
    }
}