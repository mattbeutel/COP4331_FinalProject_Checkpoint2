package oop.project.library.argument;

/**
 * Standard unchecked exception used when library input cannot be parsed.
 * <p>
 * Parsing failures are treated as programming-independent user input errors, so callers are
 * expected to handle them at scenario boundaries rather than be forced to declare them at every use site.
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
