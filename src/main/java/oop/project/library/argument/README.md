# Argument System

Handles parsing a single `String` token into typed data.

## Development Notes

- `ArgumentType<T>` remains the main polymorphic abstraction. It models exactly one concern: `String -> Parsed Value`.
- `ParseException` is unchecked because parse failures represent invalid user input at runtime, not a recoverable compile-time contract violation. The scenarios catch it at the boundary where user-facing errors are produced.
- `ArgumentTypes` now exposes stable built-in parsers as constants plus small convenience accessors for common use.
- `Validators` mirrors `ArgumentTypes` so reusable validation logic is discoverable without putting every helper on the parser namespace itself.
- Numeric range validation is implemented generically through `Validators.range(...)`, with `integerRange(...)` and `doubleRange(...)` as convenience wrappers.
- `LocalDate` parsing was removed from the library surface and is now expressed as a custom argument in the scenario, which better matches the assignment's expectation that domain-specific types live in user code.

## Feature Showcase

- `ticket AAA-1234`
    - Demonstrates a custom parser that combines regex matching and structured parsing into a domain value rather than returning a raw `String`.
    - Returned map: `{prefix=AAA, number=1234}`

## MLP Design Analysis

### Individual Review (Argument Lead)

Good:
- `ArgumentType<T>` stays lightweight and focused, so adding new parsers does not require subclass trees or command-specific logic.
- Separating `Validators` from `ArgumentTypes` keeps the API easier to navigate while still allowing parse-and-validate composition.

Less good:
- The library still uses callback-based composition for many features, which is flexible but may feel abstract to first-time users.
- `description()` is currently used mainly for documentation and error context rather than for richer generated help text.

### Individual Review (Command Lead)

Good:
- The argument layer no longer knows anything about argument names or command structure.
- Custom types remain easy to define from user code, which keeps the library useful beyond the fixed scenarios.

Less good:
- There is still some overlap between parsing and validation because validators wrap parser results; this is acceptable, but the boundary is conceptual rather than enforced by different object types.
- Showcase-level structured values currently rely on scenario-local records rather than library-provided domain containers.

### Team Review

- We agree that moving generic validation helpers into `Validators` made the public API easier to understand.
- We still see room to improve discoverability around composing custom parsers and validators for brand-new users.
