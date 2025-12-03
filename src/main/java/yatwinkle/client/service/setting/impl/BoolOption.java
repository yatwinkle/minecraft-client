package yatwinkle.client.service.setting.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import yatwinkle.client.service.setting.AbstractOption;

public class BoolOption extends AbstractOption<Boolean> implements BooleanSupplier {

    private final AtomicBoolean value;

    public BoolOption(String id, String name, String description, boolean defaultValue) {
        super(id, name, description, defaultValue);
        this.value = new AtomicBoolean(defaultValue);
    }

    @Override
    public boolean getAsBoolean() { return value.get(); }

    @Override
    public Boolean get() { return value.get(); }

    @Override
    protected void setValueInternal(Boolean newValue) {
        boolean oldValue = value.getAndSet(newValue);
        if (oldValue != newValue) {
            notifyListeners(newValue);
        }
    }

    public void toggle() {
        boolean prev, next;
        do {
            prev = value.get();
            next = !prev;
        } while (!value.compareAndSet(prev, next));
        notifyListeners(next);
    }

}
