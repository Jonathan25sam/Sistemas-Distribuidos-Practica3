//package sistemas_distribuidos_practica_3;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServidorCarniceria {
   
    static Carniceria tienda1 = new Carniceria("carnicería 'La carniceria del pueblo'", 10);
    static Carniceria tienda2 = new Carniceria("carnicería 'El del sombrero'", 10);
    static Carniceria tienda3 = new Carniceria("carnicería 'El toro bravo '", 10);
 
    // 1. LISTA DE CLIENTES CONECTADOS
    public static List<DataOutputStream> clientesConectados = Collections.synchronizedList(new ArrayList<>());

    // 2. MÉTODO PARA GRITARLE A TODOS QUE EL STOCK CAMBIÓ
    public static void avisarATodos(String mensaje) {
        synchronized(clientesConectados) {
            for (DataOutputStream cliente : clientesConectados) {
                try {
                    cliente.writeUTF(mensaje);
                } catch (IOException e) {
                   
                }
            }
        }
    }
    

    public static String obtenerEstadoGlobal() {
        return "--- INVENTARIO GLOBAL ACTUALIZADO ---\n" +
               "1. " + tienda1.getEstado() + "\n" +
               "2. " + tienda2.getEstado() + "\n" +
               "3. " + tienda3.getEstado() + "\n" +
               "-------------------------------------";
    }
 
    public static void main(String[] args) {
        int puerto = 5000; 

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("====================================");
            System.out.println("   SERVIDOR MULTICLIENTE TIEMPO REAL");
            System.out.println("   Esperando clientes en puerto " + puerto + "...");
            System.out.println("====================================");

            while (true) {
                Socket clienteSocket = servidor.accept();
                System.out.println("--> Nuevo cliente conectado: " + clienteSocket.getInetAddress());
                
                new Thread(new ManejadorCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Carniceria getTienda(int id) {
        switch (id) {
            case 1: return tienda1;
            case 2: return tienda2;
            case 3: return tienda3;
            default: return null;
        }
    }
}

// HILO QUE ATIENDE A CADA CLIENTE INDIVIDUALMENTE
class ManejadorCliente implements Runnable {
    private Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        DataInputStream entrada = null;
        DataOutputStream salida = null;
        
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
            
           
            ServidorCarniceria.clientesConectados.add(salida);

           
            while (true) {
                String mensaje = entrada.readUTF();
                String[] partes = mensaje.split(" ");
                String comando = partes[0];

               if (comando.equals("ESTADO")) {
                   
                    salida.writeUTF(ServidorCarniceria.obtenerEstadoGlobal());
                } 
                else if (comando.equals("COMPRAR")) {
                    int idTienda = Integer.parseInt(partes[1]);
                    int cantidad = Integer.parseInt(partes[2]);
                    Carniceria tienda = ServidorCarniceria.getTienda(idTienda);
                    
                    if (tienda != null) {
                        boolean exito = tienda.comprarCarne(cantidad);
                        if (exito) {
                            salida.writeUTF("COMPRA EXITOSA");
                           
                            ServidorCarniceria.avisarATodos("📢 ¡ALGUIEN COMPRÓ CARNE!\n" + ServidorCarniceria.obtenerEstadoGlobal());
                        } else {
                            salida.writeUTF("ERROR: No hay stock suficiente en espera.");
                        }
                    }
                } 
                else if (comando.equals("SURTIR")) {
                    int idTienda = Integer.parseInt(partes[1]);
                    int cantidad = Integer.parseInt(partes[2]);
                    Carniceria tienda = ServidorCarniceria.getTienda(idTienda);
                    
                    if (tienda != null) {
                        tienda.resurtirCarne(cantidad);
                        salida.writeUTF("SURTIDO EXITOSO");
                     ServidorCarniceria.avisarATodos("📢 ¡LLEGÓ CARNE NUEVA!\n" + ServidorCarniceria.obtenerEstadoGlobal());
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("--> Un cliente se desconectó.");
        } finally {
          
            if (salida != null) {
                ServidorCarniceria.clientesConectados.remove(salida);
            }
            try { socket.close(); } catch (IOException e) {}
        }
    }
}

class Carniceria {
    private String nombre;
    private int stock;
    
    public Carniceria(String nombre, int stock) { this.nombre = nombre; this.stock = stock; }

    public String getEstado() { return String.format("[%s] Stock: %d", nombre, stock); }

    public synchronized boolean comprarCarne(int cantidad) {
        while (stock < cantidad) {
            try {
                System.out.println("   [ESPERA] Stock insuficiente en " + nombre);
                wait(); 
            } catch (InterruptedException e) { return false; }
        }
        stock -= cantidad;
        notifyAll();
        System.out.println("   [VENTA] Se vendieron " + cantidad + "kg en " + nombre);
        return true;
    }

    public synchronized void resurtirCarne(int cantidad) {
        stock += cantidad;
        System.out.println("   [SURTIDO] Llegaron " + cantidad + "kg a " + nombre);
        notifyAll();
    }
}
