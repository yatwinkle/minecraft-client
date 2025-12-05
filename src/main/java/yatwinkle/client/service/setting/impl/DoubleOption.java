package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class DoubleOption extends AbstractOption<Double> implements DoubleSupplier {
    private final double min, max, step;
    private double value;
    private final List<DoubleConsumer> doubleListeners = new ArrayList<>();

    public DoubleOption(String id, String name, String description, double defaultValue, double min, double max, double step) {
        super(id, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.value = clamp(defaultValue);
    }

    @Override
    public double getAsDouble() { return value; }

    @Override
    public Double get() { return value; }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    @Override
    protected void setValueInternal(Double newValue) {
        double finalValue = clamp(newValue);
        if (Double.compare(this.value, finalValue) == 0) return;

        this.value = finalValue;

        if (!doubleListeners.isEmpty()) {
            for (int i = doubleListeners.size() - 1; i >= 0; i--) {
                doubleListeners.get(i).accept(finalValue);
            }
        }

        notifyListeners(finalValue);
    }


    public DoubleOption onDoubleChange(DoubleConsumer listener) {
        doubleListeners.add(listener);
        return this;
    }

    private double clamp(double val) {
        if (step > 0) {
            val = min + Math.round((val - min) / step) * step;
        }
        return Math.min(max, Math.max(min, val));
    }
}
