//lexer of the project responsible for creating tokens.

import java.io.*;

public class Lexer {

    private FileReader fileReader;
    private char currentChar;

    public Lexer(FileReader fileReader) {
        this.fileReader = fileReader;

        try {
            this.currentChar = fileReader.getCurrentChar();
        } catch (IOException e) {
            System.out.println("Error at : " + currentChar + " at " + fileReader.position());
        }
    }
    
    //next token this method compiles chars and uses them to generate tokens. using string builder.
    public Token nextToken() throws IOException, LexicalException {

        skipWhitespace();
        String startPos = fileReader.position();

        // checking for comments
        if (currentChar == '/' && fileReader.peekNextChar() == '/') {
            skipComment();
            skipWhitespace();
            return nextToken(); // get the next token after the comment
        }
        
        // End of file check - return END_OF_TEXT if EOF is reached
        if (!fileReader.isReady())
        {
        return new Token(TokenKind.END_OF_TEXT, null, startPos);
        }

        // Check for and handle the '.' character as PERIOD token
        if (currentChar == '.') {
        fileReader.nextChar(); // Consume the '.' character
        return new Token(TokenKind.PERIOD, ".", startPos);
        }


        //this is to create it also accounts for if there are numbers within the ID such as w2djfs. 
        if (Character.isLetter(currentChar)) {
            StringBuilder id = new StringBuilder();

            while (Character.isLetter(currentChar) || currentChar == '_' || Character.isDigit(currentChar)) {
                id.append(currentChar);
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar();
            }

            String idString = id.toString();

            //if it happens to be a key word return that key word
            if (isKeyword(idString)) {
                if (idString.equals("true") || idString.equals("false")) {
                    return new Token(TokenKind.BOOLEAN_LITERAL, idString, startPos);
                }
                return new Token(TokenKind.valueOf(idString.toUpperCase()), idString, idString, startPos);
            }
            return new Token(TokenKind.IDENTIFIER, idString, startPos);
        }

        //this makes numbers. If there is only numbers send out a number token. If there is a leter in it anywhere send out and Identifier.
        if (Character.isDigit(currentChar)) {
            StringBuilder num = new StringBuilder();
            while (Character.isDigit(currentChar)) {
                num.append(currentChar);
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar();
            }

            if (Character.isLetter(currentChar) || currentChar == '_') {
                while (Character.isLetter(currentChar) || currentChar == '_' || Character.isDigit(currentChar)) {
                    num.append(currentChar);
                    fileReader.nextChar();
                    currentChar = fileReader.getCurrentChar();
                }
                return new Token(TokenKind.IDENTIFIER, num.toString(), startPos);
            } else {
                return new Token(TokenKind.INTEGER_LITERAL, num.toString(), startPos);
            }
        }

        // for symbols
        Token token = processSymbols(startPos);
        if (token != null) {
        return token;
        }

        // end of text recognition the if statement is to make it so the bottom but of code is reachable.
        if (!fileReader.isReady()) {
            return new Token(TokenKind.END_OF_TEXT, "EOT", fileReader.position());
        }
        //final edge case for anything that slips by all the code
        throw new LexicalException("Unexpected character : " + currentChar + " at " + startPos);
    }

    //method for proccesing symbols simply returns a token if it a a symbol token along with the TOKENKIND 
    //and what it is plus where it is. if it doesnt exist put out an error.
    private Token processSymbols(String startPos) throws IOException, LexicalException
    {
        switch (currentChar) {
            case '+':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.PLUS, "+", startPos);
            case '-':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.MINUS, "-", startPos);
            case '*':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.ASTERISK, "*", startPos);
            case '/':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.SLASH, "/", startPos);
            case '(':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.LEFT_PAREN, "(", startPos);
            case ')':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.RIGHT_PAREN, ")", startPos);
            case '<':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.LESS_THAN, "<", startPos);
            case '>':
                if (fileReader.peekNextChar() == '=') {
                fileReader.nextChar();
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.GREATER_THAN_EQUAL, ">=", startPos);
                } else {
                    fileReader.nextChar();
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.GREATER_THAN, ">", startPos);
                }
            case '=':
                if (fileReader.peekNextChar() == '<') {
                    fileReader.nextChar(); // consuming '='
                    fileReader.nextChar(); // consuming '<'
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.LESS_THAN_EQUAL, "=<", startPos);
                } else {
                    fileReader.nextChar();
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.EQUAL, "=", startPos);
                }
            case '!':
                if (fileReader.peekNextChar() == '=') {
                    fileReader.nextChar(); // consuming '!'
                    fileReader.nextChar(); // consuming '='
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.NOT_EQUAL, "!=", startPos);
                } else {
                    throw new LexicalException("Unexpected character : " + currentChar + " at " + startPos);
                }
                case ',':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.COMMA, "-", startPos);
            case ':':
                if (fileReader.peekNextChar() == '=') {
                    fileReader.nextChar(); // consuming ':'
                    fileReader.nextChar(); // consuming '='
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.ASSIGN, ":=", startPos);
                } else {
                    fileReader.nextChar();
                    currentChar = fileReader.getCurrentChar();
                    return new Token(TokenKind.COLON, ":", startPos);
                }
            case ';':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.SEMICOLON, ";", startPos);
            case '.':
                fileReader.nextChar();
                currentChar = fileReader.getCurrentChar(); 
                return new Token(TokenKind.PERIOD, ".", startPos);
            default:
                throw new LexicalException("Unexpected character : " + currentChar + " at " + startPos);
        }
    }

    //simple method to check if the token is a key word.
    private boolean isKeyword(String str) {
        return str.equals("program") ||
                str.equals("bool") ||
                str.equals("int") ||
                str.equals("if") ||
                str.equals("then") ||
                str.equals("else") ||
                str.equals("end") ||
                str.equals("while") ||
                str.equals("do") ||
                str.equals("print") ||            
                str.equals("or") ||
                str.equals("mod") ||
                str.equals("and") ||
                str.equals("false") ||
                str.equals("true") ||
                str.equals("not");
    }

    //skip whitespace can be used often just to reomve any white space
    private void skipWhitespace() throws IOException {
        while (Character.isWhitespace(currentChar)) {
            fileReader.nextChar();
            currentChar = fileReader.getCurrentChar();
        }
    }
    
    //skipping comments it assumes it is used when a comment is detected.
    private void skipComment() throws IOException {
        fileReader.nextChar();
        currentChar = fileReader.getCurrentChar();

        // read until end of line/file
        while (currentChar != '\n' && currentChar != '\r' && fileReader.isReady()) {
            fileReader.nextChar();
            currentChar = fileReader.getCurrentChar();
        }

        // incase of any line ends biz
        if (currentChar == '\r' && fileReader.peekNextChar() == '\n') {
            fileReader.nextChar(); // consume the '\n'
        }
    }
}