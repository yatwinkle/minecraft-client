package yatwinkle.client.service.event;

import java.util.function.Consumer;

public class Listener<T extends Event> {

    private final Class<T> targetEvent;
    private final Consumer<T> action;
    private final int priority;

    public Listener(Class<T> targetEvent, Consumer<T> action) {
        this(targetEvent, EventPriority.NORMAL, action);
    }

    public Listener(Class<T> targetEvent, int priority, Consumer<T> action) {
        this.targetEvent = targetEvent;
        this.priority = priority;
        this.action = action;
    }

    public Class<T> getTargetEvent() {
        return targetEvent;
    }

    public int getPriority() {
        return priority;
    }

    public void accept(T event) {
        action.accept(event);
    }
}