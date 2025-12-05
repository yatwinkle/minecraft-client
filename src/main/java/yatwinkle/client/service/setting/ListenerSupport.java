package yatwinkle.client.service.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ListenerSupport<T> {

    private final List<Consumer<T>> listeners = new ArrayList<>();

    public void add(Consumer<T> listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public boolean remove(Consumer<T> listener) {
        return listeners.remove(listener);
    }

    public void notify(T value) {
        if (listeners.isEmpty()) return;

        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).accept(value);
        }
    }

    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    public void clear() {
        listeners.clear();
    }
}
