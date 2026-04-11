package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentTypes;
import oop.project.library.command.CommandSpec;
import oop.project.library.command.ValueSpec;

import java.util.Map;

public final class CommandScenarios {

    private static final CommandSpec MUL = CommandSpec.builder("mul")
            .positional(ValueSpec.positional("left", ArgumentTypes.integer()))
            .positional(ValueSpec.positional("right", ArgumentTypes.integer()))
            .build();

    private static final CommandSpec DIV = CommandSpec.builder("div")
            .named(ValueSpec.named("left", ArgumentTypes.decimal()))
            .named(ValueSpec.named("right", ArgumentTypes.decimal()))
            .build();

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        return parse("mul", MUL, arguments);
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        return parse("div", DIV, arguments);
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO");
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO");
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        throw new UnsupportedOperationException("TODO");
    }

    private static Map<String, Object> parse(String scenario, CommandSpec spec, String arguments) {
        try {
            return spec.parse(arguments).asMap();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid " + scenario + " input: " + e.getMessage(), e);
        }
    }
}