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

public class MyTelnet {
	public static int port = 80;  // The server is listening on this port number.

	public static void main(String args1[]) {
		String args[] = new String[]{"condor.depaul.edu","80"};
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
				String command = promptUser(in, "Press enter to send header or type 'quit' to exit.");
				
				// If user typed "quit" then get out of the loop by setting keepLooping to false.
				if (command.equalsIgnoreCase("quit")) {
					keepLooping = false;
				} else {
					getWebPage(serverName);					
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
	static void getWebPage(String serverName) {
		Socket socket;
		BufferedReader fromServer;
		PrintStream toServer;

		try {
			// Opens connection with the correct servername and port
			socket = new Socket(serverName, port);

			// input and output streams for the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintStream(socket.getOutputStream());

			// Send name/address to server
			toServer.println("GET /elliott/dog.txt HTTP/1.1");
			toServer.println("Host: localhost:2540");
			toServer.println("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:25.0) Gecko/20100101 Firefox/25.0");
			toServer.println("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			toServer.println("Accept-Language: en-US,en;q=0.5");
			toServer.println("Accept-Encoding: gzip, deflate");
			toServer.println("Connection: keep-alive");
			toServer.println("");
			
			toServer.flush();  //flush the stream

			// Read two or three lines of response from the server and block while synchronously waiting:
			// synchronous call is when you wait for the call to finish executing before moving on.

			String textFromServer = "";
			boolean keepGoing = true;
			while (keepGoing) {
				textFromServer = fromServer.readLine();
				if(textFromServer == null)
				{
					keepGoing=false;
				}
				else
				{
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