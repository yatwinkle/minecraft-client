package yatwinkle.client.service.setting.impl;

import java.util.function.BooleanSupplier;
import yatwinkle.client.service.setting.AbstractOption;

public class BoolOption extends AbstractOption<Boolean> implements BooleanSupplier {
    private boolean value;

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

    @Override
    public void setValueInternal(Boolean newValue) {
        boolean val = newValue;
        if (this.value == val) return;

        this.value = val;
        notifyListeners(val);
    }

    public void toggle() {
        setValueInternal(!value);
    }
}
