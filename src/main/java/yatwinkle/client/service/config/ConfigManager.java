package yatwinkle.client.service.config;

import com.typesafe.config.*;
import net.fabricmc.loader.api.FabricLoader;
import yatwinkle.client.helper.ClientLogger;
import yatwinkle.client.service.module.Module;
import yatwinkle.client.service.module.Modules;
import yatwinkle.client.service.setting.AbstractOption;
import yatwinkle.client.service.setting.impl.*;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ConfigManager implements ClientLogger, AutoCloseable {

    private static final String DEFAULT_CONFIG = "default";
    private static final String EXTENSION = ".conf";
    private static final String SETTINGS_KEY = "settings";
    private static final String ENABLED_KEY = "enabled";
    private static final String KEYBIND_KEY = "key";
    private static final String MODULES_BLOCK = "modules";

    private static final Pattern VALID_CONFIG_NAME = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private static final ConfigRenderOptions RENDER_OPTIONS = ConfigRenderOptions.defaults()
            .setOriginComments(false)
            .setComments(true)
            .setFormatted(true)
            .setJson(false);

    private static ConfigManager instance;

    private final Path configDir;
    private final Modules modules;
    private final ExecutorService ioExecutor;
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    private ConfigManager(Modules modules) {
        this.modules = modules;
        this.configDir = FabricLoader.getInstance().getConfigDir().resolve("yatwinkle");

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            error("Failed to create config directory!", e);
        }

        this.ioExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Yatwinkle-Config-IO");
            t.setDaemon(true);
            return t;
        });

        loadConfig(DEFAULT_CONFIG);
    }

    public static void init(Modules modules) {
        if (instance == null) {
            instance = new ConfigManager(modules);
        }
    }

    public static ConfigManager get() {
        if (instance == null) {
            throw new IllegalStateException("ConfigManager not initialized!");
        }
        return instance;
    }

    public static ConfigManager getIfInit() {
        return instance;
    }

    public void saveDefaultConfig() {
        saveConfig(DEFAULT_CONFIG);
    }

    public void loadConfig(String name) {
        if (!isValidConfigName(name)) {
            error("Invalid config name: " + name);
            return;
        }

        Path path = getPath(name);
        if (!Files.exists(path)) {
            if (DEFAULT_CONFIG.equals(name)) {
                return;
            }
            error("Config file not found: " + name);
            return;
        }

        try {
            Config loadedConfig = ConfigFactory.parseFile(path.toFile());
            applyConfigToModules(loadedConfig);
            dirty.set(false);
        } catch (Exception e) {
            error("Failed to parse config: " + name, e);
        }
    }

    public void saveConfig(String name) {
        if (!isValidConfigName(name)) {
            error("Invalid config name: " + name);
            return;
        }

        Config snapshot = buildConfigFromModules();

        CompletableFuture.runAsync(() -> writeConfigToFile(name, snapshot), ioExecutor)
                .exceptionally(ex -> {
                    error("Failed to save config: " + name, ex);
                    return null;
                })
                .thenRun(() -> dirty.set(false));
    }

    public List<String> listConfigs() {
        try (Stream<Path> stream = Files.list(configDir)) {
            return stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(p -> p.getFileName().toString().replace(EXTENSION, ""))
                    .filter(this::isValidConfigName)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            error("Failed to list configs", e);
            return Collections.emptyList();
        }
    }

    public void markDirty() {
        dirty.set(true);
    }

    public boolean isDirty() {
        return dirty.get();
    }

    @Override
    public void close() {
        if (isDirty()) {
            saveDefaultConfig();
        }

        ioExecutor.shutdown();
        try {
            if (!ioExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                ioExecutor.shutdownNow();
                warn("Config IO executor did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            ioExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private boolean isValidConfigName(String name) {
        return name != null && VALID_CONFIG_NAME.matcher(name).matches();
    }

    private void writeConfigToFile(String name, Config config) {
        try {
            String rendered = config.root().render(RENDER_OPTIONS);
            Path targetPath = getPath(name);
            Path tempPath = targetPath.resolveSibling(targetPath.getFileName() + ".tmp");

            Files.writeString(tempPath, rendered, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write config file: " + name, e);
        }
    }

    private Path getPath(String name) {
        String safeName = name.endsWith(EXTENSION) ? name : name + EXTENSION;
        return configDir.resolve(safeName);
    }

    private Config buildConfigFromModules() {
        Map<String, Object> modulesMap = new LinkedHashMap<>();

        for (Module module : modules.getAll()) {
            Map<String, Object> moduleData = new LinkedHashMap<>();
            moduleData.put(ENABLED_KEY, module.isActive());
            moduleData.put(KEYBIND_KEY, module.getKey());

            Map<String, Object> settingsData = new LinkedHashMap<>();
            for (AbstractOption<?> option : module.getSettings()) {
                Object serializedValue = serializeOption(option);
                if (serializedValue != null) {
                    settingsData.put(option.getId(), serializedValue);
                }
            }

            if (!settingsData.isEmpty()) {
                moduleData.put(SETTINGS_KEY, settingsData);
            }

            modulesMap.put(module.getId(), moduleData);
        }

        return ConfigFactory.parseMap(Collections.singletonMap(MODULES_BLOCK, modulesMap));
    }

    private Object serializeOption(AbstractOption<?> option) {
        return switch (option) {
            case BoolOption o -> o.getAsBoolean();
            case IntOption o -> o.getAsInt();
            case DoubleOption o -> o.getAsDouble();
            case StringOption o -> o.get();
            case ColorOption o -> o.getInt();
            case EnumOption<?> o -> o.get().name();
            case MultiEnumOption<?> o -> o.get().stream()
                    .map(Enum::name)
                    .toList();
            default -> null;
        };
    }

    private void applyConfigToModules(Config config) {
        if (!config.hasPath(MODULES_BLOCK)) return;

        Config modulesConfig = config.getConfig(MODULES_BLOCK);

        for (Module module : modules.getAll()) {
            String id = module.getId();
            if (!modulesConfig.hasPath(id)) continue;

            try {
                Config mc = modulesConfig.getConfig(id);

                if (mc.hasPath(SETTINGS_KEY)) {
                    applySettings(module, mc.getConfig(SETTINGS_KEY));
                }

                if (mc.hasPath(KEYBIND_KEY)) {
                    module.setKey(mc.getInt(KEYBIND_KEY));
                }

                if (mc.hasPath(ENABLED_KEY)) {
                    module.setState(mc.getBoolean(ENABLED_KEY));
                }
            } catch (Exception e) {
                error("Error applying config for module: " + id, e);
            }
        }
    }

    private void applySettings(Module module, Config settings) {
        for (AbstractOption<?> opt : module.getSettings()) {
            if (!settings.hasPath(opt.getId())) continue;

            deserializeOption(opt, settings, opt.getId());
        }
    }

    private void deserializeOption(AbstractOption<?> option, Config config, String key) {
        switch (option) {
            case BoolOption o -> o.set(config.getBoolean(key));
            case IntOption o -> o.set(config.getInt(key));
            case DoubleOption o -> o.set(config.getDouble(key));
            case StringOption o -> o.set(config.getString(key));
            case ColorOption o -> o.set(new Color(config.getInt(key), true));
            case EnumOption<?> o -> deserializeEnum(o, config.getString(key));
            case MultiEnumOption<?> o -> deserializeMultiEnum(o, config.getStringList(key));
            default -> { }
        }
    }

    private <E extends Enum<E>> void deserializeEnum(EnumOption<E> option, String name)
            throws IllegalArgumentException {
        option.set(Enum.valueOf(option.get().getDeclaringClass(), name));
    }

    private <E extends Enum<E>> void deserializeMultiEnum(MultiEnumOption<E> option, List<String> names)
            throws IllegalArgumentException {
        Class<E> enumClass = option.getEnumClass();
        Set<E> values = EnumSet.noneOf(enumClass);

        for (String name : names) {
            values.add(Enum.valueOf(enumClass, name));
        }

        option.set(values);
    }
}
