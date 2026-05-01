package oop.project.library.argument;

import java.util.Objects;

/**
 * Small reusable {@link ArgumentType} implementation backed by a parser callback.
 */
public record SimpleArgumentType<T>(String description, Parser<T> parser) implements ArgumentType<T> {

    @FunctionalInterface
    public interface Parser<T> {
        T parse(String raw) throws ParseException;
    }

    public SimpleArgumentType {
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(parser, "parser");
    }

    @Override
    public T parse(String raw) throws ParseException {
        return parser.parse(raw);
    }

}
