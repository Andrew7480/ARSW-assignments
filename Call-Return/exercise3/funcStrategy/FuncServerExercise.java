package funcStrategy;
import java.net.*;

import java.io.*;

public class FuncServerExercise {
    private static final int PORT = 35000;
    public static void main(String[] args) throws IOException {
        Cos cos = new Cos();
        Sen sen = new Sen();
        Tan tan = new Tan();
        ServerSocket serverSocket = null;
        FuncStrategy funcStrategy = cos;
        
        
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
            if (inputLine.startsWith("fun:")) {
                String funcName = inputLine.substring(4);
                System.out.println("Received function change request: " + funcName);
                switch (funcName) {
                    case "cos":
                        funcStrategy = cos;
                        break;
                    case "sen":
                        funcStrategy = sen;
                        break;
                    case "tan":
                        funcStrategy = tan;
                        break;
                    default:
                        out.println("Error: Function not recognized");
                        continue;
                }
                out.println("Function set to: " + funcName);
                continue;
            }
            try {
                Double number = Double.parseDouble(inputLine);
                outputLine = "The result of the function " + funcStrategy.getName() + " is: " + funcStrategy.execute(number);
                out.println(outputLine);
            } catch (NumberFormatException  e) {
                out.println("Error: Input is not a number " + e.getMessage());
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}