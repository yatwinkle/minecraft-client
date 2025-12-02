package yatwinkle.client.feature.module.player;

import org.lwjgl.glfw.GLFW;
import yatwinkle.client.feature.event.network.EventPlayerTick;
import yatwinkle.client.helper.MinecraftInstances;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.module.Category;
import yatwinkle.client.service.module.Module;
import yatwinkle.injection.mixin.accessor.ClientPlayerEntityAccessor;

public class ModuleAutoSprint extends Module implements MinecraftInstances {

    public ModuleAutoSprint() {
        super("AutoSprint", "Auto Sprint", Category.MOVEMENT, GLFW.GLFW_KEY_X, true);
    }

    @SuppressWarnings("unused")
    private final Listener<EventPlayerTick> onTick = listen(new Listener<>(EventPlayerTick.class, event -> {
        if (client.player != null
                && ((ClientPlayerEntityAccessor) client.player).invokeCanSprint()
                && client.player.input.movementForward > 0F)
            client.player.setSprinting(true);
    }));

    @Override
    public void onDisable() {
        if (client.player != null)
            client.player.setSprinting(false);
        super.onDisable();
    }
}
