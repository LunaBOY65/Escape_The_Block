package com.escapetheblock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Reads and writes the best time without knowing about the game window or UI.
 */
public class Configuration {
    private static final String BEST_TIME_KEY = "best";
    private static final double DEFAULT_BEST_TIME_SECONDS = 3.0;

    private final Path file;

    public Configuration() {
        this(Path.of("config.xml"));
    }

    public Configuration(Path file) {
        this.file = file;
    }

    public double loadBestTime() throws IOException {
        if (Files.notExists(file)) {
            saveBestTime(DEFAULT_BEST_TIME_SECONDS);
            return DEFAULT_BEST_TIME_SECONDS;
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.loadFromXML(input);
        }

        String value = properties.getProperty(BEST_TIME_KEY);
        if (value == null) {
            throw new IOException("Missing '" + BEST_TIME_KEY + "' value in " + file);
        }

        double bestTime;
        try {
            bestTime = Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid best time in " + file, exception);
        }
        if (!Double.isFinite(bestTime) || bestTime < 0) {
            throw new IOException("Best time must be a finite, non-negative number in " + file);
        }
        return bestTime;
    }

    public void saveBestTime(double bestTimeSeconds) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(BEST_TIME_KEY, Double.toString(bestTimeSeconds));
        try (OutputStream output = Files.newOutputStream(file)) {
            properties.storeToXML(output, "Escape the Block options");
        }
    }
}
