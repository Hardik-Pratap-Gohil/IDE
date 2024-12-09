package com.example.ide.assembler;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class RiscVAssembler {
    public static void main(String[] args) {
        // Set the file path directly here
        String filePath = "resources/program.asm";  // Set your file path here

        // Read the assembly code from the file
        String code = readFile(filePath);

        if (code == null || code.isEmpty()) {
            System.out.println("Error reading the assembly file or the file is empty.");
            return;
        }

        // Parse the code and convert it to instructions
        Parser parser = new Parser();
        List<Instruction> instructions = parser.parse(code);

        // Convert each instruction to machine code and print
        for (Instruction instruction : instructions) {
            String machineCode = instruction.toMachineCode();
            System.out.println("Machine Code: " + machineCode);
        }
    }

    // Method to read the file content
    private static String readFile(String filePath) {
        StringBuilder code = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                code.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
            return null;
        }
        return code.toString();
    }
}
