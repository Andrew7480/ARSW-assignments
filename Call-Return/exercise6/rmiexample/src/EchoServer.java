import java.rmi.Remote;
import java.rmi.RemoteException;

/*
*  Interface que extiende la interface Remote
*/
public interface EchoServer extends Remote {
    public String echo(String cadena) throws RemoteException;
}