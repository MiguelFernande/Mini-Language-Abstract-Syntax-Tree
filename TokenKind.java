// an enumeration to contain all token types to refreced to I hope I was able to get all token types of not it can be easily added to.

public enum TokenKind {
    // Keywords
    PROGRAM("program", "Keyword"),
    BOOL("bool", "Keyword"),
    INT("int", "Keyword"),
    IF("if", "Keyword"),
    THEN("then", "Keyword"),
    ELSE("else", "Keyword"),
    END("end", "Keyword"),
    WHILE("while", "Keyword"),
    DO("do", "Keyword"),
    PRINT("print", "Keyword"),

    // Operators and symbols
    COLON(":", "Symbol"),
    SEMICOLON(";", "Symbol"),
    ASSIGN(":=", "Operator"),
    LESS_THAN("<", "Operator"),
    LESS_THAN_EQUAL("=<", "Operator"),
    EQUAL("=", "Operator"),
    NOT_EQUAL("!=", "Operator"),
    GREATER_THAN(">", "Operator"),
    GREATER_THAN_EQUAL(">=", "Operator"),
    PLUS("+", "Operator"),
    MINUS("-", "Operator"),
    OR("or", "Operator"),
    ASTERISK("*", "Operator"),
    SLASH("/", "Operator"),
    MOD("mod", "Operator"),
    AND("and", "Operator"),
    NOT("not", "Operator"),
    LEFT_PAREN("(", "Symbol"),
    RIGHT_PAREN(")", "Symbol"),
    COMMA(",", "Symbol"),
    PERIOD(".", "Symbol"),

    // Literals and Identifiers
    BOOLEAN_LITERAL(null, "Boolean Literal"),
    INTEGER_LITERAL(null, "Integer Literal"),
    IDENTIFIER(null, "Identifier"),

    // Special 
    //never really need to error token type but it there if I do
    END_OF_TEXT( null, "End of Text"),
    ERROR(null, "Error");

    private final String lexeme;
    private final String description;

    TokenKind(String lexeme, String description) {
        this.lexeme = lexeme;
        this.description = description;
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public String getDescription() {
        return this.description;
    }
}
