package yatwinkle.client.service.event;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AtomicBus {

    public static final AtomicBus BUS = new AtomicBus();

    private final Map<Class<?>, Listener<?>[]> registry = new ConcurrentHashMap<>();

    private static final Comparator<Listener<?>> PRIORITY_SORTER =
            Comparator.comparingInt(Listener::getPriority);

    @SuppressWarnings("unchecked")
    public void post(Event event) {
        Listener<?>[] listeners = registry.get(event.getClass());

        if (listeners == null || listeners.length == 0) return;

        boolean isCancellable = event instanceof Cancellable;

        for (Listener<?> listener : listeners) {
            ((Listener<Event>) listener).accept(event);

            if (isCancellable && ((Cancellable) event).isCancelled()) {
                break;
            }
        }
    }

    public synchronized void subscribe(Listener<?> listener) {
        Class<?> type = listener.getTargetEvent();

        Listener<?>[] current = registry.get(type);
        Listener<?>[] updated;

        if (current == null) {
            updated = new Listener<?>[]{listener};
        } else {
            updated = Arrays.copyOf(current, current.length + 1);
            updated[current.length] = listener;
            Arrays.sort(updated, PRIORITY_SORTER);
        }

        registry.put(type, updated);
    }

    public synchronized void unsubscribe(Listener<?> listener) {
        Class<?> type = listener.getTargetEvent();
        Listener<?>[] current = registry.get(type);

        if (current != null) {
            int index = -1;
            for (int i = 0; i < current.length; i++) {
                if (current[i] == listener) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                if (current.length == 1) {
                    registry.remove(type);
                    return;
                }

                Listener<?>[] updated = new Listener<?>[current.length - 1];
                System.arraycopy(current, 0, updated, 0, index);
                System.arraycopy(current, index + 1, updated, index, current.length - index - 1);

                registry.put(type, updated);
            }
        }
    }
}