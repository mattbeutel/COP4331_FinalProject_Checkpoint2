package oop.project.library.scenarios;

import oop.project.library.argument.ArgumentType;
import oop.project.library.argument.ArgumentTypes;
import oop.project.library.argument.ParseFailure;
import oop.project.library.input.Input;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ArgumentScenarios {

    private static final ArgumentType<Integer> INT = ArgumentTypes.integer();
    private static final ArgumentType<Double> DOUBLE = ArgumentTypes.decimal();
    private static final ArgumentType<String> STRING = ArgumentTypes.string();
    private static final ArgumentType<java.time.LocalDate> DATE = ArgumentTypes.localDate();

    private static final ArgumentType<Integer> POSITIVE_INT =
            ArgumentTypes.integer().validate(ArgumentTypes.intRange(1, Integer.MAX_VALUE));

    private static final ArgumentType<String> DIFFICULTY =
            ArgumentTypes.string().validate(ArgumentTypes.choices("easy", "medium", "hard"));

    public static Map<String, Object> add(String arguments) throws RuntimeException {
        List<String> tokens = positionalTokens(arguments, "add", 2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("left", parse("add", "left", INT, tokens.get(0)));
        result.put("right", parse("add", "right", INT, tokens.get(1)));
        return result;
    }

    public static Map<String, Object> sub(String arguments) throws RuntimeException {
        List<String> tokens = positionalTokens(arguments, "sub", 2);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("left", parse("sub", "left", DOUBLE, tokens.get(0)));
        result.put("right", parse("sub", "right", DOUBLE, tokens.get(1)));
        return result;
    }

    public static Map<String, Object> fizzbuzz(String arguments) throws RuntimeException {
        List<String> tokens = positionalTokens(arguments, "fizzbuzz", 1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("number", parse("fizzbuzz", "number", POSITIVE_INT, tokens.get(0)));
        return result;
    }

    public static Map<String, Object> difficulty(String arguments) throws RuntimeException {
        List<String> tokens = positionalTokens(arguments, "difficulty", 1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("difficulty", parse("difficulty", "difficulty", DIFFICULTY, tokens.get(0)));
        return result;
    }

    public static Map<String, Object> date(String arguments) throws RuntimeException {
        List<String> tokens = positionalTokens(arguments, "date", 1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", parse("date", "date", DATE, tokens.get(0)));
        return result;
    }

    private static <T> T parse(String scenario, String name, ArgumentType<T> type, String raw) {
        try {
            return type.parse(raw);
        } catch (ParseFailure e) {
            throw new RuntimeException("Invalid " + scenario + " input: invalid " + name + ": " + e.getMessage(), e);
        }
    }

    private static List<String> positionalTokens(String arguments, String scenario, int expectedCount) {
        Input input = new Input(arguments);
        List<String> tokens = new ArrayList<>();

        while (true) {
            Input.Value value = input.parseValue().orElse(null);
            if (value == null) {
                break;
            }

            switch (value) {
                case Input.Value.Literal(String literal) -> tokens.add(literal);
                case Input.Value.QuotedString(String quoted) -> tokens.add(quoted);
                case Input.Value.SingleFlag(String name) ->
                        throw new RuntimeException("Invalid " + scenario + " input: unexpected named argument -" + name);
                case Input.Value.DoubleFlag(String name) ->
                        throw new RuntimeException("Invalid " + scenario + " input: unexpected named argument --" + name);
            }
        }

        if (tokens.size() != expectedCount) {
            throw new RuntimeException(
                    "Invalid " + scenario + " input: expected " + expectedCount + " argument(s) but got " + tokens.size()
            );
        }

        return tokens;
    }

}