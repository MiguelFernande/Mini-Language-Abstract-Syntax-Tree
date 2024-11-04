//this file is resposible for reading the file with nextChar

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileReader {
    private BufferedReader buffReader;
    private int currentLine = 1;
    private int currentColumn;
    private char currentChar;

    public FileReader(String filePath) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        //this creaters the buffered reader putting through the file input stream and input stream reader allows it to run better.
        this.buffReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        nextChar();
    }

    //next char resposible for grabbing the next charcter in the sequence.

    public void nextChar() throws IOException {
        int charAsInt = buffReader.read();
        if (charAsInt == -1) {
            currentChar = 0;
        } else {
            currentChar = (char) charAsInt;
            if (currentChar == '\r') {
                // If the current char was a carriage return, we'll check if the next char is a newline

                buffReader.mark(1); // mark the current position
                char nextChar = (char) buffReader.read();
                if (nextChar != '\n') {
                    buffReader.reset();
                }
                currentChar = '\n'; // We treat \r the same as \n for line counting purposes.
            }

            if (currentChar == '\n') {
                currentLine++;
                currentColumn = 0;
            } else {
                currentColumn++;
            }
        }
    }

    //peek method used for seeing what the next varieable is useful for analyzing certain symbols. 
    //marks current symbol and returns the one after that
    public char peekNextChar() throws IOException {
        char nextChar;

        buffReader.mark(1);
        nextChar = (char) buffReader.read();
        buffReader.reset();

        return nextChar;
    }

    //getters and setters

    public char getCurrentChar() throws IOException {
        return currentChar;
    }

    public String position() {
        return currentLine + ":" + currentColumn;
    }

    public boolean isReady() throws IOException {
        return buffReader.ready();
    }
}