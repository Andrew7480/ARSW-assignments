import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Ingrese su nombre: ");
            String nombre = scanner.nextLine();

            System.out.print("Puerto local: ");
            int puertoLocal = Integer.parseInt(scanner.nextLine());

            LocateRegistry.createRegistry(puertoLocal);

            ChatServiceImpl localImpl = new ChatServiceImpl();

            ChatService localStub = (ChatService) UnicastRemoteObject.exportObject(localImpl, 0);
            Registry localRegistry = LocateRegistry.getRegistry("localhost", puertoLocal);

            localRegistry.rebind("chat", localStub);

            System.out.println("Servicio publicado en puerto " + puertoLocal);

            System.out.print("IP remota: ");
            String ipRemota = scanner.nextLine();

            System.out.print("Puerto remoto: ");
            int puertoRemoto = Integer.parseInt(scanner.nextLine());

            System.out.println("\nEsperando conexión con el otro usuario...");

            ChatService remoto = null;

            while (remoto == null) {

                try {

                    Registry remoteRegistry = LocateRegistry.getRegistry(ipRemota, puertoRemoto);

                    remoto = (ChatService) remoteRegistry.lookup("chat");

                    System.out.println("Conectado al chat.\n");

                } catch (Exception e) {
                    System.out.println("The remote service is not available yet. Retrying in 2 seconds...");
                    Thread.sleep(2000);
                }
            }

            while (true) {

                System.out.print("> ");

                String mensaje = scanner.nextLine();

                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Chat finalizado.");
                    break;
                }

                remoto.recibirMensaje(nombre + ": " + mensaje);
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Error en el chat:");
            e.printStackTrace();
        }
    }
}