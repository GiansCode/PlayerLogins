package io.obadiah.pl.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.obadiah.pl.common.PlayerLoginsCore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Represents a JSON file. Can be utilised for configs, storage, etc.
 *
 * Modified from <a href="https://github.com/ObadiahCrowe/ConfigLib">https://github.com/ObadiahCrowe/ConfigLib</a>
 * to work with Spigot.
 *
 * @param <T> The class that is extending LoadableConfig.
 */
public abstract class LoadableConfig<T> {

    /**
     * The Gson instance utilised in loading / saving.
     */
    private static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    /**
     * Class of the configuration (for serialisation).
     */
    private transient final Class<? extends LoadableConfig> configurationClass;

    /**
     * Logger to log to.
     */
    private transient final Logger logger;

    /**
     * Represents a configuration file.
     *
     * @param clazz Class of the configuration.
     */
    public LoadableConfig(Class<? extends LoadableConfig> clazz) {
        this.configurationClass = clazz;
        this.logger = PlayerLoginsCore.getLogger();
    }

    /**
     * @return The path that this config should be saved and loaded from.
     */
    public abstract Path getPath();

    /**
     * @return Default config to write if none can be found.
     */
    public abstract T getDefaultConfig();

    /**
     * Loads the configuration from disk and returns its instantiated object.
     *
     * @return The config instance.
     */
    @SuppressWarnings("unchecked")
    public T load() {
        try {
            this.logger.info("Attempting to load config, " + this.getClass().getSimpleName() + "..");
            return (T) GSON_INSTANCE.fromJson(String.join("", Files.readAllLines(this.getPath())), this.configurationClass);
        } catch (IOException | ClassCastException e) {
            if (e instanceof NoSuchFileException) {
                this.logger.warning("Could not find, " + this.getPath().toFile().getName() + ", creating one now..");
            } else {
                e.printStackTrace();
            }

            T config = this.getDefaultConfig();
            ((LoadableConfig) config).save();

            return config;
        }
    }

    /**
     * Saves the config to disk.
     */
    public void save() {
        try {
            this.logger.info("Saving config, " + this.getClass().getSimpleName() + "..");
            if (!Files.exists(PlayerLoginsCore.getPath())) {
                Files.createDirectory(PlayerLoginsCore.getPath());
            }

            if (!Files.exists(this.getPath())) {
                Files.createFile(this.getPath());
            }

            Files.write(this.getPath(), GSON_INSTANCE.toJson(this).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a loadable config via its class.
     *
     * @param clazz Class to load from.
     *
     * @return Loaded, generified, config.
     */
    public static <E extends LoadableConfig> LoadableConfig<E> getByClass(Class<E> clazz) {
        try {
            return (E) clazz.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Error loading config, " + clazz.getSimpleName() + ": " + e);
        }
    }
}
