package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class MultiEnumOption<T extends Enum<T>> extends AbstractOption<Set<T>> {

    private volatile Set<T> view;
    private final Class<T> enumClass;

    public MultiEnumOption(String id, String name, String description, Class<T> enumClass) {
        super(id, name, description, Collections.emptySet());
        this.enumClass = enumClass;
        this.view = Collections.unmodifiableSet(EnumSet.noneOf(enumClass));
    }

    @SafeVarargs
    public MultiEnumOption(String id, String name, String description, T... defaultValues) {
        this(id, name, description, getEnumClass(defaultValues));
        if (defaultValues.length > 0) {
            Set<T> initial = EnumSet.noneOf(enumClass);
            Collections.addAll(initial, defaultValues);
            this.view = Collections.unmodifiableSet(initial);
        }
    }

    @SafeVarargs
    private static <E extends Enum<E>> Class<E> getEnumClass(E... values) {
        if (values.length == 0) throw new IllegalArgumentException("Values cannot be empty");
        return values[0].getDeclaringClass();
    }

    @Override
    public Set<T> get() {
        return view;
    }

    public boolean is(T variant) {
        return view.contains(variant);
    }

    @Override
    protected void setValueInternal(Set<T> value) {
        synchronized (this) {
            Set<T> copy = value.isEmpty() ? EnumSet.noneOf(enumClass) : EnumSet.copyOf(value);
            this.view = Collections.unmodifiableSet(copy);
        }
    }

    public synchronized void toggle(T variant) {
        Set<T> copy = EnumSet.noneOf(enumClass);
        copy.addAll(view);

        if (copy.contains(variant)) {
            copy.remove(variant);
        } else {
            copy.add(variant);
        }

        this.view = Collections.unmodifiableSet(copy);

        listenerSupport.notify(this.view);
    }
}
