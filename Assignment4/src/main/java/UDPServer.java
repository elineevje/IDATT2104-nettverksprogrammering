import java.io.IOException;
import java.net.*;

/**
 * The UDPServer class represents a simple server that listens for incoming UDP packets
 * and sends back a response to the client.
 */
public class UDPServer {

  public static void main(String[] args) throws IOException {
    final int PORT_NUMBER = 12345;

    // Create a DatagramSocket to listen for incoming packets
    DatagramSocket socket = new DatagramSocket(PORT_NUMBER);

    System.out.println("Server is running and waiting for connections...");

    while (true) {
      // Create buffers for sending and receiving data
      byte[] receiveData = new byte[1024];
      byte[] sendData;

      // Receive a packet from the client
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      socket.receive(receivePacket);

      // Extract data from the packet
      String[] requestData = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(" ");
      int firstNumber = Integer.parseInt(requestData[0]);
      int secondNumber = Integer.parseInt(requestData[1]);
      char operationChoice = requestData[2].charAt(0);

      // Perform the calculation based on the operation choice
      int result = 0;
      if (operationChoice == '+') {
        result = firstNumber + secondNumber;
      } else if (operationChoice == '-') {
        result = firstNumber - secondNumber;
      }

      // Convert the result to bytes
      sendData = String.valueOf(result).getBytes();

      // Get the client's address and port from the received packet
      InetAddress clientAddress = receivePacket.getAddress();
      int clientPort = receivePacket.getPort();

      // Create a new packet containing the result and send it back to the client
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
      socket.send(sendPacket);
    }
  }
}
