package oop.project.library.argument;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public final class ArgumentTypes {

    private ArgumentTypes() {}

    public static ArgumentType<Boolean> bool() {
        return custom("boolean", raw -> {
            if ("true".equals(raw)) {
                return true;
            }
            if ("false".equals(raw)) {
                return false;
            }
            throw new ParseFailure("Expected boolean true/false but got '" + raw + "'.");
        });
    }

    public static ArgumentType<Integer> integer() {
        return custom("integer", raw -> {
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                throw new ParseFailure("Expected integer but got '" + raw + "'.", e);
            }
        });
    }

    public static ArgumentType<Double> decimal() {
        return custom("double", raw -> {
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                throw new ParseFailure("Expected double but got '" + raw + "'.", e);
            }
        });
    }

    public static ArgumentType<String> string() {
        return custom("string", Function.identity());
    }

    public static ArgumentType<LocalDate> localDate() {
        return custom("date", raw -> {
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                throw new ParseFailure("Expected ISO local date but got '" + raw + "'.", e);
            }
        });
    }

    public static <E extends Enum<E>> ArgumentType<E> enumType(Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");
        return custom(enumClass.getSimpleName(), raw -> {
            try {
                return Enum.valueOf(enumClass, raw);
            } catch (IllegalArgumentException e) {
                throw new ParseFailure(
                        "Expected one of " + Arrays.toString(enumClass.getEnumConstants()) + " but got '" + raw + "'.",
                        e
                );
            }
        });
    }

    public static <T> ArgumentType<T> custom(String description, Function<String, T> parser) {
        return new SimpleArgumentType<>(description, raw -> {
            try {
                return parser.apply(raw);
            } catch (ParseFailure e) {
                throw e;
            } catch (RuntimeException e) {
                throw new ParseFailure("Failed to parse " + description + " from '" + raw + "'.", e);
            }
        });
    }

}