package com.example.ide.assembler.RISCV;


import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final List<AssemblerError> errors; // List to collect errors
    private final TextArea outputTextArea;     // Reference to IDE's outputTextArea
    private int start = 0;  // Start of the current token
    private int current = 0; // Current position in the source
    private int line = 1;   // Current line number

    public Lexer(String source, List<AssemblerError> errors, TextArea outputTextArea) {
        this.source = source;
        this.errors = errors;
        this.outputTextArea = outputTextArea;
    }

    public int getLine() {
        return line;
    }

    // Tokenizes the input assembly code into individual tokens
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            // Mark the start of a new token
            start = current;
            try {
                scanToken();
            } catch (IllegalArgumentException e) {
                // Log the error and move to the next line
                AssemblerError error = new AssemblerError(line, e.getMessage());
                errors.add(error);
                outputTextArea.appendText(error.toString() + "\n");
                skipToNextLine();
            }
        }
        if (tokens.isEmpty() && errors.isEmpty()) {
            AssemblerError error = new AssemblerError(line, "Empty or invalid instruction.");
            errors.add(error);
            outputTextArea.appendText(error.toString() + "\n");
        }
        return tokens;
    }

    // Scans a single token
    private void scanToken() {
        char c = advance();

        try {
            switch (c) {
                // Ignore whitespace
                case ' ':
                case '\t':
                    break;
                // Handle newlines
                case '\n':
                    line++;
                    break;
                // Handle parentheses
                case '(':
                    addToken(TokenType.LPAREN, "(");
                    break;
                case ')':
                    addToken(TokenType.RPAREN, ")");
                    break;
                case ',':
                    addToken(TokenType.COMMA, ",");
                    break;
                default:
                    if (isDigit(c) || c == '-' || c == '0') {
                        number();
                    } else if (isAlpha(c)) {
                        identifier();
                    } else {
                        throw new IllegalArgumentException("Unexpected character: " + c);
                    }
                    break;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Syntax error at line " + line + ": " + e.getMessage());

        }
    }

    // Processes numbers (immediate values)
    private void number() {
        if (source.charAt(start) == '-') {
            advance(); // Consume '-'
        }
        // Hexadecimal
        if (peek() == '0' && (peekNext() == 'x' || peekNext() == 'X')) {
            advance();
            advance();
            while (isHexDigit(peek())) advance();
            String lexeme = source.substring(start, current);
            int value = Integer.parseInt(lexeme.substring(2), 16);
            addToken(TokenType.IMM, lexeme, value);
            return;
        }
        // Decimal
        while (isDigit(peek())) advance();
        String lexeme = source.substring(start, current);
        int value = Integer.parseInt(lexeme);
        addToken(TokenType.IMM, lexeme, value);
    }



    // Processes identifiers (instructions, registers, labels)
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        // Extract the lexeme
        String lexeme = source.substring(start, current);

        // Match it to a TokenType
        TokenType type = getTokenType(lexeme);
        addToken(type, lexeme);
    }

    // Matches a token to its type
    private TokenType getTokenType(String token) {
        switch (token.toUpperCase()) {
            case "ADD": return TokenType.ADD;
            case "SUB": return TokenType.SUB;
            case "XOR": return TokenType.XOR;
            case "OR": return TokenType.OR;
            case "AND": return TokenType.AND;
            case "XORI": return TokenType.XORI;
            case "ORI": return TokenType.ORI;
            case "ANDI": return TokenType.ANDI;
            case "ADDI": return TokenType.ADDI;
            case "LB": return TokenType.LB;
            case "LH": return TokenType.LH;
            case "LW": return TokenType.LW;
            case "JALR": return TokenType.JALR;
            case "SB": return TokenType.SB;
            case "SH": return TokenType.SH;
            case "SW": return TokenType.SW;
            case "BEQ": return TokenType.BEQ;
            case "BNE": return TokenType.BNE;
            case "JAL": return TokenType.JAL;
            case "LUI": return TokenType.LUI;
            case "FADD": return TokenType.FADD;
            case "FSUB": return TokenType.FSUB;
            case "FMUL": return TokenType.FMUL;
            case "FDIV": return TokenType.FDIV;
            case "FMIN": return TokenType.FMIN;
            case "FMAX": return TokenType.FMAX;
            case "FSQRT": return TokenType.FSQRT;
        }

        // Match registers
        if (token.matches("X[1-9]|X1[0-9]|X2[0-9]|X3[0-2]")) {
            return TokenType.X;
        }

        // If unknown, throw an exception
        throw new IllegalArgumentException("Unrecognized token: " + token + " at line " + line);
    }

    // Utility to parse immediate values
    private Object parseImmediate(String token) {
        if (token.startsWith("0x") || token.startsWith("0X")) {
            return Integer.parseInt(token.substring(2), 16); // Hexadecimal
        } else if (token.startsWith("0b") || token.startsWith("0B")) {
            return Integer.parseInt(token.substring(2), 2); // Binary
        } else {
            return Integer.parseInt(token); // Decimal
        }
    }

    // Advances the current pointer and returns the consumed character
    private char advance() {
        return source.charAt(current++);
    }

    // Peeks at the current character without consuming it
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // Checks if we've reached the end of the source
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // Utility methods for character checks
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHexDigit(char c) {
        return isDigit(c) || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    private boolean isBinaryDigit(char c) {
        return c == '0' || c == '1';
    }

    private boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Adds a token to the list
    private void addToken(TokenType type, String lexeme) {
        addToken(type, lexeme, null);
    }

    private void addToken(TokenType type, String lexeme, Object literal) {
        tokens.add(new Token(type, lexeme, literal, line));
    }


    // Skip to the next line after an error
    private void skipToNextLine() {
        while (!isAtEnd() && peek() != '\n') {
            advance();
        }
        if (!isAtEnd() && peek() == '\n') {
            line++;
            advance();
        }
    }
}
