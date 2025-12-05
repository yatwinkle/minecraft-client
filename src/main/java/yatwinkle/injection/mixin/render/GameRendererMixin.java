package yatwinkle.injection.mixin.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yatwinkle.client.service.render.renderers.impl.BuiltDualKawase;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void renderAfterWorld(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        BuiltDualKawase.blur.onRenderAfterWorld();
    }
}
