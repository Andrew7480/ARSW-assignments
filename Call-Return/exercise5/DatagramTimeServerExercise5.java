import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* clase que implementa un servidor de datagramas
*/
public class DatagramTimeServerExercise5 {
    DatagramSocket socket;

    public DatagramTimeServerExercise5() {
        try {
            socket = new DatagramSocket(4445);
        } catch (SocketException ex) {
            Logger.getLogger(DatagramTimeServerExercise5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @SuppressWarnings("java:S2189")
    public void startServer() {
        byte[] buf = new byte[256];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String dString = new Date().toString();
                buf = dString.getBytes();
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(DatagramTimeServerExercise5.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        DatagramTimeServerExercise5 ds = new DatagramTimeServerExercise5();
        ds.startServer();
    }
}