package yatwinkle.client.service.module;

import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Event;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.setting.AbstractSetting;
import yatwinkle.client.service.setting.impl.BooleanSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module implements MinecraftLogger {

    public final String id;
    public final String displayName;
    public final Category category;

    private boolean state;
    private int key;

    private final List<AbstractSetting<?>> settings = new ArrayList<>();
    private final List<Listener<?>> listeners = new ArrayList<>();

    protected <T extends Event> Listener<T> listen(Listener<T> listener) {
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

    protected BooleanSetting bool(String id, String name, String description, boolean defaultValue) {
        return register(new BooleanSetting(id, name, description, defaultValue));
    }

    private <T extends AbstractSetting<?>> T register(T setting) {
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

    public List<AbstractSetting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }
}
