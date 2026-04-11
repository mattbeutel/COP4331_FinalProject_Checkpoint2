package oop.project.library.command;

import oop.project.library.input.BasicArgs;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CommandSpec {

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private final List<ValueSpec<?>> positionalSpecs = new ArrayList<>();
        private final List<ValueSpec<?>> namedSpecs = new ArrayList<>();

        private Builder(String name) {
            this.name = Objects.requireNonNull(name, "name");
        }

        public Builder positional(ValueSpec<?> spec) {
            requireKind(spec, ValueSpec.Kind.POSITIONAL);
            positionalSpecs.add(spec);
            return this;
        }

        public Builder named(ValueSpec<?> spec) {
            requireKind(spec, ValueSpec.Kind.NAMED);
            namedSpecs.add(spec);
            return this;
        }

        public CommandSpec build() {
            return new CommandSpec(name, positionalSpecs, namedSpecs);
        }

        private static void requireKind(ValueSpec<?> spec, ValueSpec.Kind expected) {
            Objects.requireNonNull(spec, "spec");
            if (spec.kind() != expected) {
                throw new IllegalArgumentException("Expected a " + expected + " specification.");
            }
        }
    }

    private final String name;
    private final List<ValueSpec<?>> positionalSpecs;
    private final List<ValueSpec<?>> namedSpecs;

    private CommandSpec(String name, List<ValueSpec<?>> positionalSpecs, List<ValueSpec<?>> namedSpecs) {
        this.name = name;
        this.positionalSpecs = List.copyOf(positionalSpecs);
        this.namedSpecs = List.copyOf(namedSpecs);
    }

    public ParsedCommand parse(String rawArguments) {
        return parse(tokenize(rawArguments));
    }

    public ParsedCommand parse(BasicArgs args) {
        Map<String, Object> values = new LinkedHashMap<>();
        parsePositionals(args, values);
        parseNamed(args, values);
        return new ParsedCommand(values);
    }

    private static BasicArgs tokenize(String rawArguments) {
        Input input = new Input(rawArguments);
        List<Input.Value> tokens = new ArrayList<>();
        while (true) {
            Input.Value value = input.parseValue().orElse(null);
            if (value == null) {
                break;
            }
            tokens.add(value);
        }

        BasicArgs args = new BasicArgs(new ArrayList<>(), new LinkedHashMap<>());
        for (int i = 0; i < tokens.size(); i++) {
            Input.Value token = tokens.get(i);
            switch (token) {
                case Input.Value.Literal(String literal) -> args.positional().add(literal);
                case Input.Value.QuotedString(String quoted) -> args.positional().add(quoted);
                case Input.Value.SingleFlag(String name) -> i += attachNamedValue(args, tokens, i, name);
                case Input.Value.DoubleFlag(String name) -> i += attachNamedValue(args, tokens, i, name);
            }
        }
        return args;
    }

    private static int attachNamedValue(BasicArgs args, List<Input.Value> tokens, int index, String name) {
        if (index + 1 >= tokens.size()) {
            args.named().put(name, "");
            return 0;
        }

        Input.Value next = tokens.get(index + 1);
        switch (next) {
            case Input.Value.Literal(String literal) -> {
                args.named().put(name, literal);
                return 1;
            }
            case Input.Value.QuotedString(String quoted) -> {
                args.named().put(name, quoted);
                return 1;
            }
            case Input.Value.SingleFlag(String ignoredSingle) -> {
                args.named().put(name, "");
                return 0;
            }
            case Input.Value.DoubleFlag(String ignoredDouble) -> {
                args.named().put(name, "");
                return 0;
            }
        }
    }

    private void parsePositionals(BasicArgs args, Map<String, Object> values) {
        if (args.positional().size() > positionalSpecs.size()) {
            throw new RuntimeException("Command '" + name + "' received too many positional arguments.");
        }

        for (int i = 0; i < positionalSpecs.size(); i++) {
            ValueSpec<?> spec = positionalSpecs.get(i);
            if (i < args.positional().size()) {
                values.put(spec.name(), spec.type().parse(args.positional().get(i)));
            } else if (spec.required()) {
                throw new RuntimeException("Missing positional argument '" + spec.name() + "'.");
            } else {
                values.put(spec.name(), spec.defaultValue());
            }
        }
    }

    private void parseNamed(BasicArgs args, Map<String, Object> values) {
        for (Map.Entry<String, String> entry : args.named().entrySet()) {
            ValueSpec<?> spec = resolveNamed(entry.getKey());
            if (spec == null) {
                throw new RuntimeException("Unknown named argument '" + entry.getKey() + "'.");
            }

            String raw = entry.getValue();
            if (raw.isEmpty()) {
                throw new RuntimeException("Named argument '" + spec.name() + "' requires a value.");
            }

            values.put(spec.name(), spec.type().parse(raw));
        }

        for (ValueSpec<?> spec : namedSpecs) {
            if (!values.containsKey(spec.name())) {
                if (spec.required()) {
                    throw new RuntimeException("Missing named argument '" + spec.name() + "'.");
                }
                values.put(spec.name(), spec.defaultValue());
            }
        }
    }

    private ValueSpec<?> resolveNamed(String key) {
        for (ValueSpec<?> spec : namedSpecs) {
            if (spec.matchesName(key)) {
                return spec;
            }
        }
        return null;
    }
}