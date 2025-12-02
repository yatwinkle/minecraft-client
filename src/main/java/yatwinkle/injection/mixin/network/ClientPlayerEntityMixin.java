package yatwinkle.injection.mixin.network;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.injection.wrapper.network.ClientPlayerEntityWrapper;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements MinecraftLogger {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tickHook(CallbackInfo callbackInfo) {
        ClientPlayerEntityWrapper.onTick(callbackInfo);
    }
}
