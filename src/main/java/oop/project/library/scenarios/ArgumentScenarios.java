package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentTypes;
import oop.project.library.command.CommandSpec;
import oop.project.library.command.ParsedCommand;
import oop.project.library.command.ValueSpec;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public final class ArgumentScenarios {

    private enum Difficulty {
        PEACEFUL,
        EASY,
        NORMAL,
        HARD
    }

    private static final CommandSpec ADD = CommandSpec.builder("add")
            .positional(ValueSpec.positional("left", ArgumentTypes.integer()))
            .positional(ValueSpec.positional("right", ArgumentTypes.integer()))
            .build();

    private static final CommandSpec SUB = CommandSpec.builder("sub")
            .positional(ValueSpec.positional("left", ArgumentTypes.decimal()))
            .positional(ValueSpec.positional("right", ArgumentTypes.decimal()))
            .build();

    private static final CommandSpec FIZZBUZZ = CommandSpec.builder("fizzbuzz")
            .positional(ValueSpec.positional("number", ArgumentTypes.integer().validate(ArgumentTypes.intRange(1, 100))))
            .build();

    private static final CommandSpec DIFFICULTY = CommandSpec.builder("difficulty")
            .positional(ValueSpec.positional("difficulty", ArgumentTypes.enumType(Difficulty.class)))
            .build();

    private static final CommandSpec DATE = CommandSpec.builder("date")
            .positional(ValueSpec.positional("date", ArgumentTypes.localDate()))
            .build();

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            ParsedCommand parsed = ADD.parse(arguments);
            int left = parsed.getInt("left");
            int right = parsed.getInt("right");
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid add input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        try {
            ParsedCommand parsed = SUB.parse(arguments);
            double left = parsed.getDouble("left");
            double right = parsed.getDouble("right");
            return Map.of("left", left, "right", right);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid sub input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            ParsedCommand parsed = FIZZBUZZ.parse(arguments);
            int number = parsed.getInt("number");
            return Map.of("number", number);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid fizzbuzz input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            ParsedCommand parsed = DIFFICULTY.parse(arguments);
            Difficulty difficulty = parsed.get("difficulty", Difficulty.class);
            return Map.of("difficulty", difficulty.name().toLowerCase(Locale.ROOT));
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid difficulty input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            ParsedCommand parsed = DATE.parse(arguments);
            LocalDate date = parsed.get("date", LocalDate.class);
            return Map.of("date", date);
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid date input: " + e.getMessage(), e);
        }
    }

}
