package yatwinkle.client.service.module;

import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Event;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.setting.AbstractOption;
import yatwinkle.client.service.setting.impl.BoolOption;
import yatwinkle.client.service.setting.impl.EnumOption;
import yatwinkle.client.service.setting.impl.MultiEnumOption;
import yatwinkle.client.service.setting.impl.number.DoubleOption;
import yatwinkle.client.service.setting.impl.number.IntOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module implements MinecraftLogger {

    public final String id;
    public final String displayName;
    public final Category category;
    private boolean state;
    private int key;

    private final List<AbstractOption<?>> settings = new ArrayList<>();
    private final List<Listener<?>> listeners = new ArrayList<>();

    protected <E extends Event> Listener<E> listen(Listener<E> listener) {
        listeners.add(listener);
        return listener;
    }

    public Module(String id, String displayName, Category category) {
        this(id, displayName, category, 0, false);
    }

    public Module(String id, String displayName, Category category, int key) {
        this(id, displayName, category, key, false);
    }

    public Module(String id, String displayName, Category category, int key, boolean state) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.key = key;
        this.state = state;
    }


    protected BoolOption bool(String id, String name, String description, boolean defaultValue) {
        return register(new BoolOption(id, name, description, defaultValue));
    }

    protected IntOption number(String id, String name, String description, int value, int min, int max, int step) {
        return register(new IntOption(id, name, description, value, min, max, step));
    }

    protected DoubleOption number(String id, String name, String description, double value, double min, double max, double step) {
        return register(new DoubleOption(id, name, description, value, min, max, step));
    }

    protected <T extends Enum<T>> EnumOption<T> mode(String id, String name, String description, T defaultValue) {
        return register(new EnumOption<>(id, name, description, defaultValue));
    }

    @SafeVarargs
    protected final <T extends Enum<T>> MultiEnumOption<T> multi(String id, String name, String description, T... defaultValues) {
        return register(new MultiEnumOption<>(id, name, description, defaultValues));
    }

    protected <T extends Enum<T>> MultiEnumOption<T> multi(String id, String name, String description, Class<T> enumClass) {
        return register(new MultiEnumOption<>(id, name, description, enumClass));
    }


    private <T extends AbstractOption<?>> T register(T setting) {
        settings.add(setting);
        return setting;
    }

    public void setState(boolean enabled) {
        if (this.state == enabled) return;

        this.state = enabled;

        if (enabled) {
            listeners.forEach(AtomicBus.BUS::subscribe);
            onEnable();
        } else {
            listeners.forEach(AtomicBus.BUS::unsubscribe);
            onDisable();
        }
    }

    public void toggle() {
        setState(!state);
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public boolean isActive() {
        return state;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public List<AbstractOption<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }
}
