package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class MultiEnumOption<T extends Enum<T>> extends AbstractOption<Set<T>> {

    private final AtomicLong bitMask;
    private final Class<T> enumClass;
    private final T[] enumConstants;
    private final AtomicReference<Set<T>> cachedSet;

    public MultiEnumOption(String id, String name, String description, Class<T> enumClass) {
        super(id, name, description, Collections.emptySet());
        this.enumClass = enumClass;
        this.enumConstants = enumClass.getEnumConstants();
        if (enumConstants.length > 64) throw new IllegalArgumentException("Enum > 64 values not supported");
        this.bitMask = new AtomicLong(0L);
        this.cachedSet = new AtomicReference<>(Collections.emptySet());
    }

    @SafeVarargs
    public MultiEnumOption(String id, String name, String description, T... defaultValues) {
        this(id, name, description, defaultValues[0].getDeclaringClass());
        long mask = 0L;
        for (T v : defaultValues) mask |= (1L << v.ordinal());
        bitMask.set(mask);
        cachedSet.set(maskToSet(mask));
    }

    @Override public Set<T> get() { return cachedSet.get(); }

    public boolean is(T variant) {
        return (bitMask.get() & (1L << variant.ordinal())) != 0;
    }

    public Class<T> getEnumClass() {
        return enumClass;
    }

    public T[] getEnumConstants() {
        return enumConstants.clone();
    }

    @Override
    protected void setValueInternal(Set<T> value) {
        long newMask = setToMask(value);
        long oldMask = bitMask.getAndSet(newMask);
        if (oldMask != newMask) {
            Set<T> newSet = maskToSet(newMask);
            cachedSet.set(newSet);
            notifyListeners(newSet);
        }
    }

    public void toggle(T variant) {
        long m = 1L << variant.ordinal();
        long oldMask = bitMask.getAndUpdate(c -> c ^ m);
        Set<T> newSet = maskToSet(oldMask ^ m);
        cachedSet.set(newSet);
        notifyListeners(newSet);
    }

    public void enable(T variant) {
        long m = 1L << variant.ordinal();
        long oldMask = bitMask.getAndUpdate(c -> c | m);
        if ((oldMask & m) == 0) updateCache(oldMask | m);
    }

    public void disable(T variant) {
        long m = 1L << variant.ordinal();
        long oldMask = bitMask.getAndUpdate(c -> c & ~m);
        if ((oldMask & m) != 0) updateCache(oldMask & ~m);
    }

    private void updateCache(long mask) {
        Set<T> newSet = maskToSet(mask);
        cachedSet.set(newSet);
        notifyListeners(newSet);
    }

    private Set<T> maskToSet(long mask) {
        if (mask == 0L) return Collections.emptySet();
        EnumSet<T> result = EnumSet.noneOf(enumClass);
        for (int i = 0; i < enumConstants.length; i++) {
            if ((mask & (1L << i)) != 0) result.add(enumConstants[i]);
        }
        return Collections.unmodifiableSet(result);
    }

    private long setToMask(Set<T> set) {
        long mask = 0L;
        for (T v : set) mask |= (1L << v.ordinal());
        return mask;
    }
}
