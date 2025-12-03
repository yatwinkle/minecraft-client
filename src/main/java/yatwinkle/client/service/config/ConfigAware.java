package yatwinkle.client.service.config;

import yatwinkle.client.service.setting.AbstractOption;
import java.util.function.Consumer;

public interface ConfigAware {

    default <T, O extends AbstractOption<T>> O trackConfig(O option) {
        option.onChange(value -> {
            ConfigManager manager = ConfigManager.getIfInitialized();
            if (manager != null) {
                manager.markDirty();
            }
        });
        return option;
    }

    default <T, O extends AbstractOption<T>> O trackConfigWithCallback(O option, Consumer<T> callback) {
        option.onChange(value -> {
            callback.accept(value);
            ConfigManager manager = ConfigManager.getIfInitialized();
            if (manager != null) {
                manager.markDirty();
            }
        });
        return option;
    }
}
