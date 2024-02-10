package ru.venidiktov.jdbc.starter.util;

import java.io.IOException;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Блок отработает только 1 раз при первой загрузке класса в память (обычно происходит при встрече первого упоминания класса в коде)
     */
    static {
        loadProperties();
    }

    private static void loadProperties() {
        try(var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
