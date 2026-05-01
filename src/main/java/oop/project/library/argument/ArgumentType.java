package oop.project.library.argument;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Parses a single raw string token into a typed value.
 */
public interface ArgumentType<T> {

    /**
     * Human-readable name for the kind of value this argument parses.
     */
    String description();

    /**
     * Parses a single raw token into a typed value.
     *
     * @param raw raw user input for one argument value
     * @return parsed typed value
     * @throws ParseException when the token is not a valid value for this argument type
     */
    T parse(String raw) throws ParseException;

    default ArgumentType<T> validate(Validator<? super T> validator) {
        ArgumentType<T> self = this;
        return new SimpleArgumentType<>(description(), raw -> {
            T value = self.parse(raw);
            validator.validate(value);
            return value;
        });
    }

    default ArgumentType<T> validate(Predicate<? super T> predicate, String message) {
        return validate(value -> {
            if (!predicate.test(value)) {
                throw new ParseException(message);
            }
        });
    }

    default <R> ArgumentType<R> map(String description, Function<? super T, ? extends R> mapper) {
        ArgumentType<T> self = this;
        return new SimpleArgumentType<>(description, raw -> mapper.apply(self.parse(raw)));
    }

}
