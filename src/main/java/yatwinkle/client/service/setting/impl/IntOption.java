package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntOption extends AbstractOption<Integer> implements IntSupplier {
    private final int min, max, step;
    private int value;
    private final List<IntConsumer> intListeners = new ArrayList<>();

    public IntOption(String id, String name, String description, int defaultValue, int min, int max, int step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(defaultValue);
    }

    @Override
    public int getAsInt() { return value; }

    @Override
    public Integer get() { return value; }

    public int getMin() { return min; }
    public int getMax() { return max; }
    public int getStep() { return step; }

    @Override
    protected void setValueInternal(Integer newValue) {
        int finalValue = clamp(newValue);
        if (this.value == finalValue) return;

        this.value = finalValue;

        if (!intListeners.isEmpty()) {
            for (IntConsumer listener : new ArrayList<>(intListeners)) {
                listener.accept(finalValue);
            }
        }

        notifyListeners(finalValue);
    }

    public IntOption onIntChange(IntConsumer listener) {
        intListeners.add(listener);
        return this;
    }

    private int clamp(int val) {
        if (step > 0) {
            val = min + Math.round((float) (val - min) / step) * step;
        }
        return Math.min(max, Math.max(min, val));
    }
}
