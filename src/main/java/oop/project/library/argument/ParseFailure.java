package oop.project.library.argument;

/**
 * Backwards-compatible alias for {@link ParseException}.
 *
 * @deprecated prefer {@link ParseException} for new code.
 */
@Deprecated
public class ParseFailure extends ParseException {

    public ParseFailure(String message) {
        super(message);
    }

    public ParseFailure(String message, Throwable cause) {
        super(message, cause);
    }

}
