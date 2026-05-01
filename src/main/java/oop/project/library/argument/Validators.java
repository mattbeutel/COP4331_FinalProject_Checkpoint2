package oop.project.library.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Reusable validators that can be composed onto {@link ArgumentType}s.
 */
public final class Validators {

    private Validators() {}

    public static <T extends Comparable<? super T>> Validator<T> range(T minInclusive, T maxInclusive) {
        Objects.requireNonNull(minInclusive, "minInclusive");
        Objects.requireNonNull(maxInclusive, "maxInclusive");
        if (minInclusive.compareTo(maxInclusive) > 0) {
            throw new IllegalArgumentException("Minimum range bound cannot be greater than the maximum bound.");
        }
        return value -> {
            if (value.compareTo(minInclusive) < 0 || value.compareTo(maxInclusive) > 0) {
                throw new ParseException(
                        "Expected value in range [" + minInclusive + ", " + maxInclusive + "] but got " + value + "."
                );
            }
        };
    }

    /**
     * Creates a validator for integer values within an inclusive range.
     */
    public static Validator<Integer> integerRange(int minInclusive, int maxInclusive) {
        return range(minInclusive, maxInclusive);
    }

    /**
     * Creates a validator for decimal values within an inclusive range.
     */
    public static Validator<Double> doubleRange(double minInclusive, double maxInclusive) {
        return range(minInclusive, maxInclusive);
    }

    public static <T> Validator<T> choices(Collection<? extends T> allowedValues) {
        Objects.requireNonNull(allowedValues, "allowedValues");
        Set<T> allowed = Set.copyOf(allowedValues);
        return value -> {
            if (!allowed.contains(value)) {
                throw new ParseException("Expected one of " + allowed + " but got '" + value + "'.");
            }
        };
    }

    public static Validator<String> choices(String... allowedValues) {
        return choices(new LinkedHashSet<>(Arrays.asList(allowedValues)));
    }

    public static Validator<String> matchesRegex(String regex) {
        return matchesRegex(Pattern.compile(regex));
    }

    public static Validator<String> matchesRegex(Pattern pattern) {
        Objects.requireNonNull(pattern, "pattern");
        return value -> {
            if (!pattern.matcher(value).matches()) {
                throw new ParseException("Expected string matching regex '" + pattern.pattern() + "' but got '" + value + "'.");
            }
        };
    }

}
