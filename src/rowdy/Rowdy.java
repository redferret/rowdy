package rowdy;


import growdy.Grammar;
import growdy.GRowdy;
import growdy.exceptions.AmbiguousGrammarException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;

import rowdy.nodes.RowdyNode;
import rowdy.nodes.RowdyNodeFactory;
import rowdy.exceptions.ConstantReassignmentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
 * 
 * @author Richard DeSilvey
 */
public class Rowdy {
  
  private final Linker linker;
  private final RowdyInstance rowdyInstance;
  private final GRowdy growdy;
  private final String[] args;
  private String programFileName;
  private boolean verbose;
  public static final RowdyNodeFactory nodeFactory = new RowdyNodeFactory();
  
  public Rowdy(String[] args) {
    rowdyInstance = new RowdyInstance();
    this.args = args;
    Grammar grBuilder = getBuilder();
    RowdyNode.initRunner(rowdyInstance);
    growdy = GRowdy.getInstance(grBuilder, nodeFactory);
    linker = new Linker(growdy, rowdyInstance);
    programFileName = "";
    verbose = false;
    if (args.length > 1) {
      verbose = args[args.length - 1].equalsIgnoreCase("-v");
      programFileName = args[0];
    } else if (args.length == 1){
      verbose = args[0].equalsIgnoreCase("-v");
      if (!verbose) {
        programFileName = args[0];
      }
    }
  }
  
  public void run() {
    if (!programFileName.isEmpty()) {
      List<BaseNode> importTrees = new ArrayList<>();
      RowdyNode mainProgram = null;
      try {
        growdy.buildFromSource(programFileName);
        mainProgram = (RowdyNode) growdy.getProgram();
        linker.loadImports((BaseNode) mainProgram, importTrees);
        importTrees.forEach(tree -> {
          try {
            rowdyInstance.optimizeProgram(tree);
            rowdyInstance.declareGlobals(tree);
          } catch (ConstantReassignmentException ex) {
            rowdyInstance.handleException(ex, verbose);
          }
        });
        try {
          linker.loadJarLibs("bin/");
        } catch (IOException | ClassNotFoundException | URISyntaxException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          rowdyInstance.handleException(ex, verbose);
        }
        
      } catch (IOException | SyntaxException | ParseException | 
              AmbiguousGrammarException e) {
        rowdyInstance.handleException(e, verbose);
        System.exit(500);
      }

      try {
        List<Value> programParameters = new ArrayList<>();
        
        for (int p = 1; p < args.length; p++) {
          String arg = args[p];
          if (!arg.equals("-v")) {
            programParameters.add(new Value(args[p], false));
          }
        }
        rowdyInstance.initialize(mainProgram);
        rowdyInstance.optimizeProgram(mainProgram);
        rowdyInstance.execute(programParameters);
      } catch (Throwable e) {
        rowdyInstance.handleException(e, verbose);
      }
    } else {
      List<BaseNode> compiledImports = new ArrayList<>();
      try {
        growdy.buildFromSource("bin/rowdy/core");
        compiledImports.add((BaseNode) growdy.getProgram());
        linker.loadImports((BaseNode) growdy.getProgram(), compiledImports);
        compiledImports.forEach(compiledImport -> {
          try {
            rowdyInstance.optimizeProgram(compiledImport);
            rowdyInstance.declareGlobals(compiledImport);
          } catch (ConstantReassignmentException ex) {
            rowdyInstance.handleException(ex, verbose);
          }
        });
        linker.loadJarLibs("bin/");
      } catch (Throwable ex) {
        rowdyInstance.handleException(ex, verbose);
      }
      
      rowdyInstance.runAsShell();
      
      Scanner keys = new Scanner(rowdyInstance.getInputStream());
      PrintStream out = (PrintStream) rowdyInstance.getOutputStream();
      
      String line;
      displayVersion();
      do {
        StringBuilder program = new StringBuilder();
        for (;;) {
          out.print(">> ");
          line = keys.nextLine();
          if (line.isEmpty()) {
            break;
          }
          if (line.contains("..")) {
            line = line.replace("..", "\n");
            program.append(line);
          } else {
            program.append(line);
            break;
          }
        }
        if (program.toString().isEmpty()) {
          continue;
        }else if (program.toString().equalsIgnoreCase("exit")) {
          break;
        }
        try {
          growdy.buildFromString(program.toString(), STMT_LIST);
          rowdyInstance.initialize((RowdyNode) growdy.getProgram());
          rowdyInstance.executeLine();
          out.println();
          // Check for a single line import
          String importPath = rowdyInstance.getNextImport();
          if (importPath != null) {
            linker.loadImport(importPath);
            linker.loadJarLibs("bin/");
          }
        } catch (Throwable e) {
          rowdyInstance.handleException(e, verbose);
        }
      } while (true);
    }
  }
  
  public static Grammar getBuilder() {
    Grammar grBuilder = null;
    try {
      try (InputStream inputStream = Rowdy.class.getResourceAsStream("lang/Rowdy.gr");
              ObjectInputStream in = new ObjectInputStream(inputStream)) {
        grBuilder = (Grammar) in.readObject();
      }
    } catch (IOException | ClassNotFoundException i) {
      throw new RuntimeException("There was a problem loading the Grammar: " + i.getMessage());
    }

    return grBuilder;
  }

  private static void displayVersion() {
    System.out.println("Rowdy 1.1.5 (default)");
    System.out.println("Developed on [Java SE Runtime Environment (build 1.8.0_161-b12)\n" +
      "Java HotSpot 64-Bit Server VM (build 25.161-b12, mixed mode)]");
    System.out.println("Current Java Version: " + System.getProperty("java.version"));
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Rowdy rowdy = new Rowdy(args);
    rowdy.run();
  }
  
}
