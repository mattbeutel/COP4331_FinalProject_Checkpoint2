package oop.project.library.input;

import oop.project.library.argument.ParseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public final class Input {

    public sealed interface Value {
        record Literal(String value) implements Value {}
        record QuotedString(String value) implements Value {}
        record SingleFlag(String name) implements Value {}
        record DoubleFlag(String name) implements Value {}
    }

    private final char[] chars;
    private int index = 0;

    public Input(String input) {
        chars = input.toCharArray();
    }

    public BasicArgs parseBasicArgs() {
        List<Value> tokens = new ArrayList<>();
        while (true) {
            Value value = parseValue().orElse(null);
            if (value == null) {
                break;
            }
            tokens.add(value);
        }

        BasicArgs args = new BasicArgs(new ArrayList<>(), new LinkedHashMap<>());
        for (int tokenIndex = 0; tokenIndex < tokens.size(); tokenIndex++) {
            Value token = tokens.get(tokenIndex);
            if (token instanceof Value.Literal literal) {
                args.positional().add(literal.value());
            } else if (token instanceof Value.QuotedString quotedString) {
                args.positional().add(quotedString.value());
            } else if (token instanceof Value.SingleFlag singleFlag) {
                tokenIndex += attachNamedValue(args, tokens, tokenIndex, singleFlag.name());
            } else if (token instanceof Value.DoubleFlag doubleFlag) {
                tokenIndex += attachNamedValue(args, tokens, tokenIndex, doubleFlag.name());
            }
        }
        return args;
    }

    public Optional<Value> parseValue() {
        while (index < chars.length && chars[index] == ' ') {
            index++;
        }
        if (index >= chars.length) {
            return Optional.empty();
        }

        if (chars[index] == '"') {
            int start = index;
            do {
                index++;
            } while (index < chars.length && chars[index] != '"');
            if (index >= chars.length) {
                throw new ParseException("Unterminated quoted string @ index " + start + ".");
            }
            String value = new String(chars, start + 1, index - start - 1);
            index++;
            return Optional.of(new Value.QuotedString(value));
        }

        int start = index;
        do {
            index++;
        } while (index < chars.length && chars[index] != ' ' && chars[index] != '"');
        if (index < chars.length && chars[index] == '"') {
            throw new ParseException("Invalid quote within literal @ index " + index + ".");
        }

        String value = new String(chars, start, index - start);
        if (value.startsWith("--") && value.length() > 2 && Character.isLetter(value.charAt(2))) {
            return Optional.of(new Value.DoubleFlag(value.substring(2)));
        }
        if (value.startsWith("-") && value.length() > 1 && Character.isLetter(value.charAt(1))) {
            return Optional.of(new Value.SingleFlag(value.substring(1)));
        }
        return Optional.of(new Value.Literal(value));
    }

    private static int attachNamedValue(BasicArgs args, List<Value> tokens, int tokenIndex, String rawName) {
        String name = rawName;
        int equalsIndex = rawName.indexOf('=');
        if (equalsIndex >= 0) {
            name = rawName.substring(0, equalsIndex);
            String inlineValue = rawName.substring(equalsIndex + 1);
            args.named().put(name, inlineValue);
            return 0;
        }

        if (tokenIndex + 1 >= tokens.size()) {
            args.named().put(name, "");
            return 0;
        }

        Value next = tokens.get(tokenIndex + 1);
        if (next instanceof Value.Literal literal) {
            args.named().put(name, literal.value());
            return 1;
        }
        if (next instanceof Value.QuotedString quotedString) {
            args.named().put(name, quotedString.value());
            return 1;
        }
        args.named().put(name, "");
        return 0;
    }

}
