/*--------------------------------------------------------

1. Name / Date:  Terry Schmidt, April 9

2. Java version used, if not the official version for the class:  1.8

3. Precise command-line compilation examples / instructions:

e.g.:

> javac InetClient.java


4. Precise examples / instructions to run this program:

e.g.:

In separate shell windows:

> java InetClient
> java InetServer

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For example, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. programonechecklist.html
 b. InetServer.java
 c. InetClient.java

5. Notes:

e.g.:

I changed lots of variables, put in lots of comments and generally tried to take
your example and make it my own as much as I could.

----------------------------------------------------------*/
import java.io.*; 
import java.net.*; 

public class InetClient {
	public static int port = 2000;  // The server is listening on this port number.

	public static void main(String args[]) {
		String serverName;
		
		// Get host name from command line, if the program is being run from command line.
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];

		System.out.println("Terry Schmidt's Inet Client, 1.8.\n");  // Basic printout message
		System.out.println("Using server: " + serverName + ", Port: "+ port);  // Printout 

		// Try to read input from user.
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			boolean keepLooping = true;
			do {
				// Prompt user to input domain name to lookup.
				String hostNameToLookup = promptUser(in, "Enter a hostname or an IP address, (quit) to end: ");
				
				// If user typed "quit" then get out of the loop by setting keepLooping to false.
				if (hostNameToLookup.equalsIgnoreCase("quit")) {
					keepLooping = false;
				} else {
					getRemoteAddress(hostNameToLookup, serverName);					
				}
			} 
			while (keepLooping); 
			System.out.println("Cancelled by user request.");
		} catch (IOException exception) {  // Exception if the try block fails.
			exception.printStackTrace();
		}
	}

	// Ask user for input.
	static String promptUser(BufferedReader in, String message) throws IOException {
		System.out.print(message);
		System.out.flush();
		return in.readLine();
	}
	
	// Get ip address from domain name server.
	static void getRemoteAddress(String name, String serverName) {
		Socket socket;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;

		try {
			// Opens connection with the correct servername and port
			socket = new Socket(serverName, port);

			// input and output streams for the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintStream(socket.getOutputStream());

			// Send name/address to server
			toServer.println(name);
			toServer.flush();  //flush the stream

			// Read two or three lines of response from the server and block while synchronously waiting:
			// synchronous call is when you wait for the call to finish executing before moving on.
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine();
				if (textFromServer != null) { // we want to make sure the client actually wrote something.  If they did, it won't be null.
					System.out.println(textFromServer); // print it		
				}
			}
			socket.close();
		} catch (IOException exception) {  // if try block fails, then there's an IOException
			System.out.println("Socket error."); // tell that there was a socket error
			exception.printStackTrace();
		}
	}
}