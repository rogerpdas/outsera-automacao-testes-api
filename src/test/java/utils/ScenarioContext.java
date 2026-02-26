package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contexto compartilhado entre steps via PicoContainer.
 * Armazena token JWT e outros dados entre steps do mesmo cenário.
 */
public class ScenarioContext {

    private final Map<String, Object> context = new HashMap<>();

    public static final String ACCESS_TOKEN    = "ACCESS_TOKEN";
    public static final String LAST_RESPONSE   = "LAST_RESPONSE";
    public static final String LAST_CURL       = "LAST_CURL";

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    public String getString(String key) {
        Object value = context.get(key);
        return value != null ? value.toString() : null;
    }

    public boolean contains(String key) {
        return context.containsKey(key) && context.get(key) != null;
    }

    public void clear() {
        context.clear();
    }
}
