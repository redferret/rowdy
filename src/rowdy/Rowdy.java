package rowdy;


import growdy.GRBuilder;
import growdy.GRowdy;
import growdy.exceptions.AmbiguousGrammarException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.exceptions.MainNotFoundException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;
import rowdy.nodes.RowdyNodeFactory;

/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
 * 
 * @author Richard DeSilvey
 */
public class Rowdy {
  
  public static GRBuilder getBuilder() {
    GRBuilder grBuilder = null;
      try {
        try (InputStream inputStream = Rowdy.class.getResourceAsStream("lang/Rowdy.gr"); 
                ObjectInputStream in = new ObjectInputStream(inputStream)) {
          grBuilder = (GRBuilder) in.readObject();
        }
        } catch (IOException | ClassNotFoundException i) {
          throw new RuntimeException("There was a problem loading the Grammar: " + i.getLocalizedMessage());
        }

      return grBuilder;
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
      GRBuilder grBuilder = getBuilder();
      RowdyRunner rowdyProgram = new RowdyRunner();
      RowdyNodeFactory factory = new RowdyNodeFactory(rowdyProgram);
      GRowdy growdy = GRowdy.getInstance(grBuilder, factory);
      if (args.length > 0) {
        String programFileName = args[0];

        try {
          growdy.buildFromSource(programFileName);
        rowdyProgram.initialize(growdy);
      } catch (IOException | SyntaxException | ParseException | AmbiguousGrammarException e) {
        handleException(rowdyProgram, e);
        System.exit(500);
      }

      try {
        List<Value> programParameters = new ArrayList<>();

        for (int p = 1; p < args.length; p++) {
          String in = args[p];
          if (Character.isDigit(in.charAt(0))) {
            programParameters.add(new Value(Double.parseDouble(args[p])));
          } else {
            String argStr = args[p];
            if (argStr.equals("true") || argStr.equals("false")) {
              programParameters.add(new Value(Boolean.valueOf(argStr)));
            } else {
              programParameters.add(new Value(args[p]));
            }
          }
        }
        rowdyProgram.execute(programParameters);
      } catch (NumberFormatException | MainNotFoundException | 
              ConstantReassignmentException e) {
        handleException(rowdyProgram, e);
      }
    } else {
      Scanner keys = new Scanner(System.in);
      String line;
      do {
        StringBuilder program = new StringBuilder();
        for(;;){
          line = keys.nextLine();
          if (line.isEmpty()) {
            break;
          }
          if (line.contains("\\\\")){
            line = line.replace("\\\\", "\n");
            program.append(line);
            line = "";
          } else {
            program.append(line);
            break;
          }
        }
        if (program.toString().isEmpty()){
          continue;
        }
        if (program.toString().equalsIgnoreCase("exit")) {
          break;
        }
        try {
          growdy.buildFromString(program.toString(), STMT_LIST);
          rowdyProgram.initializeLine(growdy);
        } catch (ParseException | SyntaxException | AmbiguousGrammarException e) {
          handleException(rowdyProgram, e);
          continue;
        }

        try {
          rowdyProgram.executeLine();
        } catch (Exception | ConstantReassignmentException e) {
          handleException(rowdyProgram, e);
        }
      }while(true);
    }

  }

  private static void handleException(RowdyRunner rowdyProgram, Throwable e) {
    System.out.println(e.getClass().getCanonicalName() + ": " + e.getLocalizedMessage());
    rowdyProgram.dumpCallStack();
    e.printStackTrace();
  }
}
