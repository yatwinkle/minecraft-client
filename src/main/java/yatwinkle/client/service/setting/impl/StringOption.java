package yatwinkle.client.service.setting.impl;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.Objects;
import java.util.function.Predicate;

public class StringOption extends AbstractOption<String> {

    private String value;
    private final Predicate<String> validator;

    public StringOption(String id, String name, String description, String defaultValue) {
        this(id, name, description, defaultValue, s -> true);
    }

    public StringOption(String id, String name, String description, String defaultValue, Predicate<String> validator) {
        super(id, name, description, defaultValue);
        this.value = defaultValue;
        this.validator = validator;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    protected void setValueInternal(String newValue) {
        if (newValue == null) return;

        if (Objects.equals(this.value, newValue)) return;

        if (!validator.test(newValue)) return;

        this.value = newValue;
        notifyListeners(newValue);
    }

    public void append(String text) {
        setValueInternal(this.value + text);
    }

    public void backspace() {
        if (value != null && !value.isEmpty()) {
            setValueInternal(value.substring(0, value.length() - 1));
        }
    }

    public void clear() {
        setValueInternal("");
    }
}
