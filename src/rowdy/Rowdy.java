package rowdy;


import growdy.GRBuilder;
import growdy.GRowdy;
import growdy.Terminal;
import growdy.exceptions.AmbiguousGrammarException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.RowdyNodeFactory;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.exceptions.MainNotFoundException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static rowdy.lang.RowdyGrammarConstants.ID;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
 * 
 * @author Richard DeSilvey
 */
public class Rowdy {
  private final RowdyRunner rowdyProgram;
  private final GRowdy growdy;
  private final String[] args;
  private String programFileName;
  private boolean verbose;
  
  public Rowdy(String[] args) {
    rowdyProgram = new RowdyRunner();
    this.args = args;
    GRBuilder grBuilder = getBuilder();
    RowdyNodeFactory factory = new RowdyNodeFactory();
    RowdyNode.initRunner(rowdyProgram);
    growdy = GRowdy.getInstance(grBuilder, factory);
    programFileName = "";
    if (args.length == 1) {
      verbose = args[args.length - 1].equalsIgnoreCase("-verbose");
      if (!verbose) {
        programFileName = args[0];
      }
    }
  }
  
  public void run() {
    if (!programFileName.isEmpty()) {
      
      try {
        growdy.buildFromSource(programFileName);
        rowdyProgram.initialize(growdy);
        rowdyProgram.declareGlobals();
        try {

          loadJarLibs("bin/RowdyLib.jar");
        } catch (IOException | ClassNotFoundException | URISyntaxException | 
                IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          handleException(ex);
        }
        
        
      } catch (IOException | SyntaxException | ParseException | 
              AmbiguousGrammarException | ConstantReassignmentException e) {
        handleException(e);
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
      } catch (NumberFormatException | MainNotFoundException
              | ConstantReassignmentException e) {
        handleException(e);
      }
    } else {
      Scanner keys = new Scanner(System.in);
      String line;
      do {
        StringBuilder program = new StringBuilder();
        for (;;) {
          line = keys.nextLine();
          if (line.isEmpty()) {
            break;
          }
          if (line.contains("\\\\")) {
            line = line.replace("\\\\", "\n");
            program.append(line);
            line = "";
          } else {
            program.append(line);
            break;
          }
        }
        if (program.toString().isEmpty()) {
          continue;
        }
        if (program.toString().equalsIgnoreCase("exit")) {
          break;
        }
        try {
          growdy.buildFromString(program.toString(), STMT_LIST);
          rowdyProgram.initialize(growdy);
        } catch (ParseException | SyntaxException | AmbiguousGrammarException e) {
          handleException(e);
          continue;
        }

        try {
          rowdyProgram.executeLine();
        } catch (Exception | ConstantReassignmentException e) {
          handleException(e);
        }
      } while (true);
    }
  }
  
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

  public void loadNativeJava(Class c) throws IllegalAccessException, 
          IllegalArgumentException, InvocationTargetException {
    
    Collection<Method> methods = new ArrayList<>();
    for (Method method : c.getMethods()) {
      if (method.isAnnotationPresent(JavaHookin.class)) {
        methods.add(method);
      }
    }

    for (Method method : methods) {
      Object value = method.invoke(null);
      allocateNativeJavaHookin(method.getName(), (NativeJavaHookin) value);
    }
  }

  public void allocateNativeJavaHookin(String functionName, NativeJavaHookin nativeJavaHookin) {
    try {
      rowdyProgram.allocateIfExists(new Terminal("id", ID, functionName), new Value(nativeJavaHookin));
    } catch (ConstantReassignmentException ex) {
      handleException(ex);
    }
  }
  
  public void loadJarLibs(String pathToJar) throws IOException, 
          ClassNotFoundException, URISyntaxException, IllegalAccessException, 
          IllegalArgumentException, InvocationTargetException {
    
    try (JarFile jarFile = new JarFile(pathToJar)) {
      Enumeration<JarEntry> e = jarFile.entries();
      
      URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
      URLClassLoader cl = URLClassLoader.newInstance(urls);
      
      while (e.hasMoreElements()) {
        JarEntry je = e.nextElement();
        if (je.isDirectory() || !je.getName().endsWith(".class")) {
          continue;
        }
        // -6 because of .class
        String className = je.getName().substring(0, je.getName().length() - 6);
        className = className.replace('/', '.');
        Class c = cl.loadClass(className);
        loadNativeJava(c);
      }
    }
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Rowdy rowdy = new Rowdy(args);
    rowdy.run();
  }

  public void handleException(Throwable e) {
    System.out.println(e.getClass().getCanonicalName() + ": " + e.getLocalizedMessage());
    rowdyProgram.dumpCallStack();
    if (verbose) {
      e.printStackTrace();
    }
  }
  
}
