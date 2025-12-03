package yatwinkle.client.service.module;

import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.client.service.config.ConfigAware;
import yatwinkle.client.service.config.ConfigManager;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Event;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.setting.AbstractOption;
import yatwinkle.client.service.setting.impl.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Module implements MinecraftLogger, ConfigAware {

    private final String id;
    private final String displayName;
    private final Category category;

    private final AtomicInteger key;
    private final AtomicBoolean state;

    private final List<AbstractOption<?>> settings = new CopyOnWriteArrayList<>();
    private final Set<Listener<?>> listenerSet = ConcurrentHashMap.newKeySet();
    private final List<Listener<?>> listeners = new CopyOnWriteArrayList<>();

    private volatile List<AbstractOption<?>> cachedSettingsView;
    private KeyChangeCallback keyChangeCallback;

    @FunctionalInterface
    public interface KeyChangeCallback {
        void onKeyChange(Module module, int oldKey, int newKey);
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
        this.key = new AtomicInteger(key);
        this.state = new AtomicBoolean(state);
    }

    void setKeyChangeCallback(KeyChangeCallback callback) {
        this.keyChangeCallback = callback;
    }

    protected <T extends Event> Listener<T> listen(Listener<T> listener) {
        if (listenerSet.add(listener)) {
            listeners.add(listener);
            if (state.get()) {
                AtomicBus.BUS.subscribe(listener);
            }
        }
        return listener;
    }

    protected BoolOption bool(String id, String name, String description, boolean defaultValue) {
        return trackConfig(register(new BoolOption(id, name, description, defaultValue)));
    }

    protected IntOption number(String id, String name, String description, int value, int min, int max, int step) {
        return trackConfig(register(new IntOption(id, name, description, value, min, max, step)));
    }

    protected DoubleOption number(String id, String name, String description, double value, double min, double max, double step) {
        return trackConfig(register(new DoubleOption(id, name, description, value, min, max, step)));
    }

    protected <T extends Enum<T>> EnumOption<T> mode(String id, String name, String description, T defaultValue) {
        return trackConfig(register(new EnumOption<>(id, name, description, defaultValue)));
    }

    @SafeVarargs
    protected final <T extends Enum<T>> MultiEnumOption<T> multi(String id, String name, String description, T... defaultValues) {
        return trackConfig(register(new MultiEnumOption<>(id, name, description, defaultValues)));
    }

    protected <T extends Enum<T>> MultiEnumOption<T> multi(String id, String name, String description, Class<T> enumClass) {
        return trackConfig(register(new MultiEnumOption<>(id, name, description, enumClass)));
    }

    private <S extends AbstractOption<?>> S register(S setting) {
        settings.add(setting);
        cachedSettingsView = null;
        return setting;
    }

    public void setState(boolean enabled) {
        if (state.compareAndSet(!enabled, enabled)) {
            applyState(enabled);
            ConfigManager.get().markDirty();
        }
    }

    public void toggle() {
        boolean prev, next;
        do {
            prev = state.get();
            next = !prev;
        } while (!state.compareAndSet(prev, next));

        applyState(next);
        ConfigManager.get().markDirty();
    }

    private void applyState(boolean enabled) {
        if (enabled) {
            listeners.forEach(AtomicBus.BUS::subscribe);
            onEnable();
        } else {
            listeners.forEach(AtomicBus.BUS::unsubscribe);
            onDisable();
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}

    public boolean isActive() { return state.get(); }
    public int getKey() { return key.get(); }

    public void setKey(int newKey) {
        int oldKey = key.getAndSet(newKey);
        if (oldKey != newKey) {
            if (keyChangeCallback != null) {
                keyChangeCallback.onKeyChange(this, oldKey, newKey);
            }
            ConfigManager.get().markDirty();
        }
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Category getCategory() { return category; }

    public List<AbstractOption<?>> getSettings() {
        List<AbstractOption<?>> view = cachedSettingsView;
        if (view == null) {
            synchronized (this) {
                view = cachedSettingsView;
                if (view == null) {
                    view = List.copyOf(settings);
                    cachedSettingsView = view;
                }
            }
        }
        return view;
    }
}
