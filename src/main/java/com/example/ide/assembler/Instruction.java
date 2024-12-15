package com.example.ide.assembler;

public class Instruction {
    private final String opcode;
    private final String[] operands;

    // Constructor
    public Instruction(String opcode, String[] operands) {
        this.opcode = opcode;
        this.operands = operands;
    }

    // Convert the instruction to its machine code representation
    public String toMachineCode() {
        return switch (opcode.toLowerCase()) {
            case "add" -> convertAddInstruction();
            case "sub" -> convertSubInstruction();
            case "xor" -> convertXorInstruction();
            case "or"  -> convertOrInstruction();
            case "and" -> convertAndInstruction();
            default -> "Unknown Instruction: " + opcode;
        };
    }



    // Convert register names (e.g., x1, x2) to binary
    private String registerToBinary(String reg) {
        if (reg.startsWith("x")) {
            int regNum = Integer.parseInt(reg.substring(1)); // Extract number after "x"
            return String.format("%05d", Integer.parseInt(Integer.toBinaryString(regNum))); // 5-bit binary representation
        }
        return "00000"; // Default for invalid register
    }

    // Example machine code conversion for "add" instruction
    private String convertAddInstruction() {
        if (operands.length == 3) {
            String rd = registerToBinary(operands[0]); // Destination register (e.g., x1)
            String rs1 = registerToBinary(operands[1]); // Source register 1 (e.g., x2)
            String rs2 = registerToBinary(operands[2]); // Source register 2 (e.g., x3)

            // Construct the R-type instruction format for "add"
            return "0000000" + rs2 + rs1 + "000" + rd + "0110011"; // Example: "00000000001000110000000110110011"
        }
        return "Invalid operands for add instruction";
    }

    // Example machine code conversion for "sub" instruction
    private String convertSubInstruction() {
        if (operands.length == 3) {
            String rd = registerToBinary(operands[0]);
            String rs1 = registerToBinary(operands[1]);
            String rs2 = registerToBinary(operands[2]);

            // Construct the R-type instruction format for "sub"
            return "0100000" + rs2 + rs1 + "000" + rd + "0110011"; // Example: "01000000010100110000001011001101"
        }
        return "Invalid operands for sub instruction";
    }

    private String convertXorInstruction() {
        if (operands.length == 3) {
            String rd = registerToBinary(operands[0]); // Destination register (e.g., x1)
            String rs1 = registerToBinary(operands[1]); // Source register 1 (e.g., x2)
            String rs2 = registerToBinary(operands[2]); // Source register 2 (e.g., x3)

            // Construct the R-type instruction format for "add"
            return "0000000" + rs2 + rs1 + "100" + rd + "0110011"; // Example: "00000000001000110000000110110011"
        }
        return "Invalid operands for xor instruction";
    }

    private String convertOrInstruction() {
        if (operands.length == 3) {
            String rd = registerToBinary(operands[0]); // Destination register (e.g., x1)
            String rs1 = registerToBinary(operands[1]); // Source register 1 (e.g., x2)
            String rs2 = registerToBinary(operands[2]); // Source register 2 (e.g., x3)

            // Construct the R-type instruction format for "add"
            return "0000000" + rs2 + rs1 + "110" + rd + "0110011"; // Example: "00000000001000110000000110110011"
        }
        return "Invalid operands for or instruction";
    }

    private String convertAndInstruction() {
        if (operands.length == 3) {
            String rd = registerToBinary(operands[0]); // Destination register (e.g., x1)
            String rs1 = registerToBinary(operands[1]); // Source register 1 (e.g., x2)
            String rs2 = registerToBinary(operands[2]); // Source register 2 (e.g., x3)

            // Construct the R-type instruction format for "add"
            return "0000000" + rs2 + rs1 + "111" + rd + "0110011"; // Example: "00000000001000110000000110110011"
        }
        return "Invalid operands for and instruction";
    }




}
