package com.thunderbear06.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModConfiguration");

    public static <T extends ConfigState> T loadConfig(String modID, T state) {
        Path configPath = getPath(modID);

        if (!Files.exists(configPath, LinkOption.NOFOLLOW_LINKS))
            createConfig(configPath, state);

        state = readConfig(configPath, state.getClass());

        return state;
    }

    private static Path getPath(String modID) {
        return Platform.getConfigFolder().resolve(modID+".json");
    }

    private static <T extends ConfigState> void createConfig(Path configPath, T state) {
        try {
            Files.createFile(configPath);
            writeConfig(configPath, state);
        } catch (IOException e) {
            LOGGER.error("Failed to create config file: {}", e.getLocalizedMessage());
        }
    }

    private static <T extends ConfigState> void writeConfig(Path configPath, T state) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(state);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            LOGGER.error("Failed to write config file: {}", e.getLocalizedMessage());
        }
    }

    private static <T extends ConfigState> T readConfig(Path configPath, Class<? extends ConfigState> clazz) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(Files.newBufferedReader(configPath));
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + e.getLocalizedMessage());
        }
    }

    public interface ConfigState {}
}
