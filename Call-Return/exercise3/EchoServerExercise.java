import java.net.*;
import java.io.*;

public class EchoServerExercise {
    private static final int PORT = 35000;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine, outputLine;

        while ((inputLine = in.readLine()) != null) {
            try {
                int number = Integer.parseInt(inputLine);
                outputLine = "The square of the number is: " + square(number);
                out.println(outputLine);
            } catch (Exception e) {
                out.println("Error: Input is not a number " + e.getMessage());
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static int square(int number) {
        return number * number;
    }
}