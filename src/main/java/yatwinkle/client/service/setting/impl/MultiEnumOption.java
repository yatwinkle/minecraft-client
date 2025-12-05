package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class MultiEnumOption<T extends Enum<T>> extends AbstractOption<Set<T>> {
    private long bitMask;
    private final Class<T> enumClass;
    private final T[] enumConstants;
    private Set<T> cachedSet;

    public MultiEnumOption(String id, String name, String description, Class<T> enumClass) {
        super(id, name, description, Collections.emptySet());
        this.enumClass = enumClass;
        this.enumConstants = enumClass.getEnumConstants();

        if (enumConstants.length > 64) {
            throw new IllegalArgumentException("Enum > 64 values not supported (long overflow)");
        }

        this.bitMask = 0L;
        this.cachedSet = Collections.emptySet();
    }

    @SafeVarargs
    public MultiEnumOption(String id, String name, String description, T... defaultValues) {
        this(id, name, description, defaultValues[0].getDeclaringClass());
        long mask = 0L;
        for (T v : defaultValues) {
            mask |= (1L << v.ordinal());
        }
        this.bitMask = mask;
        this.cachedSet = maskToSet(mask);
    }

    @Override
    public Set<T> get() {
        return cachedSet;
    }

    public boolean is(T variant) {
        return (bitMask & (1L << variant.ordinal())) != 0;
    }

    public Class<T> getEnumClass() {
        return enumClass;
    }

    public T[] getEnumConstants() {
        return enumConstants;
    }

    @Override
    protected void setValueInternal(Set<T> value) {
        long newMask = setToMask(value);
        if (this.bitMask == newMask) return;

        updateState(newMask);
    }

    public void toggle(T variant) {
        long mask = 1L << variant.ordinal();
        updateState(bitMask ^ mask);
    }

    public void enable(T variant) {
        long mask = 1L << variant.ordinal();
        if ((bitMask & mask) == 0) {
            updateState(bitMask | mask);
        }
    }

    public void disable(T variant) {
        long mask = 1L << variant.ordinal();
        if ((bitMask & mask) != 0) {
            updateState(bitMask & ~mask);
        }
    }

    private void updateState(long newMask) {
        this.bitMask = newMask;
        this.cachedSet = maskToSet(newMask);
        notifyListeners(cachedSet);
    }

    private Set<T> maskToSet(long mask) {
        if (mask == 0L) return Collections.emptySet();

        EnumSet<T> result = EnumSet.noneOf(enumClass);
        for (int i = 0; i < enumConstants.length; i++) {
            if ((mask & (1L << i)) != 0) {
                result.add(enumConstants[i]);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private long setToMask(Set<T> set) {
        long mask = 0L;
        for (T v : set) {
            mask |= (1L << v.ordinal());
        }
        return mask;
    }
}
