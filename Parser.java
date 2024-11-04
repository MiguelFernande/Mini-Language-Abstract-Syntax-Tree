
//this is the parser it is responsible for taking in a program token by token from the lexer and determining if the program is syntaxicaly correct. 
//If it is do nothing if it isn't send out an error.
//The ast Node documentation will be at the bottom since it was implemented after all the comments about the base parser. 
//even with the AST List and hashmaps the program functions the same as before. 
import java.io.*;
import java.util.*;

//java apperently doent like varargs with generic types, this was the simplest method so I went wit suppresing the warning. 
//also the types are known and managed so it should be OK.
@SuppressWarnings("unchecked")

public class Parser {
    private Lexer lexer;
    private Token currentToken;

    // contructor
    public Parser(Lexer lexer) throws LexicalException, IOException {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    // function that is called on to parse. checks to see if the program is empty
    // before hand.
    public Map<String, Object> parse() throws ParseException, IOException, LexicalException {

        Map<String, Object> astRoot = new HashMap<>();

        if (currentToken.getKind() == TokenKind.END_OF_TEXT) {
            throw new ParseException("No Program detected or end of text was reached before any tokens were parsed");
        }

        astRoot = parseProgram();

        System.out.println("program was parsed with no errors");

        return astRoot;
    }

    // program parser checks to see if there is a period followed by end of text at
    // the end of the program
    private Map<String, Object> parseProgram() throws ParseException, IOException, LexicalException {

        match(TokenKind.PROGRAM);
        Map<String, Object> identifierNode = parseIdentifier();
        match(TokenKind.COLON);
        Map<String, Object> bodyNode = parseBody();

        if (currentToken.getKind() == TokenKind.PERIOD) {
            match(TokenKind.PERIOD);
            match(TokenKind.END_OF_TEXT);
            return createNode("Program", identifierNode, bodyNode);
        } else {
            throw new ParseException("Expected end of text after PERIOD but found " + currentToken.getKind() + " at "
                    + currentToken.getPosition());
        }
    }

    // conditional delcarations and parse statements
    private Map<String, Object> parseBody() throws ParseException, LexicalException, IOException {
        Map<String, Object> declarationsNode = new HashMap<>();

        if (currentToken.getKind() == TokenKind.BOOL || currentToken.getKind() == TokenKind.INT) {
            declarationsNode = parseDeclarations();
        }

        Map<String, Object> statementsNode = parseStatements();
        return createNode("Body", declarationsNode, statementsNode);

    }

    // parse a declaration immediatly and repeat as long and every decleration is
    // follower by an int or bool
    private Map<String, Object> parseDeclarations() throws ParseException, LexicalException, IOException {
        List<Map<String, Object>> declarationNodes = new ArrayList<>();
        while (currentToken.getKind() == TokenKind.BOOL || currentToken.getKind() == TokenKind.INT) {
            Map<String, Object> declarationNode = parseDeclaration();
            declarationNodes.add(declarationNode);
        }
        if (!declarationNodes.isEmpty()) {
            return createNode("Declarations", declarationNodes.toArray(new Map[0]));
        }
        return new HashMap<>(); // Return an empty map instead of null.
    }

    // Parses a single declaration. It checks for a type (bool/int) and then the identifiers.
    private Map<String, Object> parseDeclaration() throws ParseException, LexicalException, IOException {
        String type = currentToken.getKind() == TokenKind.BOOL ? "bool" : "int";

        match(currentToken.getKind());

        List<Map<String, Object>> identifiers = new ArrayList<>();

        Map<String, Object> identifierNode = parseIdentifier();
        identifiers.add(identifierNode);

        while (currentToken.getKind() == TokenKind.COMMA) {
            match(TokenKind.COMMA);
            identifierNode = parseIdentifier();
            identifiers.add(identifierNode);
        }

        match(TokenKind.SEMICOLON);
        return createNode("Declaration", new HashMap<String, Object>() {
            {
                put("type", type);
                put("identifiers", identifiers);
            }
        });
    }

// Parses statements, which can be assignment, conditional, iterative, or print statements.
    private Map<String, Object> parseStatements() throws ParseException, LexicalException, IOException {
        List<Map<String, Object>> statementNodes = new ArrayList<>();

        Map<String, Object> statementNode = parseStatement();
        statementNodes.add(statementNode);

        while (currentToken.getKind() == TokenKind.SEMICOLON) {

            match(TokenKind.SEMICOLON);

            statementNode = parseStatement();
            statementNodes.add(statementNode);

        }

        return createNode("Statements", statementNodes.toArray(new Map[0]));
    }

    // switch statement to see which kind of statement it is. if not error
    private Map<String, Object> parseStatement() throws ParseException, IOException, LexicalException {
        Map<String, Object> statementNode;
        switch (currentToken.getKind()) {
            case IDENTIFIER:
                statementNode = parseAssignmentStatement();
                break;
            case IF:
                statementNode = parseConditionalStatement();
                break;
            case WHILE:
                statementNode = parseIterativeStatement();
                break;
            case PRINT:
                statementNode = parsePrintStatement();
                break;
            default:
                throw new ParseException("Expected IF, IDENTIFIER, WHILE, or PRINT token but found "
                        + currentToken.getKind() + " at " + currentToken.getPosition());
        }

        return statementNode;
    }

    // simple parsing below for a while. Simply followed word for word of the mini
    // language.
    private Map<String, Object> parseAssignmentStatement() throws ParseException, IOException, LexicalException {
        Map<String, Object> identifierNode = parseIdentifier();
        match(TokenKind.ASSIGN);
        Map<String, Object> expressionNode = parseExpression();

        return createNode("AssignmentStatement", new HashMap<String, Object>() {
            {
                put("identifier", identifierNode);
                put("expression", expressionNode);
            }
        });
    }

    private Map<String, Object> parseConditionalStatement() throws ParseException, IOException, LexicalException {
        match(TokenKind.IF);
        Map<String, Object> conditionNode = parseExpression();
        match(TokenKind.THEN);
        Map<String, Object> thenBodyNode = parseBody();

        Map<String, Object> elseBodyNode = new HashMap<>();
        if (currentToken.getKind() == TokenKind.ELSE) {
            match(TokenKind.ELSE);
            elseBodyNode = parseBody();
        }

        match(TokenKind.END);

        HashMap<String, Object> conditionalExpression = new HashMap<>();
        conditionalExpression.put("type", "ConditionalStatement");
        conditionalExpression.put("condition", conditionNode);
        conditionalExpression.put("thenBody", thenBodyNode);
        if (elseBodyNode != null) {
            conditionalExpression.put("elseBody", elseBodyNode);
        }

        return createNode("ConditionalStatement", conditionalExpression);
    }

    private Map<String, Object> parseIterativeStatement() throws ParseException, IOException, LexicalException {
        match(TokenKind.WHILE);
        Map<String, Object> conditionNode = parseExpression();
        match(TokenKind.DO);
        Map<String, Object> bodyNode = parseBody();
        match(TokenKind.END);

        return createNode("IterativeStatement", new HashMap<String, Object>() {
            {
                put("condition", conditionNode);
                put("body", bodyNode);
            }
        });
    }

    private Map<String, Object> parsePrintStatement() throws ParseException, IOException, LexicalException {
        match(TokenKind.PRINT);
        Map<String, Object> expressionNode = parseExpression();

        return createNode("PrintStatement", new HashMap<String, Object>() {
            {
                put("expression", expressionNode);
            }
        });
    }

    // uses boolean isRelationalOperator function to see if the function is whats
    // needed for expressions.
    private Map<String, Object> parseExpression() throws ParseException, IOException, LexicalException {
        Map<String, Object> simpleExpressionNode = parseSimpleExpression();
        Map<String, Object> rightExpressionNode = new HashMap<>();

        if (isRelationalOperator(currentToken.getKind())) {
            String operator = currentToken.getLexeme();
            match(currentToken.getKind());
            rightExpressionNode = parseSimpleExpression();

            Map<String, Object> relationalExpression = new HashMap<>();
            relationalExpression.put("type", "RelationalExpression");
            relationalExpression.put("leftExpression", simpleExpressionNode);
            relationalExpression.put("operator", operator);
            relationalExpression.put("rightExpression", rightExpressionNode);

            return createNode("Expression", relationalExpression);
        } else {
            return simpleExpressionNode;
        }
    }

    // follows the formula as above.
    private Map<String, Object> parseSimpleExpression() throws ParseException, IOException, LexicalException {
        Map<String, Object> termNode = parseTerm();
        List<Map<String, Object>> expressionComponents = new ArrayList<>();
        expressionComponents.add(termNode);

        while (isAdditiveOperator(currentToken.getKind())) {
            String operator = currentToken.getLexeme();
            match(currentToken.getKind());
            Map<String, Object> nextTermNode = parseTerm();

            Map<String, Object> additiveExpression = new HashMap<>();
            additiveExpression.put("type", "AdditiveExpression");
            additiveExpression.put("operator", operator);
            additiveExpression.put("term", nextTermNode);

            expressionComponents.add(additiveExpression);
        }

        if (expressionComponents.size() == 1) {
            return termNode;
        } else {
            return createNode("SimpleExpression", expressionComponents.toArray(new Map[0]));
        }
    }

    // same as beofre follows the method above
    private Map<String, Object> parseTerm() throws ParseException, IOException, LexicalException {
        Map<String, Object> factorNode = parseFactor();
        List<Map<String, Object>> termComponents = new ArrayList<>();
        termComponents.add(factorNode);

        while (isMultiplicativeOperator(currentToken.getKind())) {
            String operator = currentToken.getLexeme();
            match(currentToken.getKind());
            Map<String, Object> nextFactorNode = parseFactor();

            Map<String, Object> multiplicativeExpression = new HashMap<>();
            multiplicativeExpression.put("type", "MultiplicativeExpression");
            multiplicativeExpression.put("operator", operator);
            multiplicativeExpression.put("factor", nextFactorNode);

            termComponents.add(multiplicativeExpression);
        }

        if (termComponents.size() == 1) {
            return factorNode;
        } else {
            return createNode("Term", termComponents.toArray(new Map[0]));
        }
    }

    // starts off with checking a unary operator then switch statement for literals,
    // Identifiers, and ( expression ) if not error
    private Map<String, Object> parseFactor() throws ParseException, IOException, LexicalException {
        if (isUnaryOperator(currentToken.getKind())) {
            String operator = currentToken.getLexeme();
            match(currentToken.getKind());
            Map<String, Object> factorNode = parseFactor();

            return createNode("UnaryExpression", new HashMap<String, Object>() {
                {
                    put("operator", operator);
                    put("factor", factorNode);
                }
            });
        } else {
            switch (currentToken.getKind()) {
                case BOOLEAN_LITERAL:
                case INTEGER_LITERAL:
                    return parseLiteral();
                case IDENTIFIER:
                    return parseIdentifier();
                case LEFT_PAREN:
                    match(TokenKind.LEFT_PAREN);
                    Map<String, Object> expressionNode = parseExpression();
                    match(TokenKind.RIGHT_PAREN);
                    return expressionNode;
                default:
                    throw new ParseException(
                            "Expected a Factor but found " + currentToken.getKind() + " at "
                                    + currentToken.getPosition());
            }
        }
    }

    // sorts between both literals a little unnecesarry but wanted to follow the
    // mini language to the tea.
    private Map<String, Object> parseLiteral() throws ParseException, IOException, LexicalException {
        if (currentToken.getKind() == TokenKind.BOOLEAN_LITERAL) {
            return parseBooleanLiteral();
        } else if (currentToken.getKind() == TokenKind.INTEGER_LITERAL) {
            return parseIntegerLiteral();
        } else {
            throw new ParseException(
                    "Expected a literal, but found " + currentToken.getKind() + " at " + currentToken.getPosition());
        }
    }

    // the next 3 methods are a little unnecesarry but again I wanted to follow the
    // mini language to the tea.
    private Map<String, Object> parseBooleanLiteral() throws ParseException, IOException, LexicalException {
        String value = currentToken.getLexeme();
        match(TokenKind.BOOLEAN_LITERAL);
        return createNode("BooleanLiteral", new HashMap<String, Object>() {
            {
                put("value", value);
            }
        });
    }

    private Map<String, Object> parseIntegerLiteral() throws ParseException, IOException, LexicalException {
        String value = currentToken.getLexeme();
        match(TokenKind.INTEGER_LITERAL);
        return createNode("IntegerLiteral", new HashMap<String, Object>() {
            {
                put("value", value);
            }
        });
    }

    private Map<String, Object> parseIdentifier() throws ParseException, IOException, LexicalException {
        String name = currentToken.getLexeme();
        match(TokenKind.IDENTIFIER);
        return createNode("Identifier", new HashMap<String, Object>() {
            {
                put("name", name);
            }
        });
    }

    // statements used to check operator types by some methods
    private boolean isRelationalOperator(TokenKind tokenKind) {
        return tokenKind == TokenKind.LESS_THAN ||
                tokenKind == TokenKind.LESS_THAN_EQUAL ||
                tokenKind == TokenKind.EQUAL ||
                tokenKind == TokenKind.NOT_EQUAL ||
                tokenKind == TokenKind.GREATER_THAN ||
                tokenKind == TokenKind.GREATER_THAN_EQUAL;
    }

    private boolean isAdditiveOperator(TokenKind tokenKind) {
        return tokenKind == TokenKind.PLUS ||
                tokenKind == TokenKind.MINUS ||
                tokenKind == TokenKind.OR;
    }

    private boolean isUnaryOperator(TokenKind tokenKind) {
        return tokenKind == TokenKind.MINUS ||
                tokenKind == TokenKind.NOT;
    }

    private boolean isMultiplicativeOperator(TokenKind tokenKind) {
        return tokenKind == TokenKind.ASTERISK ||
                tokenKind == TokenKind.SLASH ||
                tokenKind == TokenKind.MOD ||
                tokenKind == TokenKind.AND;
    }

    // match statement used by the entire parser. very simple checks the current
    // token and compares it to the expected token recieved.
    // then moves it forward by one token. else throw an error.
    private void match(TokenKind expectedToken) throws ParseException, IOException, LexicalException {
        if (currentToken.getKind() == expectedToken) {
            currentToken = lexer.nextToken();
        } else {
            throw new ParseException("Expected " + expectedToken + " but found " + currentToken.getKind() + " at "
                    + currentToken.getPosition());
        }
    }

    // AST node code starts here starts here 
/**
 * AST (Abstract Syntax Tree) Explained: 
 * This was built on top of an already made parser so this was the easiest method to implement. 
 * I tried using an abstract class if you see in my older pushes but it was too messy and difficult to set up. 
 * the new method simply required me to add onto the parse instead of reworking the
 *
 * The 'createNode' method is where the bulk of work happens, building each node we need. And once 
 * parsing is done, 'printAST' lets us visualize this tree.
 */

    // AST Node Creation: This is the core of building our AST. Each node represents a different construct of our language.
// The node contains a type and any children it has. It's a versatile way to represent our language's syntax tree.
    private Map<String, Object> createNode(String type, Map<String, Object>... children) {
        Map<String, Object> node = new HashMap<>();
        node.put("type", type);
        List<Map<String, Object>> childList = new ArrayList<>();
        for (Map<String, Object> child : children) {
            if (child != null && !child.isEmpty()) {
                childList.add(child);
            }
        }
        if (!childList.isEmpty()) {
            node.put("children", childList);
        }
        return node;
    }

    // Print AST: Converts our AST to a string representation. It uses indentation to show the structure.
    public void printAST(Map<String, Object> node) {
        String astString = buildASTString(node, 0);
        System.out.println(astString);
    }

    // Builds a string representation of our AST. It's recursive and handles indentation for readability. 
    // also coded to remove excess nulls since that was an issue in early verions.
    private String buildASTString(Map<String, Object> node, int indentLevel) {
        if (node == null || node.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String indentation = "\t".repeat(indentLevel);
        sb.append(indentation).append("(");

        // Append the node type
        String nodeType = (String) node.get("type");
        if (nodeType != null && !nodeType.isEmpty()) {
            sb.append("'").append(nodeType).append("' ");
        }

        // Handle specific attributes and child nodes
        for (String key : node.keySet()) {
            if (key.equals("type")) {
                continue; // Skip type as it's already added
            }
            Object value = node.get(key);
            if (value instanceof Map) {
                // Recursive call for child nodes
                String childString = buildASTString((Map<String, Object>) value, indentLevel + 1);
                if (!childString.isEmpty()) {
                    sb.append("\n").append(childString).append(" ");
                }
            } else if (value instanceof List) {
                // Recursive call for each child in the list
                List<Map<String, Object>> children = (List<Map<String, Object>>) value;
                if (!children.isEmpty()) {
                    sb.append("\n");
                    for (Map<String, Object> child : children) {
                        String childString = buildASTString(child, indentLevel + 1);
                        if (!childString.isEmpty()) {
                            sb.append(childString).append(" ");
                        }
                    }
                }
            } else if (value != null) {
                // Append attribute values
                sb.append("'").append(value).append("' ");
            }
        }

        sb.append(")");
        return sb.toString().trim();
    }

}