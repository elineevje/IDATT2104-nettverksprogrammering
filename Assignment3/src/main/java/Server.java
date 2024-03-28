import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server class represents a simple server that listens for incoming client connections
 * and delegates each connection to a separate thread for handling.
 */
public class Server {

  /**
   * Main method of the server program.
   *
   * @param args Command line arguments.
   * @throws IOException If an I/O error occurs when creating the server socket.
   */
  public static void main(String[] args) throws IOException {
    final int PORTNR = 12345;

    // Create a server socket to listen for incoming connections
    ServerSocket server = new ServerSocket(PORTNR);
    System.out.println("Server is running and waiting for connections...");

    // Continuously accept incoming client connections
    while (true) {
      // Accept incoming client connection
      Socket connection = server.accept();

      // Start a new thread to handle the client
      Thread clientThread = new Thread(new ClientHandler(connection));
      clientThread.start();

      try {
        clientThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
