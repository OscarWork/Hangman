/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author oscar
 */
public class Hangman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int poolSize = 3;
        int portNumber = 1337;
        try {                       // hantera första inparametern
            poolSize = Integer.parseInt(args[0]);
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("using standard value for poolsize :" + poolSize);
        } catch (java.lang.NumberFormatException nfe) {
            System.err.println("poolsize parameter must be a number");
            System.exit(1);
        }
        try{                    // hantera den andra inparametern
            portNumber = Integer.parseInt(args[1]);
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("using standard value for port number :" + portNumber);
        } catch (java.lang.NumberFormatException nfe) {
            System.err.println("Port number parameter must be a number");
            System.exit(1);
        }
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ServerSocket listen;
        try {
            System.out.println("starting server with maximum "+poolSize+" threads at a time that is accepting requests on port: "+portNumber);
            listen = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = listen.accept();
                System.out.println("client connected");
                executor.execute(new Handler(clientSocket));
            }
        } catch (IOException ex) {
            System.out.println("cannot listen to port :" + portNumber);
            System.exit(1);
        }
        
    }
    
}
