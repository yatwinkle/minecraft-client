package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.function.BooleanSupplier;

public class BoolOption extends AbstractOption<Boolean> implements BooleanSupplier {
    private volatile boolean value;

    public BoolOption(String id, String name, String description, boolean defaultValue) {
        super(id, name, description, defaultValue);
        this.value = defaultValue;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }

    @Override
    public Boolean get() {
        return value;
    }

    public void set(boolean newValue) {
        if (this.value == newValue) return;
        this.value = newValue;
        listenerSupport.notify(newValue);
    }

    @Override
    protected void setValueInternal(Boolean value) {
        set(value.booleanValue());
    }

    public synchronized void toggle() {
        set(!value);
    }
}
