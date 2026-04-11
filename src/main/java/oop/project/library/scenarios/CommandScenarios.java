package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentTypes;
import oop.project.library.command.CommandSpec;
import oop.project.library.command.ValueSpec;

import java.util.LinkedHashMap;
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

    private static final CommandSpec ECHO = CommandSpec.builder("echo")
            .positional(ValueSpec.positionalWithDefault("message", ArgumentTypes.string(), "echo,echo,echo..."))
            .build();

    private static final CommandSpec SEARCH = CommandSpec.builder("search")
            .positional(ValueSpec.positional("term", ArgumentTypes.string()))
            .named(ValueSpec.flag("case-insensitive", "i"))
            .build();

    private static final CommandSpec DISPATCH_STATIC = CommandSpec.builder("static")
            .positional(ValueSpec.positional("value", ArgumentTypes.integer()))
            .build();

    private static final CommandSpec DISPATCH_DYNAMIC = CommandSpec.builder("dynamic")
            .positional(ValueSpec.positional("value", ArgumentTypes.string()))
            .build();

    private static final CommandSpec DISPATCH = CommandSpec.builder("dispatch")
            .subcommand("static", DISPATCH_STATIC)
            .subcommand("dynamic", DISPATCH_DYNAMIC)
            .build();

    public static Map<String, Object> mul(String arguments) throws RuntimeException {
        return parse("mul", MUL, arguments);
    }

    public static Map<String, Object> div(String arguments) throws RuntimeException {
        return parse("div", DIV, arguments);
    }

    public static Map<String, Object> echo(String arguments) throws RuntimeException {
        return parse("echo", ECHO, arguments);
    }

    public static Map<String, Object> search(String arguments) throws RuntimeException {
        return parse("search", SEARCH, arguments);
    }

    public static Map<String, Object> dispatch(String arguments) throws RuntimeException {
        try {
            String trimmed = arguments.trim();
            int firstSpace = trimmed.indexOf(' ');
            if (firstSpace == -1) {
                throw new RuntimeException("Dispatch requires a subcommand.");
            }

            String type = trimmed.substring(0, firstSpace);
            Map<String, Object> parsed = DISPATCH.parse(arguments).asMap();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("type", type);
            result.putAll(parsed);
            return result;
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid dispatch input: " + e.getMessage(), e);
        }
    }

    private static Map<String, Object> parse(String scenario, CommandSpec spec, String arguments) {
        try {
            return spec.parse(arguments).asMap();
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid " + scenario + " input: " + e.getMessage(), e);
        }
    }
}