package yatwinkle;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import yatwinkle.client.feature.event.hud.EventInGameHud;
import yatwinkle.client.service.command.CommandManager;
import yatwinkle.client.service.config.ConfigManager;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;
import yatwinkle.client.service.module.Modules;

import yatwinkle.client.service.render.builders.Builder;
import yatwinkle.client.service.render.builders.states.QuadColorState;
import yatwinkle.client.service.render.builders.states.QuadRadiusState;
import yatwinkle.client.service.render.builders.states.SizeState;
import yatwinkle.client.service.render.renderers.impl.BuiltDualKawase;

import java.awt.*;

public class ClientMain implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Modules.init();
        ConfigManager.init(Modules.get());
        CommandManager.init();

        AtomicBus.BUS.subscribe(onRender);
    }

    private final Listener<EventInGameHud> onRender = new Listener<>(EventInGameHud.class, event -> {
        DrawContext context = event.context();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Color color = new Color(3, 4, 6, 175);

        BuiltDualKawase blur = Builder.blur()
                .size(new SizeState(100, 100))
                .radius(new QuadRadiusState(6))
                .color(new QuadColorState(color.getRGB()))
                .smoothness(1.0F)
                .build();

        blur.render(matrix, 50, 50, 0);
    });
}
