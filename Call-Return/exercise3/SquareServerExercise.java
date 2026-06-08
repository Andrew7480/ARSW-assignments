
import java.net.*;
import java.io.*;

public class SquareServerExercise {
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

        System.out.println("Server is running and waiting for client input...");

        while ((inputLine = in.readLine()) != null) {
            try {
                double number = Double.parseDouble(inputLine);
                outputLine = "The square of the number is: " + square(number);
                out.println(outputLine);
            } catch (NumberFormatException e) {
                out.println("Error: Input is not a number " + e.getMessage());
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static double square(double number) {
        return number * number;
    }
}