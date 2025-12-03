package yatwinkle.injection.wrapper.network;

import yatwinkle.client.feature.event.network.ChatEvent;
import yatwinkle.client.service.event.AtomicBus;

public class ClientPlayNetworkHandlerWrapper {

    public static boolean onSendChatMessage(String message) {
        ChatEvent event = new ChatEvent(message);
        AtomicBus.BUS.post(event);
        return event.isCancelled();
    }
}
