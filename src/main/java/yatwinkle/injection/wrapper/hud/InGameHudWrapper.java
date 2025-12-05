package yatwinkle.injection.wrapper.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import yatwinkle.client.feature.event.hud.EventInGameHud;
import yatwinkle.client.service.event.AtomicBus;

public class InGameHudWrapper {

    public static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        EventInGameHud event = new EventInGameHud(context, tickCounter);
        AtomicBus.BUS.post(event);
    }
}