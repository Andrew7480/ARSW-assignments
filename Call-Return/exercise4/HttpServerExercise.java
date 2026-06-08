import java.net.*;
import java.io.*;

public class HttpServerExercise {
    @SuppressWarnings("java:S2189")

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        System.out.println("Server is listening on port 35000...");

        while (true) {
            Socket clientSocket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outStream = clientSocket.getOutputStream();

            String requestLine = in.readLine();
            System.out.println();
            System.out.println("Request: " + requestLine);

            String file = requestLine.split(" ")[1];

            if (file.equals("/")) {
                file = "/index.html";
            }
            String path = "Call-Return/exercise4/web" + file;
            System.out.println("Serving file: " + path);

            File fileToServe = new File(path);

            try {

                String contentType;
                if (file.endsWith(".html")) {
                    contentType = "text/html";
                } else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (file.endsWith(".png")) {
                    contentType = "image/png";
                } else {
                    contentType = "application/octet-stream";
                }

                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n\r\n";

                outStream.write(header.getBytes());

                if (contentType.equals("text/html")) {

                    BufferedReader fileReader = new BufferedReader(new FileReader(fileToServe));

                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        outStream.write(line.getBytes());
                    }

                    fileReader.close();

                }
                else {

                    FileInputStream fileInputStream = new FileInputStream(fileToServe);

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }

                    fileInputStream.close();
                }

                outStream.flush();

            } catch (Exception e) {

                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n\r\n" +
                        "<h1>404 File Not Found</h1>";

                outStream.write(response.getBytes());
                outStream.flush();
            }

            outStream.close();
            in.close();
            clientSocket.close();
        }
    }
}