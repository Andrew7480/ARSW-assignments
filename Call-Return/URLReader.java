import java.io.*;
import java.net.*;

/*
    Example of reading from a URL.
*/
public class URLReader {
    public static void main(String[] args) throws Exception {
        URL google = URI.create("http://www.google.com/").toURL();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(google.openStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException x) {
            System.err.println(x);
        }
    }
}