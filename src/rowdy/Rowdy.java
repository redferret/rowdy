package rowdy;


import growdy.GRBuilder;
import growdy.GRowdy;
import growdy.Node;
import growdy.Terminal;
import growdy.exceptions.AmbiguousGrammarException;
import growdy.exceptions.ParseException;
import growdy.exceptions.SyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import rowdy.nodes.RowdyNode;
import rowdy.nodes.RowdyNodeFactory;
import rowdy.exceptions.ConstantReassignmentException;
import rowdy.exceptions.MainNotFoundException;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;
import static rowdy.lang.RowdyGrammarConstants.IMPORTS;
import static rowdy.lang.RowdyGrammarConstants.STMT_LIST;

/**
 * Main driver class. The grammar, terminals/non-terminals and the hint table
 * are all defined here.
 * 
 * @author Richard DeSilvey
 */
public class Rowdy {
  public static final RowdyNodeFactory nodeFactory = new RowdyNodeFactory();
  private final RowdyInstance rowdyInstance;
  private final GRowdy growdy;
  private final String[] args;
  private String programFileName;
  private boolean verbose;
  
  public Rowdy(String[] args) {
    rowdyInstance = new RowdyInstance();
    this.args = args;
    GRBuilder grBuilder = getBuilder();
    RowdyNode.initRunner(rowdyInstance);
    growdy = GRowdy.getInstance(grBuilder, nodeFactory);
    programFileName = "";
    if (args.length > 1) {
      verbose = args[args.length - 1].equalsIgnoreCase("-verbose");
      programFileName = args[0];
    } else if (args.length == 1){
      verbose = args[args.length - 1].equalsIgnoreCase("-verbose");
      if (!verbose) {
        programFileName = args[0];
      }
    }
  }
  
  public void run() {
    if (!programFileName.isEmpty()) {
      List<BaseNode> programTrees = new ArrayList<>();
      try {
        growdy.buildFromSource(programFileName);
        rowdyInstance.initialize(growdy);
        rowdyInstance.declareGlobals();
        loadImports((BaseNode) growdy.getProgram(), programTrees);
        programTrees.forEach(tree -> {
          try {
            rowdyInstance.optimizeProgram(tree);
            rowdyInstance.declareGlobals(tree);
          } catch (ConstantReassignmentException ex) {
            handleException(ex);
          }
        });
        try {
          loadJarLibs("bin/");
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
            programParameters.add(new Value(Double.parseDouble(args[p]), false));
          } else {
            String argStr = args[p];
            if (argStr.equals("true") || argStr.equals("false")) {
              programParameters.add(new Value(Boolean.valueOf(argStr), false));
            } else {
              programParameters.add(new Value(args[p], false));
            }
          }
        }
        rowdyInstance.execute(programParameters);
      } catch (NumberFormatException | MainNotFoundException
              | ConstantReassignmentException e) {
        handleException(e);
      }
    } else {
      List<BaseNode> programTrees = new ArrayList<>();
      try {
        growdy.buildFromSource("bin/core/rowdy");
        programTrees.add((BaseNode) growdy.getProgram());
        loadImports((BaseNode) growdy.getProgram(), programTrees);
        programTrees.forEach(tree -> {
          try {
            rowdyInstance.optimizeProgram(tree);
            rowdyInstance.declareGlobals(tree);
          } catch (ConstantReassignmentException ex) {
            handleException(ex);
          }
        });
        loadJarLibs("bin/");
      } catch (Throwable ex) {}
      
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
          rowdyInstance.initialize(growdy);
          rowdyInstance.executeLine();
        } catch (ParseException | SyntaxException | AmbiguousGrammarException | 
                ConstantReassignmentException e) {
          handleException(e);
        }
      } while (true);
    }
  }
  
  public List<String> getImports(BaseNode root) {
    BaseNode currentTreeNode;
    ArrayList<BaseNode> children = root.getAll();
    int currentID;
    List<String> importPaths = new ArrayList<>();
    for (int i = 0; i < children.size(); i++) {
      currentTreeNode = children.get(i);
      currentID = currentTreeNode.symbol().id();
      switch (currentID) {
        case IMPORTS:
          Node imports = currentTreeNode.get(CONSTANT, false);
          if (imports != null) {
            String importPath = ((Terminal) imports.symbol()).getName().replaceAll("\\.", "/").replaceAll("\"", "");
            importPaths.add(importPath);
            Node nextImport = currentTreeNode.get(IMPORTS);
            while (nextImport.hasSymbols()) {
              imports = nextImport.get(CONSTANT);
              importPath = ((Terminal) imports.symbol()).getName().replaceAll("\\.", "/").replaceAll("\"", "");
              importPaths.add(importPath);
              nextImport = nextImport.get(IMPORTS);
            }
          }
      }
    }
    return importPaths;
  }
  
  public void loadImports(BaseNode program, List<BaseNode> programTrees) {
    List<String> localImports = getImports(program);
    localImports.forEach(imprt -> {
      String importPath = "bin/" + imprt;
      try {
        growdy.buildFromSource(importPath);
        BaseNode subProgram = (BaseNode) growdy.getProgram();
        programTrees.add(subProgram);
        loadImports(subProgram, programTrees);
      } catch (IOException | ParseException | SyntaxException | AmbiguousGrammarException ex) {
        handleException(ex);
      }
    });
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
    for (Method method : c.getMethods()) {
      if (method.isAnnotationPresent(JavaHookin.class)) {
        NativeJava hookin = (NativeJava) method.invoke(null);
        rowdyInstance.allocateIfExists(method.getName(), new Value(hookin, true));
      }
    }
  }
  
  public List<String> getJarFileNames(String path) {
    File[] files = new File(path)
            .listFiles((File dir, String name) -> name.endsWith(".jar"));
    List<String> fileNames = new ArrayList<>();
    for (File file : files) {
      fileNames.add(file.getName());
    }
    return fileNames;
  }

  public void loadJarLibs(String pathToJars) throws IOException, 
          ClassNotFoundException, URISyntaxException, IllegalAccessException, 
          IllegalArgumentException, InvocationTargetException {
    
    List<String> jarFileNames = getJarFileNames(pathToJars);
    
    for (String jarName : jarFileNames ) {
      String pathToJar = pathToJars + jarName;
      try (JarFile jarFile = new JarFile(pathToJar)) {
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
          JarEntry je = e.nextElement();
          if (je.isDirectory() || !je.getName().endsWith(".class")) {
            continue;
          }
          String className = je.getName().substring(0, je.getName().length() - 6);
          className = className.replace('/', '.');
          Class c = cl.loadClass(className);
          loadNativeJava(c);
        }
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
    rowdyInstance.dumpCallStack();
    if (verbose) {
      e.printStackTrace();
    }
  }
  
}
