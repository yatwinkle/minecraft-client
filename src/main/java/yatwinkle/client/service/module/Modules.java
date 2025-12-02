package yatwinkle.client.service.module;

import yatwinkle.client.feature.event.client.EventKeyboardKey;
import yatwinkle.client.feature.module.player.*;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Modules {

    private static Modules instance;

    private final List<Module> modules = new ArrayList<>();

    public final ModuleAutoSprint autoSprint;

    private Modules() {
        this.autoSprint = register(new ModuleAutoSprint());

        initializeStates();
        setupInputHandling();
    }

    private void initializeStates() {
        for (Module module : modules) {
            if (module.isActive()) {
                module.setState(false);
                module.setState(true);
            }
        }
    }

    private void setupInputHandling() {
        AtomicBus.BUS.subscribe(new Listener<>(EventKeyboardKey.class, event -> {
            if (event.action() != 1) return;

            for (Module module : modules) {
                if (module.getKey() == event.key()) {
                    module.toggle();
                }
            }
        }));
    }

    private <T extends Module> T register(T module) {
        modules.add(module);
        return module;
    }

    public static void init() {
        instance = new Modules();
    }

    public static Modules get() {
        return instance;
    }
}
