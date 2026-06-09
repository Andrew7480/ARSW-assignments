import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
    Clase que implementa la interface EchoServer
 */
public class EchoServerImpl implements EchoServer {
    public EchoServerImpl(String ipRMIregistry, int puertoRMIregistry, String nombreDePublicacion) {
        try {
            EchoServer echoServer = (EchoServer) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry(ipRMIregistry, puertoRMIregistry);
            registry.rebind(nombreDePublicacion, echoServer);
            System.out.println("Echo server ready...");
        } catch (Exception e) {
            System.err.println("Echo server exception:");
            e.printStackTrace();
        }
    }

    public String echo(String cadena) throws RemoteException {
        return "desde el servidor: " + cadena;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(23000);
            System.out.println("Registry started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        EchoServerImpl ec = new EchoServerImpl("127.0.0.1", 23000, "echoServer");
    }
}