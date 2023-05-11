import java.net.*;
import java.io.*;
import java.util.Scanner;
public class Cliente {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    Scanner teclado = new Scanner(System.in);
    final String COMANDO_TERMINACION = "EXIT";

    public void levantarConexion(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            mostrarTexto("\nConectado a :" + socket.getInetAddress().getHostName()+ " que es el sevidor de este chat");
            mostrarTexto("\nPara terminar la conversaci�n escribe \"EXIT\"");
        } catch (Exception e) {
            mostrarTexto("Excepci�n al levantar conexi�n: " + e.getMessage());
            System.exit(0);
        }
    }

    public static void mostrarTexto(String s) {
        System.out.println(s);
    }
    public void abrirFlujos(String comando) {
   // public void abrirFlujos(String user, String pass) {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
            bufferDeSalida.writeUTF(comando);
           // bufferDeSalida.writeUTF(user);
           // bufferDeSalida.writeUTF(pass);
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }

    public void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            mostrarTexto("IOException on enviar");
        }
    }

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
            mostrarTexto("Conexi�n terminada");
        } catch (IOException e) {
            mostrarTexto("IOException on cerrarConexion()");
        }finally{
            System.exit(0);
        }
    }
    public void ejecutarConexion(String ip, int puerto, String comando){
    //public void ejecutarConexion(String ip, int puerto, String user, String pass) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    levantarConexion(ip, puerto);
                    abrirFlujos(comando);
                    //abrirFlujos(user, pass);
                    recibirDatos();
                } finally {
                    cerrarConexion();
                }
            }
        });
        hilo.start();
    }

    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) bufferDeEntrada.readUTF();
                mostrarTexto("\n[Servidor] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {}
    }

    public void escribirDatos() {
        String entrada = "";
        while (true) {
            System.out.print("[Usted] => ");
            entrada = teclado.nextLine();
            if(entrada.length() > 0)
                enviar(entrada);
        }
    }

    public static void main(String[] argumentos) {
        Cliente cliente = new Cliente();
        Scanner escaner = new Scanner(System.in);
        mostrarTexto("Ingresa la IP: [localhost por defecto] ");
        String ip = escaner.nextLine();
        if (ip.length() <= 0) ip = "localhost";

        mostrarTexto("Puerto: [5050 por defecto] ");
        String puerto = escaner.nextLine();
        if (puerto.length() <= 0) puerto = "5050";

        mostrarTexto("ingrese comando");
        String comando = escaner.nextLine();
        
     //   mostrarTexto("Ingresa tu usuario ");
     //   String user = escaner.nextLine();


    //    mostrarTexto("Ingresa tu contrase�a  ");
    //    String pass = escaner.nextLine();
        cliente.ejecutarConexion(ip, Integer.parseInt(puerto), comando);
        //cliente.ejecutarConexion(ip, Integer.parseInt(puerto), user, pass);
        cliente.escribirDatos();
    }
}
