import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* clase que implementa un cliente de datagramas
*/
public class DatagramTimeClientExercise5 {
    @SuppressWarnings("java:S2189")
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(4000);

            byte[] buf = new byte[256];
            String lastTime = "No time received yet";
            InetAddress address = InetAddress.getByName("127.0.0.1");

            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
                    socket.send(packet);

                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

                    socket.receive(receivePacket);

                    String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    lastTime = received;
                    System.out.println("Date: " + lastTime);
                } catch (SocketTimeoutException e) {
                    System.out.println("Server not responding. Keeping last time: " + lastTime);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DatagramTimeClientExercise5.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SocketException ex) {
            Logger.getLogger(DatagramTimeClientExercise5.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DatagramTimeClientExercise5.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatagramTimeClientExercise5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}