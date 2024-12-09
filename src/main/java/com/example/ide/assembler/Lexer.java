package com.example.ide.assembler;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    // Tokenizes the input assembly code into individual tokens
    public List<String> tokenize(String code) {
        // Split by whitespace, commas, and parentheses
        String[] tokens = code.split("\\s+|,|\\(|\\)|\\n");
        List<String> tokenList = new ArrayList<>();

        // Add tokens to the list, skipping empty tokens
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                tokenList.add(token.trim());
            }
        }

        return tokenList;
    }
}
