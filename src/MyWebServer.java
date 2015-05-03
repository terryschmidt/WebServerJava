/*--------------------------------------------------------

1. Terry Schmidt, Due date: May 3

2. Java version used, if not the official version for the class:

1.8

3. Precise command-line compilation examples / instructions:

> javac MyWebServer.java

4. Precise examples / instructions to run this program:

> java MyWebServer

5. List of files needed for running the program.

 a. MyWebServer.java
 b. http-streams.txt
 c. serverlog.txt
 d. checklist-mywebserver.html

5. Notes:

I found a bug in my way of doing the Up One functionality near the due date.
I'm not sure if this was an explicit requirement of the assignment.

----------------------------------------------------------*/

import java.net.*;
import java.io.*;
import java.util.*;

public class MyWebServer {
	public static void main(String[] args) throws IOException {
		int portToUse = 2540; // assignment requires use of port 2540
		int numberOfClientsToQueue = 10; // number of clients
		
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(portToUse, numberOfClientsToQueue);  // server
		
		Socket socket;  // socket
		
		System.out.println("Terry's webserver is listening on port " + portToUse + ".");  // basic intro
		System.out.println("");
		
		while (2 > 1) {  // listen for as long as the server is on
			socket = serverSocket.accept(); // connect
			new WebServerWorker(socket).start(); // create a worker thread
		}
	}
}

class WebServerWorker extends Thread { // inner class for worker threads
	Socket socketToConnect;
	
	public void run() {
		try {
				PrintStream outputToUser = new PrintStream(socketToConnect.getOutputStream()); // to send stuff back to browser
				BufferedReader inputFromUser =  new BufferedReader(new InputStreamReader(socketToConnect.getInputStream())); // getting stuff from browser
			
				String requestFromBrowser = inputFromUser.readLine(); // get request from browser
				String nameOfFileRequested;
				String typeOfContent = "";
				
				StringTokenizer requestTokenizer = new StringTokenizer(requestFromBrowser, " ");  // tokenize the request from the browser
				String getOrPost = requestTokenizer.nextToken(); // grab what type of http request it is
				
				if(getOrPost.equals("GET") == true) { // if its http get
					nameOfFileRequested = requestTokenizer.nextToken(); // grab the file name they requested
					if(nameOfFileRequested.contains("..") == true) { // security
						throw new RuntimeException(); // if that is true then its deemed a security concern.  throw an exception
					}
				} else {
					nameOfFileRequested = null; // if its not a GET, we're going to throw an exception
				}
				
				if(nameOfFileRequested == null) {
					System.out.println("Sorry, it must be an HTTP GET request.");
					throw new RuntimeException();
				}
				
				if(nameOfFileRequested.endsWith(".txt") == true) { // if they requested a text file
					typeOfContent = "text/plain"; // set content type accordingly
					displayRequestedFile(nameOfFileRequested, outputToUser, typeOfContent); // call function to serve the text file
				} else if(nameOfFileRequested.endsWith(".html") == true) { // if they requested an html file
					typeOfContent = "text/html"; // set content type accordingly
					displayRequestedFile(nameOfFileRequested, outputToUser, typeOfContent); // call function to serve the html file
				} else if(nameOfFileRequested.contains("/cgi/addnums.fake-cgi") == true) { // user wants to add nums
					typeOfContent = "text/html"; // set content type
					addNums(nameOfFileRequested, outputToUser, typeOfContent); // call addNums method
				} else if(nameOfFileRequested.endsWith("/") == true) {  // if its a directory
					typeOfContent = "text/html"; // set content type
					displayRequestedDirectory(nameOfFileRequested, outputToUser, typeOfContent);  // call the method to send a directory
				} else { // if its something else
					typeOfContent = "text/plain"; // set content
					displayRequestedFile(nameOfFileRequested, outputToUser, typeOfContent);  // serve it.  This last else is mainly so that you can download files from the server.  
					//For example, .java files.  With this final else, you can right click and download a .java file. 
				}
			socketToConnect.close(); // close socket
		} catch (IOException Exc) {
			Exc.printStackTrace();
		}
	}
	
	WebServerWorker (Socket clientSocket) {  // constructor
		socketToConnect = clientSocket;
	}
	
	public void displayRequestedFile(String nameOfFile, PrintStream outputToBrowser, String typeOfContent) throws IOException {
		if(nameOfFile.startsWith("/") == true) { // if the file name has a leading slash
		nameOfFile = nameOfFile.substring(1); // get rid of the leading slash
		}
		
		InputStream inStream = new FileInputStream(nameOfFile);  // open file
		File file = new File(nameOfFile);  // new file
		
		outputToBrowser.print("HTTP/1.1 200 OK" + "Content-Length: " + file.length() + "Content-Type: " + typeOfContent + "\r\n\r\n");  // header info
		
		System.out.println("Terry's server is serving file: " + nameOfFile + " with content type " + typeOfContent);  // server log messages
		
        byte[] fileBytes = new byte[10000]; // array of bytes to hold the file.  10000 bytes can hold a little less than 10KB
        int numberOfBytes = inStream.read(fileBytes);  // get number of bytes
        outputToBrowser.write(fileBytes, 0, numberOfBytes); // write all the bytes
        
        outputToBrowser.flush(); // flush
        inStream.close(); // close
	}
	
	public void addNums(String stringFromBrowser, PrintStream outputToBrowser, String typeOfContent) throws UnsupportedEncodingException, MalformedURLException {
		outputToBrowser.println("HTTP/1.1 200 OK Content-Type: " + typeOfContent + "\r\n\r\n");  // header info
		outputToBrowser.println("<html><head></head><body>"); // html
		outputToBrowser.println("<body bgcolor = '00000'>"); // background color of black
	
		Map<String,String> qCouples = new LinkedHashMap<String, String>();  // map to hold string couples
		URL url = new URL("http:/" + stringFromBrowser); // create new URL
		String qry = url.getQuery();  // grabs the query
		String[] pairs = qry.split("&");  // splits them where the & is
		int indexOfEqualSign;
		
		for(int i = 0; i < pairs.length; i++) {
			indexOfEqualSign = pairs[i].indexOf("=");  // find the index of the equals sign
			qCouples.put(URLDecoder.decode(pairs[i].substring(0, indexOfEqualSign), "UTF-8"), URLDecoder.decode(pairs[i].substring(indexOfEqualSign + 1), "UTF-8")); // put them in qcouples
		}
		
		try {
			int sum = Integer.parseInt(qCouples.get("num1")) + Integer.parseInt(qCouples.get("num2"));  // get the users numbers and add them
			String result = String.format("<font size =50><font color = #3399FF>%s, the sum of %s + %s = </font><font color='pink'><b>%d</b></font></font>\n", qCouples.get("person"), qCouples.get("num1"), qCouples.get("num2"),sum);  // format the output
			outputToBrowser.println(result);  // print the output to the browser
			System.out.println(qCouples.get("person") + " " + "requested " + qCouples.get("num1") + "+" + qCouples.get("num2"));  // print info to the server log
		} catch (java.lang.NumberFormatException e) {
			outputToBrowser.println("<font color=#FF0000><b>One of the inputs is not a number or is too big!</b></font>");  // error checking
		}
		
		outputToBrowser.println("</body></html>"); // close html tags
		outputToBrowser.flush(); // flush
	}
	
	public void displayRequestedDirectory(String directory, PrintStream outputToBrowser, String typeOfContent) throws IOException {
		String directory2 = directory;
		BufferedWriter displayFile = new BufferedWriter(new FileWriter("displayFile.html")); //  new file to write
		File firstFile = new File("./" + directory + "/");  // first directory
			
		File[] filesInDirectory = firstFile.listFiles();  // grab the list of files
			
		displayFile.write("<html><head></head>"); // write some html
		displayFile.write("<body link='pink'");  // pink hyperlinks.  Why not?
		displayFile.write("<body bgcolor = '00000'>"); // use black backgorund just because I like it!
		displayFile.write("<font size=111><font color = #3399FF> Directory: " + directory + "</font></font>" + "<br>");  // display what directory the page is showing
		displayFile.write("<a href=\"" + "http://localhost:2540" + "/\">" + "Back to Root" + "</a>"); // link for getting back to the root on each page
		
		if(directory.endsWith("/") && !directory.equals("/")) {  // check to see if we need to display the up one button
			int lengthToCut = -111;
			String[]  substrings = directory.split("/");
			lengthToCut = substrings[1].length() + 1;  // figure out much to cut the directory by if user does want to go up
			directory = directory.substring(0, directory.length() - lengthToCut - 1);  // cut the current directory by one level
			displayFile.write("&nbsp;" + "&nbsp;" + "&nbsp;" + "<a href=\"" + "http://localhost:2540" + directory + "\">" + "Up One" + "</a>"); // display up one option
		}
		
		displayFile.write("<br><br>");
		
		// readfiles.java was helpful here:
		for(File f: filesInDirectory) {  // for each file
			String fileName = f.getName();  // get the name of the file, put it in fileName
			if (fileName.startsWith(".") == true) { 
				continue; 
			}
			if (fileName.startsWith("displayFile.html") == true) {  // if the file is the displayfile
				continue;  // we don't need to make a hyperlink for the temporary display page
			}
			if (f.isDirectory() == true) { // if it is a directory
				displayFile.write("<a href=\"" + fileName + "/\">/" + fileName + "</a> <br>");  // create a clickable hyperlink for it
			}
			if (f.isFile() == true) { // if it is a file
				displayFile.write("<a href=\"" + fileName + "\" >" + fileName + "</a> <br>"); // create a clickable hyperlink for it
			}
		displayFile.flush(); // flush
		}
			
		displayFile.write("</body></html>");  // close html tags
		File tempDisplayFile = new File("displayFile.html");
		
		InputStream stream = new FileInputStream("displayFile.html");
		outputToBrowser.println("HTTP/1.1 200 OK" + "Content-Length: " + tempDisplayFile.length() + "Content-Type: "  + typeOfContent + "\r\n\r\n");  // send appropriate header
		
		System.out.println("Terry's server is sending directory: " + directory2); // server log
	
        byte[] displayFileBytes = new byte[10000]; // array to hold display file bytes. 10000 bytes can hold a little less than 10KB
        int numberOfBytes = stream.read(displayFileBytes);  // get number of bytes
        outputToBrowser.write(displayFileBytes, 0, numberOfBytes);  // write the display file bytes to browser
  
        displayFile.close(); // close buffered reader
        outputToBrowser.flush(); // flush
		stream.close(); // close to remove resource leak
		tempDisplayFile.delete();  // delete the displayfile
	}
}