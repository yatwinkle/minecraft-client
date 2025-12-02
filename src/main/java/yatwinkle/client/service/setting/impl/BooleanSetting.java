package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractSetting;

public class BooleanSetting extends AbstractSetting<Boolean> {
    private boolean value;

    public BooleanSetting(String id, String name, String description, boolean defaultValue) {
        super(id, name, description);
        this.value = defaultValue;
    }

    @Override
    public Boolean get() {
        return value;
    }

    public boolean isActive() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        if (this.value == value) return;

        this.value = value;
    }

    public void toggle() {
        set(!value);
    }
}
