package yatwinkle.client.service.setting.impl.number;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class IntOption extends NumberOption<Integer> implements IntSupplier {

    private final int min, max, step;
    private volatile int value;
    private Object intListeners;

    public IntOption(String id, String name, String description, int defaultValue, int min, int max, int step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = defaultValue;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public Integer getMin() {
        return min;
    }

    @Override
    public Integer getMax() {
        return max;
    }

    public void set(int newValue) {
        if (step > 0) {
            long diff = (long) newValue - min;
            long steps = (diff + (step >> 1)) / step;
            newValue = min + (int) (steps * step);
        }

        if (newValue > max) newValue = max;
        else if (newValue < min) newValue = min;

        if (this.value == newValue) return;

        this.value = newValue;

        notifyIntListeners(newValue);
        listenerSupport.notify(newValue);
    }

    public IntOption onIntChange(IntConsumer listener) {
        if (this.intListeners == null) {
            this.intListeners = listener;
        } else if (this.intListeners instanceof IntConsumer) {
            List<IntConsumer> list = new ArrayList<>(2);
            list.add((IntConsumer) this.intListeners);
            list.add(listener);
            this.intListeners = list;
        } else {
            @SuppressWarnings("unchecked")
            List<IntConsumer> list = (List<IntConsumer>) this.intListeners;
            list.add(listener);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    private void notifyIntListeners(int val) {
        if (intListeners == null) return;

        if (intListeners instanceof IntConsumer) {
            ((IntConsumer) intListeners).accept(val);
        } else {
            List<IntConsumer> list = (List<IntConsumer>) intListeners;
            for (IntConsumer intConsumer : list) {
                intConsumer.accept(val);
            }
        }
    }

    @Override
    protected void setValueInternal(Integer value) {
        set(value.intValue());
    }
}