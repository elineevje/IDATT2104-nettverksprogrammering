import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple web server that listens to specified port and sends a simple HTML response to the client.
 */
public class WebServer implements Runnable {
  private int portNumber;

  /**
   * Constructor for the web server.
   *
   * @param portNumber The port number to listen to.
   */
  public WebServer(int portNumber) {
    this.portNumber = portNumber;
  }

  /**
   * Starts the web server and listens to the specified port.
   */
  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
      System.out.println("Web server is listening to port " + portNumber);

      while (true) {
        Socket socket = serverSocket.accept();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<HTML><BODY>");
        out.println("<H1>Hello. You have connected to my web server </H1>");
        out.println("Headers from client:");
        out.println("<UL>");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = in.readLine()) != null && !line.equals("")) {
          out.println("<LI>" + line + "</LI>");
        }

        out.flush();
        socket.close();
        out.println("</UL>");
        out.println("</BODY></HTML>");
      }
    } catch (IOException e) {
      System.err.println("Error creating web server: " + e.getMessage());
    }
  }

  /**
   * Main method to start the web server.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    final int PORT_NUMBER = 8080;
    WebServer webServer = new WebServer(PORT_NUMBER);
    Thread webServerThread = new Thread(webServer);
    webServerThread.start();
  }
}
