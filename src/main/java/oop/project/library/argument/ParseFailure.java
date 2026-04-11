package oop.project.library.argument;

/**
 * Library-level parse/validation failure.
 */
public class ParseFailure extends RuntimeException {

    public ParseFailure(String message) {
        super(message);
    }

    public ParseFailure(String message, Throwable cause) {
        super(message, cause);
    }

}