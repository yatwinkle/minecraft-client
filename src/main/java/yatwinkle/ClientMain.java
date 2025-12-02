package yatwinkle;

import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;
import yatwinkle.client.feature.event.client.EventKeyboardKey;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.module.Modules;

public class ClientMain implements ClientModInitializer {

    public static ClientMain instance;

    @Override
    public void onInitializeClient() {
        instance = this;

        Modules.init();

        AtomicBus.BUS.subscribe(onKey);
    }

    private final Listener<EventKeyboardKey> onKey = new Listener<>(EventKeyboardKey.class, event -> {
        if (event.key() == GLFW.GLFW_KEY_G) {
            System.out.println("G pressed");
        }
    });
}
