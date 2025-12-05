package yatwinkle.client.service.config;

import com.typesafe.config.*;
import net.fabricmc.loader.api.FabricLoader;
import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.client.service.module.Module;
import yatwinkle.client.service.module.Modules;
import yatwinkle.client.service.setting.AbstractOption;
import yatwinkle.client.service.setting.impl.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ConfigManager implements MinecraftLogger, AutoCloseable {

    private static final String CONFIG_FILE_NAME = "default.conf";
    private static final long AUTO_SAVE_DELAY_MS = 5000;

    private static final ConfigRenderOptions RENDER_OPTIONS = ConfigRenderOptions.defaults()
            .setOriginComments(false)
            .setComments(true)
            .setFormatted(true)
            .setJson(false);

    private static volatile ConfigManager instance;

    private final Path configPath;
    private final AtomicReference<Config> currentConfig;
    private final AtomicBoolean dirty;
    private final ScheduledExecutorService saveExecutor;
    private final ScheduledFuture<?> autoSaveTask;
    private final Modules modules;

    private ConfigManager(Modules modules) {
        this.modules = modules;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        this.currentConfig = new AtomicReference<>(ConfigFactory.empty());
        this.dirty = new AtomicBoolean(false);

        this.saveExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ConfigManager-AutoSave");
            t.setDaemon(true);
            return t;
        });

        load();

        this.autoSaveTask = saveExecutor.scheduleWithFixedDelay(
                this::autoSave, AUTO_SAVE_DELAY_MS, AUTO_SAVE_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    public static void init(Modules modules) {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager(modules);
                }
            }
        }
    }

    public static ConfigManager get() {
        ConfigManager local = instance;
        if (local == null) {
            throw new IllegalStateException("ConfigManager not initialized!");
        }
        return local;
    }

    public static ConfigManager getIfInit() {
        return instance;
    }

    public void load() {
        try {
            if (Files.exists(configPath)) {
                Config loaded = ConfigFactory.parseFile(configPath.toFile());
                currentConfig.set(loaded);
                applyConfigToModules(loaded);
            } else {
                currentConfig.set(ConfigFactory.empty());
            }
        } catch (Exception e) {
            currentConfig.set(ConfigFactory.empty());
        }
    }

    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(() -> {
            try { saveInternal(); }
            catch (IOException ignored) { }
        }, saveExecutor);
    }

    public void save() {
        try { saveInternal(); }
        catch (IOException ignored) { }
    }

    private void saveInternal() throws IOException {
        Config config = buildConfigFromModules();
        String rendered = config.root().render(RENDER_OPTIONS);

        Path tempFile = configPath.resolveSibling(configPath.getFileName() + ".tmp");
        Files.writeString(tempFile, rendered,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tempFile, configPath,
                StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

        currentConfig.set(config);
        dirty.set(false);
    }

    public void markDirty() { dirty.set(true); }

    private void autoSave() {
        if (dirty.get()) {
            try {
                saveInternal();
                dirty.set(false);
            } catch (IOException ignored) { }
        }
    }

    private Config buildConfigFromModules() {
        Map<String, Object> configMap = new HashMap<>();
        for (Module module : modules.getAll()) {
            Map<String, Object> moduleData = new HashMap<>();
            moduleData.put("enabled", module.isActive());
            moduleData.put("key", module.getKey());

            Map<String, Object> settingsData = new HashMap<>();
            for (AbstractOption<?> option : module.getSettings()) {
                Object value = serializeOption(option);
                if (value != null) settingsData.put(option.getId(), value);
            }
            if (!settingsData.isEmpty()) moduleData.put("settings", settingsData);
            configMap.put(module.getId(), moduleData);
        }
        return ConfigFactory.parseMap(Collections.singletonMap("modules", configMap));
    }

    private void applyConfigToModules(Config config) {
        if (!config.hasPath("modules")) return;
        Config modulesConfig = config.getConfig("modules");

        for (Module module : modules.getAll()) {
            String id = module.getId();
            if (!modulesConfig.hasPath(id)) continue;

            try {
                Config mc = modulesConfig.getConfig(id);
                if (mc.hasPath("enabled")) module.setState(mc.getBoolean("enabled"));
                if (mc.hasPath("key")) module.setKey(mc.getInt("key"));
                if (mc.hasPath("settings")) applySettings(module, mc.getConfig("settings"));
            } catch (Exception ignored) { }
        }
    }

    private void applySettings(Module module, Config settings) {
        for (AbstractOption<?> opt : module.getSettings()) {
            if (!settings.hasPath(opt.getId())) continue;
            try { deserializeOption(opt, settings, opt.getId()); }
            catch (Exception ignored) { }
        }
    }

    private Object serializeOption(AbstractOption<?> option) {
        return switch (option) {
            case BoolOption o -> o.getAsBoolean();
            case IntOption o -> o.getAsInt();
            case DoubleOption o -> o.getAsDouble();
            case EnumOption<?> o -> o.get().name();
            case MultiEnumOption<?> o -> o.get().stream()
                    .map(e -> ((Enum<?>) e).name()).toList();
            default -> null;
        };
    }

    private void deserializeOption(AbstractOption<?> option, Config config, String key) {
        switch (option) {
            case BoolOption o -> o.set(config.getBoolean(key));
            case IntOption o -> o.set(config.getInt(key));
            case DoubleOption o -> o.set(config.getDouble(key));
            case EnumOption<?> o -> deserializeEnum(o, config.getString(key));
            case MultiEnumOption<?> o -> deserializeMultiEnum(o, config.getStringList(key));
            default -> {}
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> void deserializeEnum(EnumOption<?> option, String name) {
        EnumOption<T> opt = (EnumOption<T>) option;
        try {
            opt.set(Enum.valueOf(opt.get().getDeclaringClass(), name));
        } catch (IllegalArgumentException ignored) {}
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> void deserializeMultiEnum(MultiEnumOption<?> option, List<String> names) {
        MultiEnumOption<T> opt = (MultiEnumOption<T>) option;
        Class<T> enumClass = opt.getEnumClass();

        Set<T> values = EnumSet.noneOf(enumClass);
        for (String name : names) {
            try { values.add(Enum.valueOf(enumClass, name)); }
            catch (IllegalArgumentException ignored) {}
        }
        opt.set(values);
    }

    @Override
    public void close() {
        if (autoSaveTask != null) autoSaveTask.cancel(false);
        if (dirty.get()) save();

        saveExecutor.shutdown();
        try {
            if (!saveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                saveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            saveExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Path getConfigPath() { return configPath; }
    public Config getCurrentConfig() { return currentConfig.get(); }
}
