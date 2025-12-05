package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnumOption<T extends Enum<T>> extends AbstractOption<T> {
    private T value;
    private final T[] values;
    private final List<T> unmodifiableModes;

    public EnumOption(String id, String name, String description, T defaultValue) {
        super(id, name, description, defaultValue);
        this.value = defaultValue;
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
        this.unmodifiableModes = Collections.unmodifiableList(Arrays.asList(values));

        if (values.length < 2) {
            throw new IllegalArgumentException("Enum must have at least 2 values for cycling");
        }
    }

    @Override
    public T get() { return value; }

    @Override
    protected void setValueInternal(T newValue) {
        if (this.value == newValue) return;
        this.value = newValue;
        notifyListeners(newValue);
    }

    public void next() {
        int nextIndex = (value.ordinal() + 1) % values.length;
        setValueInternal(values[nextIndex]);
    }

    public void previous() {
        int prevIndex = (value.ordinal() - 1 + values.length) % values.length;
        setValueInternal(values[prevIndex]);
    }

    public boolean is(T variant) {
        return value == variant;
    }

    public List<T> getModes() {
        return unmodifiableModes;
    }
}
