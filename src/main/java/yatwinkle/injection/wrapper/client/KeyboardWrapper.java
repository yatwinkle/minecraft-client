package yatwinkle.injection.wrapper.client;

import yatwinkle.client.feature.event.client.EventKeyboardKey;
import yatwinkle.client.service.event.AtomicBus;

public class KeyboardWrapper {

    public static void onKey(int key, int scanCode, int action, int modifiers) {
        EventKeyboardKey event = new EventKeyboardKey(key, scanCode, action, modifiers);
        AtomicBus.BUS.post(event);
    }
}
