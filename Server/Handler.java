/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author oscar
 */
class Handler implements Runnable {

    private String wordsPath = "Server/words.txt";
    private Socket clientSocket = null;
    private List<String> dictionair = new ArrayList<String>();
    private String word;
    private char[] guess = null;
    private int totalScore = 0;
    private int remainingGuesses;
    private static final int MAX_GUESSES = 5;

    public Handler(Socket clientSocket) {

        this.clientSocket = clientSocket;

    }

    @Override
    public void run() {

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter wr = new PrintWriter(clientSocket.getOutputStream());
            while (true) {  // main loop, antingen så startar man eller disconnectar
                // , 
                String line;
                while ((line = rd.readLine()) != null) {
                    if (line.equals("startgame")) {     // start game
                        System.out.println("Start game message");
                        initializeGame();
                        wr.println(Arrays.toString(guess) + "|" + remainingGuesses);
                        wr.flush();
                        System.out.println(Arrays.toString(guess) + " " + remainingGuesses);

                        while ((line = rd.readLine().toUpperCase()) != null) {       
                            
                            if (line.length() > 1) {    // om clienten gissat ett ord
                                if (line.equals(word)) {        // rätt ord
                                    totalScore = totalScore + 1;
                                    wr.println("Congratulations! the word was: " + word + "|" + totalScore);
                                    wr.flush();
                                    System.out.println("Congratulations! the word was: " + word + "|" + totalScore);
                                    break;
                                } else {      // fel ord
                                    remainingGuesses = remainingGuesses - 1;
                                    if (remainingGuesses > 0) {
                                        wr.println(Arrays.toString(guess) + "|" + remainingGuesses);
                                        wr.flush();
                                        System.out.println(Arrays.toString(guess) + "|"+ remainingGuesses);
                                    } 
                                    else {
                                        totalScore = totalScore - 1;
                                        wr.println("Game over!|" + totalScore);
                                        wr.flush();
                                        System.out.println("Game over!|" + totalScore);
                                        break;
                                    }
                                }

                            } else { // om clienten gissar en bokstav

                                if (word.contains(line)&&remainingGuesses>0) {  // om bokstaven finns i ordet

                                    for (int i = 0; word.length() > i; i++) {      // byt ut + mot bokstaven i guess
                                        if (word.charAt(i) == line.charAt(0)) {
                                            guess[i] = line.charAt(0);
                                        }
                                    }
                                    if (this.compareGuess(word, guess)) { // om ordet blev klart av denna gissning
                                        totalScore = totalScore + 1;
                                        wr.println("Congratulations! the word was: " + word + "|" + totalScore);
                                        wr.flush();
                                        System.out.println("Congratulations! the word was: " + word + "|" + totalScore);
                                        break;
                                    }
                                    else{ // om ordet inte är klart än
                                        wr.println(Arrays.toString(guess) + "|" + remainingGuesses);
                                        wr.flush();
                                        System.out.println(Arrays.toString(guess) + "|"+ remainingGuesses);
                                    }
                                }
                                else{ //  om bokstaven inte finns i ordet
                                    remainingGuesses = remainingGuesses -1;
                                    if (remainingGuesses > 0) {
                                        wr.println(Arrays.toString(guess) + "|" + remainingGuesses);
                                        wr.flush();
                                        System.out.println(Arrays.toString(guess) + "|"+ remainingGuesses);
                                    } 
                                    else {
                                        totalScore = totalScore - 1;
                                        wr.println("Game over!|" + totalScore);
                                        wr.flush();
                                        System.out.println("Game over!|" + totalScore);
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (line.equals("exitgame")) {
                        wr.println("hope you had fun playing");
                        wr.flush();
                        System.out.println("disconnecting client");
                        clientSocket.close();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("something went wrong with the connection to client");
        }
    }

    private void initializeGame() {
        dictionair = readAndShuffleWords(wordsPath); // read words into arraylist, shuffle and return list.
        word = dictionair.get(0).toUpperCase();  // take first word as it is allready shuffled
        guess = new char[word.length()];
        Arrays.fill(guess, '+');        // fill guess with + characters
        remainingGuesses = MAX_GUESSES;
        System.out.println(word);       // for testing
          System.out.println(guess);      // for testing
    }

    private List<String> readAndShuffleWords(String path) {
        List<String> words = new ArrayList<String>();
        try {
            BufferedReader wordBuffer = new BufferedReader(new FileReader(path));
            String word;
            while ((word = wordBuffer.readLine()) != null) {
                words.add(word);

            }
        } catch (FileNotFoundException ex) {
            System.err.println("could not find file :" + path);

        } catch (IOException ex) {
            System.err.println("Something went wring reading file with words");
        }
        Collections.shuffle(words);
        return words;
    }
    private boolean compareGuess(String word, char[] guess){
        boolean bool = true;
        for(int i =0; word.length()>i;i++)
            if(word.charAt(i)!=guess[i]){
                bool=false;
                break;
            }
        return bool;
    }

}
