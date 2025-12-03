package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class DoubleOption extends AbstractOption<Double> implements DoubleSupplier {

    private final double min, max, step;
    private final AtomicLong valueBits;
    private final List<DoubleConsumer> doubleListeners = new CopyOnWriteArrayList<>();

    public DoubleOption(String id, String name, String description, double defaultValue, double min, double max, double step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.valueBits = new AtomicLong(Double.doubleToRawLongBits(clamp(defaultValue)));
    }

    @Override public double getAsDouble() { return Double.longBitsToDouble(valueBits.get()); }
    @Override public Double get() { return getAsDouble(); }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    @Override
    protected void setValueInternal(Double newValue) {
        double finalValue = clamp(newValue);
        long newBits = Double.doubleToRawLongBits(finalValue);
        long oldBits = valueBits.getAndSet(newBits);
        if (oldBits != newBits) {
            for (DoubleConsumer listener : doubleListeners) {
                listener.accept(finalValue);
            }

            notifyListeners(finalValue);
        }
    }

    public DoubleOption onDoubleChange(DoubleConsumer listener) {
        doubleListeners.add(listener);
        return this;
    }

    private double clamp(double val) {
        if (step > 0) {
            val = Math.round((val - min) / step) * step + min;
        }
        return Math.min(max, Math.max(min, val));
    }
}
