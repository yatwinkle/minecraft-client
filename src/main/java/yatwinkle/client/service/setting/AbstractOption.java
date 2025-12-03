package yatwinkle.client.service.setting;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class AbstractOption<T> {

    private static final BooleanSupplier ALWAYS_TRUE = () -> true;

    protected final String id;
    protected final String name;
    protected final String description;
    protected final T defaultValue;

    protected final ListenerSupport<T> listenerSupport = new ListenerSupport<>();
    protected BooleanSupplier visibility = ALWAYS_TRUE;

    public AbstractOption(String id, String name, String description, T defaultValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public abstract T get();

    protected abstract void setValueInternal(T value);

    public void set(T value) {
        if (value != null) {
            setValueInternal(value);
            listenerSupport.notify(value);
        }
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractOption<T>> S onChange(Consumer<T> listener) {
        listenerSupport.add(listener);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends AbstractOption<T>> S setVisibility(BooleanSupplier visibility) {
        this.visibility = visibility != null ? visibility : ALWAYS_TRUE;
        return (S) this;
    }

    public boolean isVisible() {
        return visibility.getAsBoolean();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
