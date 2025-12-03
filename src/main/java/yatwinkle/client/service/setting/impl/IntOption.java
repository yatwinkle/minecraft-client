package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntOption extends AbstractOption<Integer> implements IntSupplier {

    private final int min, max, step;
    private final AtomicInteger value;
    private final List<IntConsumer> intListeners = new CopyOnWriteArrayList<>();

    public IntOption(String id, String name, String description, int defaultValue, int min, int max, int step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = new AtomicInteger(clamp(defaultValue));
    }

    @Override public int getAsInt() { return value.get(); }
    @Override public Integer get() { return value.get(); }
    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getStep() { return step; }

    @Override
    protected void setValueInternal(Integer newValue) {
        int finalValue = clamp(newValue);
        int oldValue = value.getAndSet(finalValue);
        if (oldValue != finalValue) {
            for (IntConsumer listener : intListeners) {
                listener.accept(finalValue);
            }

            notifyListeners(finalValue);
        }
    }

    public IntOption onIntChange(IntConsumer listener) {
        intListeners.add(listener);
        return this;
    }

    private int clamp(int val) {
        if (step > 0) {
            int steps = Math.round((float) (val - min) / step);
            val = min + steps * step;
        }
        return Math.min(max, Math.max(min, val));
    }
}
