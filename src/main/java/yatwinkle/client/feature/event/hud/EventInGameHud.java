package yatwinkle.client.feature.event.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import yatwinkle.client.service.event.Event;

public record EventInGameHud(DrawContext context, RenderTickCounter tickCounter) implements Event { }