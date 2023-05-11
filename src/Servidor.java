import java.net.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;


public class Servidor {

    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private Scanner escaner = new Scanner(System.in);
    private final String COMANDO_TERMINACION = "EXIT";
    private final String USER_CORRECTO = "admin";
    private final String PASS_CORRECTO = "admin";

    public void levantarConexion(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            mostrarTexto("\nEsperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
            socket = serverSocket.accept();
            mostrarTexto("\nConexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
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

            String comando = bufferDeEntrada.readUTF();
            if(comando.startsWith("login")){
                String[] partes = comando.split(" ");
                if(partes.length ==3){
                    String user= partes[1];
                    String pass = partes[2];
                    mostrarTexto("User del cliente: "+user+ "\n\n\n");
                    mostrarTexto("Pass del cliente: "+pass+ "\n\n\n");
                    if(!user.equals(USER_CORRECTO)|| !pass.equals(PASS_CORRECTO)){
                        mostrarTexto( "Se cierra la conexión con el cliente por datos de acceso erróneos\n\n\n");
                        enviar("Usuario y/o contraseña incorrectos");
                        cerrarConexion();
                    }else{
                        enviar("Bienvenid@" + "," +" contínua usando los comandos permitidos (login, reverse, avg, sum, count o numbers) o escribe \"EXIT\" para salir.");

                    }
                }

            }




            //String user = (String) bufferDeEntrada.readUTF();
           // String pass = (String) bufferDeEntrada.readUTF();
           // mostrarTexto("User del cliente: "+user+ "\n\n\n");
          //  mostrarTexto("Pass del cliente: "+pass+ "\n\n\n");
            
//            if(!user.equals(USER_CORRECTO)|| !pass.equals(PASS_CORRECTO)) {
//            	mostrarTexto( "Se cierra la conexión con el cliente por datos de acceso erróneos\n\n\n");
//            	enviar("Usuario y/o contraseña incorrectos");
//            	cerrarConexion();
//            }else {
//            	enviar("Bienvenid@");
//            }
        } catch (IOException e) {
            mostrarTexto("Error en la apertura de flujos");
        }
    }




    public void
    recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) bufferDeEntrada.readUTF();
                funciones(st);


                //mostrarTexto("\n[Cliente] => " + st);
                System.out.print("\n[Usted] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {
            cerrarConexion();
        }
    }

    public void funciones(String comando){

        if(comando.startsWith("login")){
            validarLogin(comando);
        }
        if (comando.startsWith("reverse ")) {
            reverseFrase(comando);
        }
        if (comando.startsWith("avg ")) {
            promedio(comando);
        }
        if (comando.startsWith("sum ")) {
            suma(comando);
        }
        if(comando.startsWith("count ")){
            contarDigitos(comando);
        }
        if(comando.startsWith("numbers ")){
            contarNumerosEnFrase(comando);
        }
    }





    public void validarLogin(String comando){
        String[] partes = comando.split(" ");
        if(partes.length ==3){
            String user= partes[1];
            String pass = partes[2];
            mostrarTexto("User del cliente: "+user+ "\n\n\n");
            mostrarTexto("Pass del cliente: "+pass+ "\n\n\n");
            if(!user.equals(USER_CORRECTO)|| !pass.equals(PASS_CORRECTO)){
                mostrarTexto( "Se cierra la conexión con el cliente por datos de acceso erróneos\n\n\n");
                enviar("Usuario y/o contraseña incorrectos");
            }else{
                enviar("Bienvenid@" );

            }
        }


    }

    public void reverseFrase(String comando){
        String output;
        String frase = comando.substring(8);
        output = new StringBuilder(frase).reverse().toString();
        mostrarTexto(output);
        enviar(output);

    }
    public void promedio(String comando){
        String output;
        String[] partes = comando.split(" ");
        double suma=0;
        int cantNumeros = partes.length -1;
        //calcular numeros
        for(int i=1;i< partes.length;i++){
            double numero = Double.parseDouble(partes[i]);
            suma += numero;
        }
        //calcula promedio
        double promedio = suma/cantNumeros;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        output = decimalFormat.format(promedio);

        mostrarTexto(output);
        enviar(output);

    }

    public void suma(String comando){
        String output;
        String[] partes = comando.split(" ");
        double suma=0;
        int cantNumeros = partes.length -1;
        for(int i=1;i< partes.length;i++){
            double numero = Double.parseDouble(partes[i]);
            suma += numero;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        output = decimalFormat.format(suma);
        mostrarTexto(output);
        enviar(output);

    }

    public void contarDigitos(String comando){
        String output;
        String[] partes = comando.split(" ");
        int numero = Integer.parseInt(partes[1]);
        int cantidadDigitos = String.valueOf(numero).length();
        output =String.valueOf(cantidadDigitos);
        mostrarTexto(output);
        enviar("tiene un total de " + output + " digitos");

    }

    public void contarNumerosEnFrase(String comando){

        String output;
        String frase = comando.substring(8);
        int contador=0;
        for(int i=0;i<frase.length();i++){
            char caracter = frase.charAt(i);
            if(Character.isDigit(caracter)){
                contador++;
            }
        }
        mostrarTexto("la frase tiene "+ contador + " numeros");
        enviar("la frase tiene "+ contador + " numeros");

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
          mostrarTexto("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            mostrarTexto("Conversación finalizada....");
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
