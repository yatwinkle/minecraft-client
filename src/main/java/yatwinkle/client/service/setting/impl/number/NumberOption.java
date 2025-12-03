package yatwinkle.client.service.setting.impl.number;

import yatwinkle.client.service.setting.AbstractOption;

public abstract class NumberOption<T extends Number> extends AbstractOption<T> {
    public NumberOption(String id, String name, String description, T defaultValue) {
        super(id, name, description, defaultValue);
    }

    public abstract T getMin();

    public abstract T getMax();
}
