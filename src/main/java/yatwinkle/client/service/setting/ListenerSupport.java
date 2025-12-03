package yatwinkle.client.service.setting;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class ListenerSupport<T> {

    private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();

    public void add(Consumer<T> listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        listeners.add(listener);
    }

    public boolean remove(Consumer<T> listener) {
        return listeners.remove(listener);
    }

    public void notify(T value) {
        for (Consumer<T> listener : listeners) {
            listener.accept(value);
        }
    }

    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    public void clear() {
        listeners.clear();
    }
}
