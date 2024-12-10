package com.example.ide.assembler;


import java.io.*;
import java.util.List;
import java.util.Scanner;

public class RiscVAssembler {
    public static void assemble(String filePath) {

        // Read the assembly code from the file
        String code = readFile(filePath);

        if (code == null || code.isEmpty()) {
            System.out.println("Error reading the assembly file or the file is empty.");
            return;
        }

        // Determine the filename from the input path
        File inputFile = new File(filePath);
        String filename = inputFile.getName();
        String outputFileName = filename.substring(0, filename.lastIndexOf('.')) + ".bin";

        // Define the output directory and ensure it exists
        String outputDir = "src/main/resources/com/example/ide/output/";
        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdirs();  // Make the directory structure if it does not exist
        }

        String outputFilePath = outputDir + outputFileName;
        System.out.println(outputFilePath);
        // Parse the code and convert it to instructions
        Parser parser = new Parser();
        List<Instruction> instructions = parser.parse(code);

        // Write the machine code to the specified file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Instruction instruction : instructions) {
                String machineCode = instruction.toMachineCode();
                writer.write(machineCode);
                writer.newLine();  // Optionally add a new line for each instruction
            }
            System.out.println("Machine code written to: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
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
