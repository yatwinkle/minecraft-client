package yatwinkle.client.service.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ListenerSupport<T> {

    private Object listeners;

    @SuppressWarnings("unchecked")
    public void add(Consumer<T> listener) {
        if (listeners == null) {
            listeners = listener;
        } else if (listeners instanceof Consumer) {
            List<Consumer<T>> list = new ArrayList<>(2);
            list.add((Consumer<T>) listeners);
            list.add(listener);
            listeners = list;
        } else {
            ((List<Consumer<T>>) listeners).add(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public void notify(T value) {
        if (listeners == null) return;

        if (listeners instanceof Consumer) {
            ((Consumer<T>) listeners).accept(value);
        } else {
            List<Consumer<T>> list = (List<Consumer<T>>) listeners;
            for (Consumer<T> tConsumer : list) {
                tConsumer.accept(value);
            }
        }
    }
}
