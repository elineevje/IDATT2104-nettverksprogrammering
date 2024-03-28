import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * The Client class represents a simple client that connects to a server and sends requests.
 */
class Client {

    /**
     * Method to print the selection menu from the server
     *
     * @param br The BufferedReader to read the selection menu from the server
     * @throws IOException If an I/O error occurs
     */
  private static void printSelectionMenu(BufferedReader br) throws IOException {
    String selection1 = br.readLine() + "\n";
    String selection2 = br.readLine() + "\n";
    String selection3 = br.readLine() + "\n";
    String selection4 = br.readLine();
    System.out.println(selection1 + selection2 + selection3 + selection4);
  }

  public static void main(String[] args) throws IOException {

    // Define the port number and server address
    final int PORT_NUMBER = 12345;
    String server = "10.24.0.244";

    // Establish a socket connection to the server
    Socket socket = new Socket(server, PORT_NUMBER);

    // Initialize input and output streams for communication with the server
    InputStreamReader isr = new InputStreamReader(socket.getInputStream());
    BufferedReader br = new BufferedReader(isr);
    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

    // Read and print initial confirmation message from the server
    String confirmation = br.readLine();
    System.out.println(confirmation);

    // Read and print the selection menu from the server
    printSelectionMenu(br);

    // Read user input and send it to the server
    Scanner sc = new Scanner(System.in);
    String alternative = sc.nextLine();
    String firstNr, secondNr;
    pw.println(alternative);


    // Continue processing requests until the user chooses to exit
    while (!alternative.equals("x")) {
      // Read and print prompts from the server for numbers to calculate
      String number1 = br.readLine();
      System.out.println(number1);
      firstNr = sc.nextLine();
      pw.println(firstNr);

      String number2 = br.readLine();
      System.out.println(number2);
      secondNr = sc.nextLine();
      pw.println(secondNr);

      String result = br.readLine();
      System.out.println(result);

      printSelectionMenu(br);
      alternative = sc.nextLine();
      pw.println(alternative);
    }
    br.close();
    pw.close();
    socket.close();
  }
}
