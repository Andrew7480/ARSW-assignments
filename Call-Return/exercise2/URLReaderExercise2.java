import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
*/
public class URLReaderExercise2 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a URL: ");
        String urlString = scanner.nextLine();

        URL url = URI.create(urlString).toURL();

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                BufferedWriter writer = new BufferedWriter(new FileWriter("resultado.html"));) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                writer.write(inputLine);
                writer.newLine();
            }
            System.out.println("Content saved to resultado.html");
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}