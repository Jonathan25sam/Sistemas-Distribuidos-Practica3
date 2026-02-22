//package sistemas_distribuidos_practica_3;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteCarniceria {
    public static void main(String[] args) {
        String host = "localhost"; 
        int puerto = 5000;
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CLIENTE DE CARNICERÍA EN TIEMPO REAL ===");

        try {
            
            Socket socket = new Socket(host, puerto);
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

           
            salida.writeUTF("ESTADO");

            
            Thread hiloEscucha = new Thread(() -> {
                try {
                    while (true) {
                        String respuesta = entrada.readUTF(); 
                        System.out.println("\n" + respuesta);
                        System.out.print(">> "); 
                    }
                } catch (IOException e) {
                    System.out.println("\n[SERVIDOR DESCONECTADO]");
                    System.exit(0); // Si el servidor muere, apagamos el cliente
                }
            });
            hiloEscucha.start(); 

           
            System.out.println("-----------------------------------");
            System.out.println("Comandos disponibles:");
            System.out.println("[E]stado        (e)");
            System.out.println("[C]omprar carne (c 1 5)");
            System.out.println("[S]urtir carne  (s 1 10)");
            System.out.println("[X] Salir");
            System.out.println("-----------------------------------");

            while (true) {
               
                String entradaTexto = scanner.nextLine();
                String[] partes = entradaTexto.split(" ");
                String cmd = partes[0].toLowerCase();

                if (cmd.equals("x")) {
                    socket.close();
                    break;
                }

                String mensajeAEnviar = "";
                
                if (cmd.equals("e")) {
                    mensajeAEnviar = "ESTADO";
                } else if (cmd.equals("c") && partes.length >= 3) {
                    mensajeAEnviar = "COMPRAR " + partes[1] + " " + partes[2];
                } else if (cmd.equals("s") && partes.length >= 3) {
                    mensajeAEnviar = "SURTIR " + partes[1] + " " + partes[2];
                } else {
                    System.out.println("Comando no reconocido o incompleto.");
                }

               
                if (!mensajeAEnviar.isEmpty()) {
                    salida.writeUTF(mensajeAEnviar);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
