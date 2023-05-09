import java.net.*;
import java.io.*;
import java.util.Scanner;


public class Servidor {

    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private Scanner escaner = new Scanner(System.in);
    private final String COMANDO_TERMINACION = "salir()";
    private final String USER_CORRECTO = "admin";
    private final String PASS_CORRECTO = "admin";

    public void levantarConexion(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            mostrarTexto("\nEsperando conexi�n entrante en el puerto " + String.valueOf(puerto) + "...");
            socket = serverSocket.accept();
            mostrarTexto("\nConexi�n establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
        } catch (Exception e) {
            mostrarTexto("Error en levantarConexion(): " + e.getMessage());
            System.exit(0);
        }
    }
    public void flujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
            String user = (String) bufferDeEntrada.readUTF();
            String pass = (String) bufferDeEntrada.readUTF();
            mostrarTexto("User del cliente: "+user+ "\n\n\n");
            mostrarTexto("Pass del cliente: "+pass+ "\n\n\n");
            
            if(!user.equals(USER_CORRECTO)|| !pass.equals(PASS_CORRECTO)) {
            	mostrarTexto( "Se cierra la conexi�n con el cliente por datos de acceso err�neos\n\n\n");
            	enviar("Usuario y/o contrase�a incorrectos");
            	cerrarConexion();
            }else {
            	enviar("Bienvenid@");
            }
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }

    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) bufferDeEntrada.readUTF();
                mostrarTexto("\n[Cliente] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {
            cerrarConexion();
        }
    }


    public void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("Error en enviar(): " + e.getMessage());
        }
    }

    public static void mostrarTexto(String s) {
        System.out.print(s);
    }

    public void escribirDatos() {
        while (true) {
            System.out.print("[Usted] => ");
            String texto = escaner.nextLine();
            String letrasImpares ="";

            for(int i=0;i<texto.length();i++){
                if(i%2!=0){
                    char letra = texto.charAt(i);
                    letrasImpares +=letra;
                }

            }
            enviar(texto + " " + " - "+ letrasImpares.toString()
            );
        }
    }

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
          mostrarTexto("Excepci�n en cerrarConexion(): " + e.getMessage());
        } finally {
            mostrarTexto("Conversaci�n finalizada....");
            System.exit(0);

        }
    }

    public void ejecutarConexion(int puerto) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        levantarConexion(puerto);
                        flujos();
                        recibirDatos();
                    } finally {
                        cerrarConexion();
                    }
                }
            }
        });
        hilo.start();
    }




    public static void main(String[] args) throws IOException {
        Servidor s = new Servidor();
        Scanner sc = new Scanner(System.in);

        mostrarTexto("Ingresa el puerto [5050 por defecto]: ");
        String puerto = sc.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        s.ejecutarConexion(Integer.parseInt(puerto));
        s.escribirDatos();
    }
}
