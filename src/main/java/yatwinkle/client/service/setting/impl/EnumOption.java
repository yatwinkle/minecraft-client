package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EnumOption<T extends Enum<T>> extends AbstractOption<T> {

    private final AtomicReference<T> value;
    private final T[] values;
    private final List<T> unmodifiableModes;

    public EnumOption(String id, String name, String description, T defaultValue) {
        super(id, name, description, defaultValue);
        this.value = new AtomicReference<>(defaultValue);
        this.values = (T[]) defaultValue.getDeclaringClass().getEnumConstants();
        this.unmodifiableModes = Collections.unmodifiableList(Arrays.asList(values));

        if (values.length < 2) {
            throw new IllegalArgumentException("Enum must have at least 2 values for cycling");
        }
    }

    @Override public T get() { return value.get(); }

    @Override
    protected void setValueInternal(T newValue) {
        T oldValue = value.getAndSet(newValue);
        if (oldValue != newValue) {
            notifyListeners(newValue);
        }
    }

    public void next() {
        T newValue = value.updateAndGet(c -> values[(c.ordinal() + 1) % values.length]);
        notifyListeners(newValue);
    }

    public void previous() {
        T newValue = value.updateAndGet(c -> values[(c.ordinal() - 1 + values.length) % values.length]);
        notifyListeners(newValue);
    }

    public boolean is(T variant) { return value.get() == variant; }
    public List<T> getModes() { return unmodifiableModes; }
}
