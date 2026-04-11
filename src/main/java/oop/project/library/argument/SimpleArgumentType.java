package oop.project.library.argument;

import java.util.Objects;

public final class SimpleArgumentType<T> implements ArgumentType<T> {

    @FunctionalInterface
    public interface Parser<T> {
        T parse(String raw) throws ParseFailure;
    }

    private final String description;
    private final Parser<T> parser;

    public SimpleArgumentType(String description, Parser<T> parser) {
        this.description = Objects.requireNonNull(description, "description");
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public T parse(String raw) throws ParseFailure {
        return parser.parse(raw);
    }

}