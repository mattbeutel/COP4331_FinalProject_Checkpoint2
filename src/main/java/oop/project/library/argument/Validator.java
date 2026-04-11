package oop.project.library.argument;

@FunctionalInterface
public interface Validator<T> {

    void validate(T value) throws ParseFailure;

}