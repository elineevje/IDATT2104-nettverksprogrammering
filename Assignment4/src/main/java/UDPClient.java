import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The UDPClient class represents a simple client that sends requests to a server using the UDP protocol.
 * The client sends two numbers and an operation choice to the server, and then receives the result.
 */
public class UDPClient {

  public static void main(String[] args) throws IOException {

    // Define the server address and port number
    InetAddress serverAddress = InetAddress.getByName("localhost");
    final int PORT_NUMBER = 12345;

    // Create a DatagramSocket for sending and receiving data
    DatagramSocket socket = new DatagramSocket();

    Scanner scanner = new Scanner(System.in);

    while (true) {
      // Ask the user for the operation choice
      System.out.println("Choose your operation:");
      System.out.println("+ for addition");
      System.out.println("- for subtraction");
      System.out.println("x to exit the program");

      // Get the operation choice from the user
      char operationChoice = scanner.next().charAt(0);

      // Check if the user wants to exit
      if (operationChoice == 'x') {
        break;
      }

      // Prompt the user to enter numbers
      System.out.print("Enter the first number: ");
      int firstNumber = scanner.nextInt();
      System.out.print("Enter the second number: ");
      int secondNumber = scanner.nextInt();

      // Convert the numbers and operation choice to bytes
      String message = firstNumber + " " + secondNumber + " " + operationChoice;
      byte[] sendData = message.getBytes();

      // Create a DatagramPacket to send the data
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORT_NUMBER);

      // Send the packet to the server
      socket.send(sendPacket);

      // Receive the result from the server
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      socket.receive(receivePacket);

      // Display the result
      String result = new String(receivePacket.getData(), 0, receivePacket.getLength());
      System.out.println("Result from server: " + result);
    }

    // Close the socket
    socket.close();
  }
}
