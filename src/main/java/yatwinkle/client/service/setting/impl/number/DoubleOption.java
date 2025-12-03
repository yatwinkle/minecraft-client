package yatwinkle.client.service.setting.impl.number;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class DoubleOption extends NumberOption<Double> implements DoubleSupplier {

    private final double min, max, step, inverseStep;
    private volatile double value;
    private Object doubleListeners;

    public DoubleOption(String id, String name, String description, double defaultValue, double min, double max, double step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.inverseStep = step > 0 ? 1.0 / step : 0.0;
        this.value = defaultValue;
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public Double get() {
        return value;
    }

    @Override
    public Double getMin() {
        return min;
    }

    @Override
    public Double getMax() {
        return max;
    }

    public void set(double newValue) {
        if (newValue > max) newValue = max;
        else if (newValue < min) newValue = min;

        if (step > 0) {
            newValue = min + Math.round((newValue - min) * inverseStep) / inverseStep;
        }

        if (Double.compare(this.value, newValue) == 0) return;

        this.value = newValue;

        notifyDoubleListeners(newValue);
        listenerSupport.notify(newValue);
    }

    public DoubleOption onDoubleChange(DoubleConsumer listener) {
        if (this.doubleListeners == null) {
            this.doubleListeners = listener;
        } else if (this.doubleListeners instanceof DoubleConsumer) {
            List<DoubleConsumer> list = new ArrayList<>(2);
            list.add((DoubleConsumer) this.doubleListeners);
            list.add(listener);
            this.doubleListeners = list;
        } else {
            @SuppressWarnings("unchecked")
            List<DoubleConsumer> list = (List<DoubleConsumer>) this.doubleListeners;
            list.add(listener);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private void notifyDoubleListeners(double val) {
        if (doubleListeners == null) return;

        if (doubleListeners instanceof DoubleConsumer) {
            ((DoubleConsumer) doubleListeners).accept(val);
        } else {
            List<DoubleConsumer> list = (List<DoubleConsumer>) doubleListeners;
            for (DoubleConsumer doubleConsumer : list) {
                doubleConsumer.accept(val);
            }
        }
    }

    @Override
    protected void setValueInternal(Double value) {
        set(value.doubleValue());
    }
}