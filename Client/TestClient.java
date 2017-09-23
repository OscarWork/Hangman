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

/**
 *
 * @author oscar
 */
public class TestClient {

    public static void main(String[] args) throws IOException {
        Socket server;
        server = new Socket("localhost", 1337);
        BufferedReader rd = new BufferedReader(new InputStreamReader(server.getInputStream()));
        PrintWriter wr = new PrintWriter(server.getOutputStream());
        BufferedReader kd = new BufferedReader(new InputStreamReader(System.in));
        String message = null;
        String answer = null;
        while (true) {
            System.out.print("send message to server: ");
            message = kd.readLine();
            wr.println(message);
            wr.flush();
            //answer = rd.readLine();
            //System.out.println(answer);
            
        }
        

    }
}
