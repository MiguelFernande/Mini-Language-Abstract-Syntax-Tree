# Mini Language Compiler Documentation

## Overview
This is a simple compiler implementation for a mini programming language written in Java. The compiler performs lexical analysis and parsing of source code, with optional Abstract Syntax Tree (AST) generation.

## Program Components

### 1. Lexical Analyzer (Lexer)
- Located in `Lexer.java` and supported by `Token.java` and `TokenKind.java`
- Reads input source code character by character
- Converts characters into tokens (lexical units)
- Recognizes:
  - Keywords: program, bool, int, if, then, else, end, while, do, print
  - Operators: +, -, *, /, :=, <, >, =, !=, >=, =<, mod, and, or, not
  - Symbols: (, ), ;, :, ., ,
  - Literals: Boolean and Integer
  - Identifiers: Variable names

### 2. Parser
- Located in `Parser.java`
- Performs syntax analysis
- Verifies program structure follows language rules
- Can generate AST representation of the program (optional)

### 3. Program Structure
Programs must follow this basic structure:
```
program [identifier]:
    [declarations]
    [statements].
```

Example:
```
program Example:
    int x, y;
    x := 5;
    y := 10;
    print x.
```

## Features

### Basic Operations
- Variable declarations (int and bool types)
- Assignment statements
- Arithmetic operations
- Conditional statements (if/then/else)
- Loop statements (while)
- Print statements

### AST Generation
- Creates a tree representation of program structure
- Helps visualize program hierarchy
- Can be enabled/disabled in Parser.java
- Outputs in format:
  ```
  ('Program'
    ('Identifier' 'Example')
    ('Declarations'
      ('Declaration' 'int' 'x'))
    ('Statements'
      ('AssignmentStatement'
        ('Identifier' 'x')
        ('Expression' '5'))))
  ```

## How to Use

1. Save your mini-language program in a .txt file
2. Run Project2.java
3. Select your program file when prompted
4. Program will:
   - Perform lexical analysis
   - Parse the program
   - Display AST (if enabled)
   - Show any syntax errors if found

## Error Handling
- Lexical errors: Invalid characters or malformed tokens
- Syntax errors: Incorrect program structure
- Detailed error messages include line and column numbers

## Language Syntax Rules

### Declarations
- Must come before statements
- Format: `type identifier[, identifier]*;`
- Types: `int` or `bool`

### Statements
- Assignment: `identifier := expression;`
- Print: `print expression;`
- If: `if expression then [body] else [body] end`
- While: `while expression do [body] end`

### Expressions
- Arithmetic: +, -, *, /, mod
- Logical: and, or, not
- Relational: <, >, =, !=, >=, =<
- Parentheses for grouping: (expression)

### Program Termination
- Must end with a period (.)
- No statements after the period

## Implementation Notes
- FileReader.java handles character-by-character input
- LexicalException.java and ParseException.java handle errors
- Project2.java contains the main program entry point
- AST functionality can be enabled/disabled without affecting basic parsing
