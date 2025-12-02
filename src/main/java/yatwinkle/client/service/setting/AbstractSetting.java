package yatwinkle.client.service.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractSetting<T> {

    private final String id;
    private final String name;
    private final String description;

    private final Supplier<Boolean> visibility = () -> true;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public AbstractSetting(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public abstract T get();
    public abstract void set(T value);

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Supplier<Boolean> getVisibility() {
        return visibility;
    }
}
