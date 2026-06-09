import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {

    void recibirMensaje(String mensaje) throws RemoteException;

}