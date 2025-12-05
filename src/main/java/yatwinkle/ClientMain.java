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
import yatwinkle.client.service.render.renderers.impl.BuiltRectangle;

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

        BuiltRectangle rectangle = Builder.rectangle()
                .size(new SizeState(100, 100))
                .color(new QuadColorState(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW))
                .radius(new QuadRadiusState(6f, 0f, 20f, 35f))
                .smoothness(3.0f)
                .build();

        rectangle.render(matrix, 40, 40);
    });
}
