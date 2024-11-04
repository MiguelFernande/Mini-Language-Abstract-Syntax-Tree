//token class used for containing tokens in OOP.

public class Token {
    private TokenKind kind;
    private String lexeme;
    private Object value;
    private String position;

    // constructor
    public Token(TokenKind kind, String lexeme, Object value, String position) {
        this.lexeme = lexeme;
        this.kind = kind;
        this.value = value;
        this.position = position;
    }

    public Token(TokenKind kind, String lexeme, String position) {
        this(kind, lexeme, determineValue(kind, lexeme), position);
    }

    // used for determinign if it is a identifier or an int
    private static String determineValue(TokenKind kind, String lexeme) {
        if (kind == TokenKind.IDENTIFIER || kind == TokenKind.INTEGER_LITERAL) {
            return lexeme;
        } else {
            return null;
        }
    }

    // getters
    public TokenKind getKind() {
        return kind;
    }

    public Object getValue() {
        return value;
    }

    public String getPosition() {
        return position;
    }

    public String getLexeme() {
        return lexeme;
    }

    // setters
    public void setKind(TokenKind kind) {
        this.kind = kind;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public String toString() {
        return position + " " + kind + " " + value;
    }
}
