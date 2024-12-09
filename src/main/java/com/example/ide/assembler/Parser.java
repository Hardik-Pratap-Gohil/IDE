package com.example.ide.assembler;


import java.util.*;

public class Parser {
    private static final Set<String> OPCODES = new HashSet<>(Arrays.asList("add", "sub", "xor","or","and"));

    // Parse the assembly code and convert it to a list of instructions
    public List<Instruction> parse(String code) {
        Lexer lexer = new Lexer();
        List<String> tokens = lexer.tokenize(code);
        List<Instruction> instructions = new ArrayList<>();

        // Process tokens in batches of 1 opcode + n operands
        for (int i = 0; i < tokens.size(); i++) {
            String opcode = tokens.get(i);
            // Add operands after the opcode
            List<String> operands = new ArrayList<>();

            // Capture operands until the next opcode or end of list
            i++;
            while (i < tokens.size() && !isOpcode(tokens.get(i))) {
                operands.add(tokens.get(i));
                i++;
            }
            i--; // Decrease to avoid incrementing on the next iteration

            // Create instruction with opcode and operands
            instructions.add(new Instruction(opcode, operands.toArray(new String[0])));
        }

        return instructions;
    }

    // Helper function to check if a token is an opcode (like "add", "sub", etc.)
    private boolean isOpcode(String token) {
        return OPCODES.contains(token.toLowerCase());
    }
}
