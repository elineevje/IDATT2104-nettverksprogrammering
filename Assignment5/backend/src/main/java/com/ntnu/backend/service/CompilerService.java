package com.ntnu.backend.service;
import com.ntnu.backend.model.CodeModel;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Service class responsible for compiling the code
 */
@Service
public class CompilerService {
  /**
   * Compiles the code and returns the output
   *
   * @param code The code to be compiled
   * @return The compiled code
   */
  public CodeModel compileCode(CodeModel code) {

    // StringBuilder to store the output of the compilation
    StringBuilder output = new StringBuilder();

    try {
      // Command to execute the code
      String[] command = {"docker", "run", "--rm", "python:latest", "python", "-c", code.getCode()};

      // ProcessBuilder with the specified command
      ProcessBuilder pb = new ProcessBuilder(command);
      // Redirects the error stream to merge with the standard output
      pb.redirectErrorStream(true);
      // Starts the process
      Process process = pb.start();

      // Reads the output of the process
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String currentLine;

      // Appends each line of the output to the StringBuilder
      while ((currentLine = br.readLine()) != null) {
        output.append(currentLine).append("\n");
      }

      // Waits for the process to finish retrieve the exit code
      int exitCode = process.waitFor();
      // Check if the process exited with a non-zero status
      if (exitCode != 0) {
        output.append("Execution failed with exit code ").append(exitCode);
      }
    } catch (IOException | InterruptedException e) {
      // Appends error message to the output
      output.append("Error: ").append(e.getMessage());
    }

    // Sets the compiled code output to the CodeModel
    code.setCompiledCode(output.toString());
    return code;
  }
}
