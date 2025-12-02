package yatwinkle.client.feature.event.client;

import yatwinkle.client.service.event.Event;

public record EventKeyboardKey(int key, int scanCode, int action, int modifiers) implements Event { }