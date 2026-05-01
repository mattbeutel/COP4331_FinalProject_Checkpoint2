package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentType;
import oop.project.library.argument.ArgumentTypes;
import oop.project.library.argument.ParseException;
import oop.project.library.argument.Validators;
import oop.project.library.command.CommandSpec;
import oop.project.library.command.ParsedCommand;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ArgumentScenarios {

    private enum Difficulty {
        PEACEFUL,
        EASY,
        NORMAL,
        HARD
    }

    private record TicketCode(String prefix, int number) {}

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        try {
            CommandSpec command = CommandSpec.builder("add")
                    .addPositional("left", ArgumentTypes.integer())
                    .addPositional("right", ArgumentTypes.integer())
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            int left = parsed.getInt("left");
            int right = parsed.getInt("right");
            return Map.of("left", left, "right", right);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid add input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        try {
            CommandSpec command = CommandSpec.builder("sub")
                    .addPositional("left", ArgumentTypes.decimal())
                    .addPositional("right", ArgumentTypes.decimal())
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            double left = parsed.getDouble("left");
            double right = parsed.getDouble("right");
            return Map.of("left", left, "right", right);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid sub input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        try {
            CommandSpec command = CommandSpec.builder("fizzbuzz")
                    .addPositional("number", ArgumentTypes.integer().validate(Validators.integerRange(1, 100)))
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            int number = parsed.getInt("number");
            return Map.of("number", number);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid fizzbuzz input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        try {
            CommandSpec command = CommandSpec.builder("difficulty")
                    .addPositional("difficulty", ArgumentTypes.enumType(Difficulty.class))
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            Difficulty difficulty = parsed.get("difficulty", Difficulty.class);
            return Map.of("difficulty", difficulty.name().toLowerCase(Locale.ROOT));
        } catch (ParseException e) {
            throw new RuntimeException("Invalid difficulty input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        try {
            ArgumentType<LocalDate> localDate = ArgumentTypes.custom("ISO local date", raw -> {
                try {
                    return LocalDate.parse(raw);
                } catch (DateTimeParseException e) {
                    throw new ParseException("Expected ISO local date but got '" + raw + "'.", e);
                }
            });
            CommandSpec command = CommandSpec.builder("date")
                    .addPositional("date", localDate)
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            LocalDate date = parsed.get("date", LocalDate.class);
            return Map.of("date", date);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date input: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> ticket(String arguments) throws RuntimeException {
        try {
            Pattern ticketPattern = Pattern.compile("([A-Z]{3})-(\\d{4})");
            ArgumentType<TicketCode> ticketCode = ArgumentTypes.custom("ticket code", raw -> {
                Matcher matcher = ticketPattern.matcher(raw);
                if (!matcher.matches()) {
                    throw new ParseException("Expected ticket code matching 'AAA-1234' but got '" + raw + "'.");
                }
                return new TicketCode(matcher.group(1), Integer.parseInt(matcher.group(2)));
            });
            CommandSpec command = CommandSpec.builder("ticket")
                    .addPositional("code", ticketCode)
                    .build();
            ParsedCommand parsed = command.parse(arguments);
            TicketCode code = parsed.get("code", TicketCode.class);
            return Map.of("prefix", code.prefix(), "number", code.number());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid ticket input: " + e.getMessage(), e);
        }
    }

}
