//had to make my exception being a lexical excpetion. as excpected spit out what it is and where it is.

import javax.swing.text.Position;

public class LexicalException extends Exception{

    public LexicalException(String msg) 
    {
        super(msg);
    }
    
    public LexicalException(char ch, Position position) 
    {
        super("Unexpected character: " + ch + " at " + position);
    }
    
}
