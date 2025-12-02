package yatwinkle.injection.wrapper.network;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yatwinkle.client.feature.event.network.EventPlayerTick;
import yatwinkle.client.service.event.AtomicBus;

public class ClientPlayerEntityWrapper {

    public static void onTick(CallbackInfo callbackInfo) {
        EventPlayerTick playerTick = new EventPlayerTick();
        AtomicBus.BUS.post(playerTick);

        if (playerTick.isCancelled())
            callbackInfo.cancel();
    }
}
