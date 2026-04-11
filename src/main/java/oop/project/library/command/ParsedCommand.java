package oop.project.library.command;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ParsedCommand {

    private final Map<String, Object> values;

    ParsedCommand(Map<String, Object> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    public Object get(String name) {
        if (!values.containsKey(name)) {
            throw new IllegalArgumentException("Unknown argument '" + name + "'.");
        }
        return values.get(name);
    }
}