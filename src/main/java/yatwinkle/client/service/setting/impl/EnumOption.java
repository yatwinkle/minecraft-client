package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;

public class EnumOption<T extends Enum<T>> extends AbstractOption<T> {
    private T value;
    private final T[] values;

    public EnumOption(String id, String name, String description, T defaultValue) {
        super(id, name, description, defaultValue);
        this.value = defaultValue;
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    protected void setValueInternal(T value) {
        this.value = value;
    }

    public void next() {
        int index = (value.ordinal() + 1) % values.length;
        set(values[index]);
    }

    public void previous() {
        int index = (value.ordinal() - 1 + values.length) % values.length;
        set(values[index]);
    }

    public T[] getModes() {
        return values;
    }
}
