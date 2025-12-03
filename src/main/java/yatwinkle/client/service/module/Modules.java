package yatwinkle.client.service.module;

import yatwinkle.client.feature.event.client.EventKeyboardKey;
import yatwinkle.client.feature.module.player.*;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Modules {

    private static volatile Modules instance;

    private final Map<String, Module> modulesById = new ConcurrentHashMap<>();
    private final Map<Class<? extends Module>, Module> modulesByClass = new ConcurrentHashMap<>();
    private final Map<Integer, List<Module>> modulesByKey = new ConcurrentHashMap<>();

    private volatile Collection<Module> cachedModulesView;

    private Modules() {
        register(new ModuleAutoSprint());
        setupInputHandling();
    }

    public static void init() {
        if (instance == null) {
            synchronized (Modules.class) {
                if (instance == null) {
                    instance = new Modules();
                }
            }
        }
    }

    public static Modules get() {
        Modules local = instance;
        if (local == null) {
            throw new IllegalStateException("Modules not initialized!");
        }
        return local;
    }

    private void setupInputHandling() {
        AtomicBus.BUS.subscribe(new Listener<>(EventKeyboardKey.class, event -> {
            if (event.action() != 1 || event.key() == 0) return;

            List<Module> modules = modulesByKey.get(event.key());
            if (modules != null) {
                for (Module module : modules) {
                    module.toggle();
                }
            }
        }));
    }

    private <T extends Module> void register(T module) {
        if (modulesById.putIfAbsent(module.getId(), module) != null) {
            throw new IllegalArgumentException("Duplicate module id: " + module.getId());
        }

        modulesByClass.put(module.getClass(), module);

        int initialKey = module.getKey();
        if (initialKey != 0) {
            addToKeyMap(module, initialKey);
        }

        module.setKeyChangeCallback(this::onModuleKeyChange);
        cachedModulesView = null;

        if (module.isActive()) {
            module.setState(true);
        }
    }

    private void onModuleKeyChange(Module module, int oldKey, int newKey) {
        if (oldKey != 0) removeFromKeyMap(module, oldKey);
        if (newKey != 0) addToKeyMap(module, newKey);
    }

    private void addToKeyMap(Module module, int key) {
        modulesByKey.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(module);
    }

    private void removeFromKeyMap(Module module, int key) {
        modulesByKey.computeIfPresent(key, (k, list) -> {
            list.remove(module);
            return list.isEmpty() ? null : list;
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getByClass(Class<T> clazz) {
        return (T) modulesByClass.get(clazz);
    }

    public Module getById(String id) {
        return modulesById.get(id);
    }

    public Collection<Module> getAll() {
        Collection<Module> view = cachedModulesView;
        if (view == null) {
            view = Collections.unmodifiableCollection(modulesById.values());
            cachedModulesView = view;
        }
        return view;
    }
}
