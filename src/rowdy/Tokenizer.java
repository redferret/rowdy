package rowdy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Tokenizer parses code files and allows a fetch for
 each individual token when getID() is called. Once the token
 is called for it is removed from the file stack. The file stack contains
 groupings of characters which are later interpreted when getID is called.
 * @author Richard DeSilvey
 */
public class Tokenizer {

    private static final String DIGIT = "0123456789";
    private final String OPERT;
    
    private int identifierID, constantID;
    /**
     * The table of all the reserved words/symbols for the language.
     */
    private Hashtable<String, Integer> reservedWords;
    
    /**
     * Each pattern aids in parsing the file and fetching a token.
     */
    private static final Pattern id, number, tokenNumber, whiteSpace, tokenString;
    
    private Stack<String> fileStack;
    
    static {
        // Set up each regular expression
        id = Pattern.compile("([a-z]|[A-Z])+(\\_|\\d|[a-z]|[A-Z])*");
        whiteSpace = Pattern.compile("\\s");
        tokenString = Pattern.compile("\"(.)*\"");
        number = Pattern.compile("-?\\d*\\.?\\d*(F|f|D|d)?");
        tokenNumber = Pattern.compile("-?\\d+(\\.?\\d+)?(F|f|D|d)?");
    }

    
    public Tokenizer(String[] reserved, String opert, int idID, int constID){

        reservedWords = new Hashtable<>();
        OPERT = opert;
        
        for (int i = 0; i < reserved.length; i++) {
            if (!reserved[i].isEmpty()) {
                reservedWords.put(reserved[i], i);
            }
        }
        identifierID = idID;
        constantID = constID;
    }
    
    public void setIdentifiersAs(int id){
        this.identifierID = id;
    }
    
    public void setConstantsAs(int id){
        this.constantID = id;
    }
    /**
     * Parses the code file given. New tokens will be generated each time
     * parse is called on a given file.
     * @param fileName The code file being parsed.
     */
    public void parse(String fileName){
        fileStack = parseFile(fileName);
    }
    
    /**
     * Gets the contents of a file and pushes all relevant groupings of
     * characters to a stack.
     * @param fileName The file name for the datafile
     * @return The stack of grouped characters
     */
    private Stack<String> parseFile(String fileName){
        
        Stack<String> symbols = new Stack<>();
        String line = null;
        File file = new File(fileName);
        BufferedReader reader = null;
        boolean eoln = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            char cur;
            String word = "";
            while ((line = reader.readLine()) != null) {
                
                for (int c = 0, prev = 0; c < line.length(); c++){
                    cur = line.charAt(c);
                    
                    if (cur == '/'){
                        if (line.charAt(c + 1) == '/'){
                            break;
                        }
                    }
                    if (id.matcher(Character.toString(cur)).matches()){
                        word += cur;
                        do{
                            c++;
                            if (c >= line.length()) {
                                eoln = true;
                                break;
                            }
                            cur = line.charAt(c);
                            word += cur;
                        }while (id.matcher(word).matches());
                    }else if (cur == '\"'){
                        word += cur;
                        do{
                            c++;
                            if (c >= line.length()) {
                                break;
                            }
                            cur = line.charAt(c);
                            word += cur;
                        }while (cur != '\"');
                        eoln = true;
                    }else if(DIGIT.contains(Character.toString(cur))){
                        word += cur;
                        do{
                            c++;
                            if (c >= line.length()) {
                                eoln = true;// Flag adjustments
                                break;
                            }
                            cur = line.charAt(c);
                            word += cur;
                        }while (number.matcher(word).matches());
                    }else if (OPERT.contains(Character.toString(cur))
                                && cur != ' '){
                        word += cur;
                        do{
                            c++;
                            if (c >= line.length()) {
                                eoln = true;// Flag adjustments
                                break;
                            }
                            cur = line.charAt(c);
                            word += cur;
                        }while (OPERT.contains(word) && cur != ' ');
                    }
                    
                    if (!word.isEmpty()) {
                        symbols.push(
                                (eoln) ? 
                                    word 
                                :// Else
                                    word.substring(0, word.length() - 1));
                    }
                    
                    if (!whiteSpace.matcher(Character.toString(cur)).matches()){
                        if (c == prev){ // Stop parsing on unknown symbol
                            throw new RuntimeException("Parsing halted, unable"
                                    + " to resolve character '" + cur + "'");
                        }
                        if (c < line.length() && !eoln) {
                            prev = c;
                            c--;
                        }
                    }
                    eoln = false;
                    word = "";
                }
                
                symbols.push("EOLN");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        
        // Flip the contents of the stack ignoring any possible empty strings
        Stack<String> temp = new Stack<>();
        while(!symbols.isEmpty()){
            String s = symbols.pop();
            if (!s.isEmpty()) {
                temp.push(s);
            }
        }
        
        return temp;
        
    }
    
    /**
     * Tests if there still exists a token to consume.
     * @return True if there still exists tokens.
     */
    public boolean hasToken(){
        return !fileStack.isEmpty();
    }
    
    /**
     * Fetches a token from the given file.
     * @return The next token, null if there are no more tokens left to
     * consume.
     */
    public Token getToken(){
        
        if (fileStack.isEmpty()){
            return null;
        }
        
        String symbol = fileStack.pop();
        Integer tokenId = reservedWords.get(symbol);
        
        if (tokenId == null){
            
            switch(symbol){
                
                case ".":
                    return new Token(0, "PERIOD");
                case "EOLN":
                    return new Token(200, "EOLN");
                default:
                    if (id.matcher(symbol).matches()){
                        return new Token(identifierID, symbol);
                    }else if (tokenString.matcher(symbol).matches()){
                        return new Token(constantID, symbol);
                    }else if (tokenNumber.matcher(symbol).matches()){
                        return new Token(constantID, symbol);
                    }else{
                        return new Token(-1, "UNKNOWN '" + symbol + "'");
                    }
            }
            
        }else{
            return new Token(tokenId, symbol);
        }
        
    }
    
}

