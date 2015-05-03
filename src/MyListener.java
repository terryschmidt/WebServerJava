import java.io.*;
import java.net.*;
import java.util.*;

public class MyListener {

    public static void main(String a[]) throws IOException {
        int clientsToQueue = 6;
        int port = 2540;
        Socket socket;
        
        ServerSocket serverSocket = new ServerSocket(port, clientsToQueue); // makes the server

        System.out.println("Terry's listener is listening at port " + port + ".");

      
        while (2 > 1) {
            socket = serverSocket.accept();
            new WebServerWorker(socket).start();
        }
    }
}

class Worker2 extends Thread { // instantiated when a client makes a request

    Socket socket; 

    Worker2(Socket s) {
        socket = s;
    }

    public void run() {
        PrintStream outputTo = null;
        BufferedReader inputFrom = null;

        try {
            outputTo = new PrintStream(socket.getOutputStream()); // initialize
            inputFrom = new BufferedReader(new InputStreamReader(socket.getInputStream())); // initialize

            try {
                String input = "";
                while ((input = inputFrom.readLine()) != null) {
                    System.out.println(input);
                }

                outputTo.println("Got your request");
                outputTo.flush();
            } catch (RuntimeException Exc) {
                System.out.println("Server error");
            }
            
            socket.close(); // this will disconnect, but server keeps going
        } catch (IOException Exc) {
            System.out.println("Error.");
        }
    }
}