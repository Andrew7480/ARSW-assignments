import java.net.*;
import java.util.Scanner;

/*
*/
public class URLReaderExercise1 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a URL: ");
        String urlString = scanner.nextLine();

        URL url = URI.create(urlString).toURL();

        String protocol = url.getProtocol();
        String authority = url.getAuthority();
        String host = url.getHost();
        int port = url.getPort();
        String path = url.getPath();
        String query = url.getQuery();
        String file = url.getFile();
        String ref = url.getRef();

        System.out.println("Protocol: " + protocol);
        System.out.println("Authority: " + authority);
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Path: " + path);
        System.out.println("Query: " + query);
        System.out.println("File: " + file);
        System.out.println("Ref: " + ref);
    }
}