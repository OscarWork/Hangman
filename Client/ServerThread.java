/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oscar
 */
class ServerThread implements Runnable {

    private HangmanFrame gui;
    private String hostName;
    private int portNumber;
    BufferedReader rd;
    PrintWriter wr;
    LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

    public ServerThread(HangmanFrame gui, String hostName, int portNumber) {
        this.gui = gui;
        this.hostName = hostName;
        this.portNumber = portNumber;

    }

    @Override
    public void run() {
        connect();
        while (true) {
            sendToServ();

        }

    }

    void addMessage(String message) {
        messages.add(message);

    }

    void connect() {
        try {
            Socket server = new Socket(hostName, portNumber);
            rd = new BufferedReader(new InputStreamReader(server.getInputStream()));
            wr = new PrintWriter(server.getOutputStream());
            gui.connected();
        } catch (IllegalArgumentException ae){
            System.err.println("Port number must be numbers between 0 and 65535");
            System.exit(1);
        } 
        catch (IOException ioe) {
            System.err.println("Could not connect to host: "+ hostName + 
                    " on port: "+portNumber);
            System.exit(1);
        }
        
    }

    void sendToServ() {
        try {
            String message = messages.take();
            wr.println(message);
            wr.flush();
            String line;
            line = rd.readLine();
            if(line.equals("hope you had fun playing")){
                System.exit(0);
            }
            System.out.println(line);
            String[] result = line.split("\\|");
            if(message.equals("startgame")){        // om det var ett start meddelande vi skickade, gör start callback
                gui.gameStarted(result[0], result[1]);
            }
            else if (line.contains("[")) {     // om det ska fortsätta gissas
                gui.sentGuess(result[0], result[1]);
            }
            else if(line.contains("Congratulations") || line.contains("Game over!")){      // om spelet är slut
                gui.gameDone(result[0], result[1]);
            }
            
        } catch (InterruptedException ex) {
            System.out.println("could not read the message from queue");
        } catch (IOException ex) {
            System.out.println("could not read the answer from server");
        }
    }

//    void startGame() {
//        wr.println("start");
//        wr.flush();
//        String line;
//        try {
//            line = rd.readLine();
//            System.out.println(line);
//            String[] result = line.split("\\|");
//            gui.gameStarted(result[0], result[1]);
//            //System.out.println(Arrays.toString(result));
//            // System.out.println(result[0]);
//        } catch (IOException ex) {
//            System.out.println("could not get response from server");
//        }
//
//    }
//
//    void sendGuess(String guess
//    ) {
//        wr.println(guess);
//        wr.flush();
//        String line;
//        try {
//            line = rd.readLine();
//            System.out.println(line);
//            if (line.contains("[")) {     // om det ska fortsätta gissas
//                String[] result = line.split("\\|");
//                gui.sentGuess(result[0], result[1]);
//            } else if (line.contains("Congratulations") || line.contains("Game over!")) {
//                String[] result = line.split("\\|");
//                gui.gameDone(result[0], result[1]);
//            }
//        } catch (IOException ex) {
//            System.out.println("could not get response from server");
//        }
//    }

}
