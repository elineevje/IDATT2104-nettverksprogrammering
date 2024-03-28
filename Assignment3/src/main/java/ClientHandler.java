import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class to handle the client requests
 */
public class ClientHandler extends Thread {

  private final Socket socket;

  /**
   * Constructor for the ClientHandler class
   *
   * @param socket The socket to handle the client requests
   */
  public ClientHandler(Socket socket){
    this.socket =socket;

  }

  /**
   * Method to handle the client requests
   * The client can choose to add or subtract two numbers
   * or to exit the program.
   *
   */
  @Override
  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(socket.getInputStream());
      BufferedReader br = new BufferedReader(isr);
      PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

      System.out.println("Achieved connection to client!");
      pw.println("Achieved connection to server!");
      pw.println("Choose your operation:");
      pw.println("+ for plus");
      pw.println("- for minus");
      pw.println("x to exit the program");

      String operation = br.readLine();
      int number1;
      int number2;
      String alternative;

      while (true) {
        alternative = operation.trim();
        if (alternative.equals("x")) {
          break;
        }

        pw.println("Enter the first number:");
        String firstNr = br.readLine();
        number1 = Integer.parseInt(firstNr);

        pw.println("Enter the second number:");
        String secondNr = br.readLine();
        number2 = Integer.parseInt(secondNr);

        if (alternative.equals("+")) pw.println(number1 + " + " + number2 + " = " + (number1 + number2));
        else pw.println(number1 + " - " + number2 + " = " + (number1 - number2));

        pw.println("Choose your operation:");
        pw.println("+ for plus");
        pw.println("- for minus");
        pw.println("x to exit the program");
        operation = br.readLine();
      }
      br.close();
      pw.close();
      socket.close();
    } catch (IOException ioE) {
      ioE.printStackTrace();
    }
  }
}