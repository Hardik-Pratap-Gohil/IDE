package com.example.ide.assembler.RISCV;

import java.util.List;

public class Instruction {
    private final TokenType opcode; // The mnemonic (e.g., ADD, SUB)
    private final List<Token> operands; // List of operands (e.g., registers, immediates)
    private final List<AssemblerError> errors; // Shared error list

    // Constructor
    public Instruction(TokenType opcode, List<Token> operands, List<AssemblerError> errors) {
        this.opcode = opcode;
        this.operands = operands;
        this.errors = errors;
    }

    public TokenType getOpcode() {
        return opcode;
    }

    public List<Token> getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        return opcode + " " + operands;
    }

    // Convert the instruction to its machine code representation
    public String toMachineCode() {
        try {
            return switch (opcode) {
                case ADD -> convertRTypeInstruction("0000000", "000"); // R-type
                case SUB -> convertRTypeInstruction("0100000", "000"); // R-type
                case XOR -> convertRTypeInstruction("0000000", "100"); // R-type
                case OR -> convertRTypeInstruction("0000000", "110"); // R-type
                case AND -> convertRTypeInstruction("0000000", "111"); // R-type
                case ADDI -> convertITypeInstruction("0010011","000"); // I-type
                case XORI -> convertITypeInstruction("0010011","100"); // I-type
                case ORI -> convertITypeInstruction("0010011","110"); // I-type
                case ANDI -> convertITypeInstruction("0010011","111"); // I-type
                case LB -> convertITypeInstruction("0000011","000"); // I-type (Load byte)
                case LH -> convertITypeInstruction("0000011","001"); // I-type (Load half-word)
                case LW -> convertITypeInstruction("0000011","010"); // I-type (Load word)
                case SB -> convertSTypeInstruction("000"); // S-type (Store byte)
                case SH -> convertSTypeInstruction("001"); // S-type (Store half-word)
                case SW -> convertSTypeInstruction("010"); // S-type (Store word)
                case BEQ -> convertBTypeInstruction("000"); // B-type (Branch if equal)
                case BNE -> convertBTypeInstruction("001"); // B-type (Branch if not equal)
                case JAL -> convertJTypeInstruction(); // J-type
                case LUI -> convertUTypeInstruction(); // U-type
                case FADD -> convertFTypeInstruction("00000", "000"); // F-type
                case FSUB -> convertFTypeInstruction("00001", "000"); // F-type
                case FMUL -> convertFTypeInstruction("00010", "000"); // F-type
                case FDIV -> convertFTypeInstruction("00011", "000"); // F-type
                case FMIN -> convertFTypeInstruction("00100", "001"); // F-type
                case FMAX -> convertFTypeInstruction("00101", "010"); // F-type
                case FSQRT -> convertFTypeInstruction("00110", "000"); // F-type
                default -> throw new IllegalArgumentException("Unsupported opcode: " + opcode);
            };
        } catch (Exception e) {
            errors.add(new AssemblerError(0, "Instruction Error: " + opcode + " - " + e.getMessage()));
            return "ERROR";
        }
    }

    // Helper method to convert R-Type instructions
    private String convertRTypeInstruction(String funct7, String funct3) {
        if (operands.size() == 3) {
            String rd = registerToBinary(operands.get(0).lexeme);
            String rs1 = registerToBinary(operands.get(1).lexeme);
            String rs2 = registerToBinary(operands.get(2).lexeme);
            return funct7 + rs2 + rs1 + funct3 + rd + "0110011";
        }
        reportOperandError(3);
        return "ERROR";
    }

    private String convertITypeInstruction(String opcode,  String funct3) {
        if (operands.size() == 3) {
            String rd = registerToBinary(operands.get(0).lexeme);
            String rs1 = registerToBinary(operands.get(1).lexeme);
            String imm = padBinaryString(Integer.toBinaryString(Integer.parseInt(operands.get(2).lexeme)), 12);
            if (imm.length() > 12) {
                imm = imm.substring(imm.length() - 12);
            } else {
                imm = String.format("%12s", imm).replace(' ', '0');
            }
            return imm + rs1 + funct3 + rd + opcode;
        }
        reportOperandError(3);
        return "ERROR";
    }

    private String convertSTypeInstruction(String funct3) {
        if (operands.size() == 3) {
            String imm = padBinaryString(Integer.toBinaryString(Integer.parseInt(operands.get(0).lexeme)), 12);
            String rs2 = registerToBinary(operands.get(1).lexeme);
            String rs1 = registerToBinary(operands.get(2).lexeme);
            if (imm.length() > 12) {
                imm = imm.substring(imm.length() - 12);
            } else {
                imm = String.format("%12s", imm).replace(' ', '0');
            }
            return imm.substring(0, 7) + rs2 + rs1 + funct3 + imm.substring(7) + "0100011";
        }
        reportOperandError(3);
        return "ERROR";
    }

    private String convertBTypeInstruction(String funct3) {
        if (operands.size() == 3) {
            String imm = padBinaryString(Integer.toBinaryString(Integer.parseInt(operands.get(0).lexeme)), 12);
            String rs2 = registerToBinary(operands.get(1).lexeme);
            String rs1 = registerToBinary(operands.get(2).lexeme);
            if (imm.length() > 12) {
                imm = imm.substring(imm.length() - 12);
            } else {
                imm = String.format("%12s", imm).replace(' ', '0');
            }
            return imm.charAt(0) + imm.substring(2, 8) + rs2 + rs1 + funct3 + imm.substring(8, 12) + imm.charAt(1) + "1100011";
        }
        reportOperandError(3);
        return "ERROR";
    }

    private String convertUTypeInstruction() {
        if (operands.size() == 2) {
            String rd = registerToBinary(operands.get(0).lexeme);
            String imm = padBinaryString(Integer.toBinaryString(Integer.parseInt(operands.get(1).lexeme)), 20);
            return imm + rd + (opcode == TokenType.LUI ? "0110111" : "0010111");
        }
        reportOperandError(2);
        return "ERROR";
    }

    private String convertJTypeInstruction() {
        if (operands.size() == 2) {
            String rd = registerToBinary(operands.get(0).lexeme);
            String imm = padBinaryString(Integer.toBinaryString(Integer.parseInt(operands.get(1).lexeme)), 21);
            return imm.charAt(0) + imm.substring(10, 20) + imm.charAt(9) + imm.substring(1, 9) + rd + "1101111";
        }
        reportOperandError(2);
        return "ERROR";
    }

    private String convertFTypeInstruction(String funct5, String rm) {
        if (operands.size() == 3) {
            String rd = registerToBinary(operands.get(0).lexeme);
            String rs1 = registerToBinary(operands.get(1).lexeme);
            String rs2 = registerToBinary(operands.get(2).lexeme);
            return funct5 + rs2 + rs1 + rm + rd + "1010011";
        }
        reportOperandError(3);
        return "ERROR";
    }

    private void reportOperandError(int expected) {
        errors.add(new AssemblerError(0, "Instruction " + opcode + " expects " + expected + " operands, but got " + operands.size()));
    }

    private String registerToBinary(String reg) {
        if (reg.startsWith("X")) {
            int regNum = Integer.parseInt(reg.substring(1));
            return String.format("%05d", Integer.parseInt(Integer.toBinaryString(regNum)));
        }
        reportOperandError(1);
        return "00000";
    }

    private String padBinaryString(String binary, int length) {
        return "0".repeat(Math.max(0, length - binary.length())) + binary;
    }
}
