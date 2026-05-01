package oop.project.library.argument;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * Built-in argument parsers and factories for custom parsers.
 */
public final class ArgumentTypes {

    public static final ArgumentType<Boolean> BOOLEAN = custom("boolean", raw -> {
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        throw new ParseException("Expected boolean true/false but got '" + raw + "'.");
    });

    public static final ArgumentType<Integer> INTEGER = custom("integer", raw -> {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ParseException("Expected integer but got '" + raw + "'.", e);
        }
    });

    public static final ArgumentType<Double> DECIMAL = custom("double", raw -> {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new ParseException("Expected double but got '" + raw + "'.", e);
        }
    });

    public static final ArgumentType<String> STRING = custom("string", Function.identity());

    private ArgumentTypes() {}

    public static ArgumentType<Boolean> bool() {
        return BOOLEAN;
    }

    public static ArgumentType<Integer> integer() {
        return INTEGER;
    }

    public static ArgumentType<Double> decimal() {
        return DECIMAL;
    }

    public static ArgumentType<String> string() {
        return STRING;
    }

    public static <E extends Enum<E>> ArgumentType<E> enumType(Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");
        return custom(enumClass.getSimpleName(), raw -> {
            for (E constant : enumClass.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(raw)) {
                    return constant;
                }
            }
            throw new ParseException(
                    "Expected one of " + Arrays.toString(enumClass.getEnumConstants()) + " but got '" + raw + "'."
            );
        });
    }

    public static <E extends Enum<E>> ArgumentType<String> enumNameLowercase(Class<E> enumClass) {
        return enumType(enumClass).map(enumClass.getSimpleName().toLowerCase(Locale.ROOT),
                value -> value.name().toLowerCase(Locale.ROOT));
    }

    /**
     * Creates a custom argument parser with a descriptive type name used in error messages.
     *
     * @param description short human-readable description of the value being parsed
     * @param parser parsing callback for the raw token
     * @return argument type backed by the supplied callback
     */
    public static <T> ArgumentType<T> custom(String description, Function<String, T> parser) {
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(parser, "parser");
        return new SimpleArgumentType<>(description, raw -> {
            try {
                return parser.apply(raw);
            } catch (ParseException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new ParseException("Failed to parse " + description + " from '" + raw + "'.", e);
            }
        });
    }

}
