import java.rmi.RemoteException;

public class ChatServiceImpl implements ChatService {

    @Override
    public void recibirMensaje(String mensaje) throws RemoteException {
        System.out.println();
        System.out.println("Mensaje recibido:" + mensaje);
        System.out.print("> ");
    }
}