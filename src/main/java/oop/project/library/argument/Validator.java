package oop.project.library.argument;

/**
 * Validates an already-parsed value and throws a {@link ParseException} when it is unacceptable.
 */
@FunctionalInterface
public interface Validator<T> {

    void validate(T value) throws ParseException;

}
