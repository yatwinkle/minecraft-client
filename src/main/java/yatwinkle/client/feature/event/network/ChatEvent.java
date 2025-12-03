package yatwinkle.client.feature.event.network;

import yatwinkle.client.service.event.CancellableEvent;

public class ChatEvent extends CancellableEvent {
    private final String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
