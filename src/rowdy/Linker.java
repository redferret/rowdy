
package rowdy;

import growdy.GRowdy;
import growdy.Node;
import growdy.Terminal;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import rowdy.exceptions.ConstantReassignmentException;
import static rowdy.lang.RowdyGrammarConstants.CONSTANT;
import static rowdy.lang.RowdyGrammarConstants.IMPORTS;
import static rowdy.lang.RowdyGrammarConstants.SINGLE_IMPORT;

/**
 *
 * @author Richard
 */
public class Linker {
  
  private final RowdyInstance rowdyInstance;
  private final GRowdy growdy;
  private final List<String> loadedImports;
  
  public Linker(GRowdy growdy, RowdyInstance rowdyInstance) {
    this.rowdyInstance = rowdyInstance;
    this.growdy = growdy;
    loadedImports = new ArrayList<>();
  }
  
  public void loadImport(String importPath) {
    try {
      growdy.buildFromSource("bin/" + importPath);
      BaseNode compiledImport = (BaseNode) growdy.getProgram();
      List<BaseNode> programTrees = new ArrayList<>();
      loadImports((BaseNode) growdy.getProgram(), programTrees);
      programTrees.forEach(tree -> {
        try {
          rowdyInstance.optimizeProgram(tree);
          rowdyInstance.declareGlobals(tree);
        } catch (ConstantReassignmentException ex) {
          rowdyInstance.handleException(ex, false);
        }
      });
      rowdyInstance.optimizeProgram(compiledImport);
      rowdyInstance.declareGlobals(compiledImport);
    } catch (Throwable ex) {
      rowdyInstance.handleException(ex, false);
    }
  }
  
  public void loadImports(BaseNode program, List<BaseNode> compiledImports) {
    List<String> localImports = new ArrayList<>();
    BaseNode imports = program.get(IMPORTS, false);
    getImports(imports, localImports);
    localImports.forEach(localImport -> {
      String importPath = "bin/" + localImport;
      try {
        if (!loadedImports.contains(importPath)) {
            loadedImports.add(importPath);
            growdy.buildFromSource(importPath);
            BaseNode compliedImport = (BaseNode) growdy.getProgram();
            compiledImports.add(compliedImport);
            loadImports(compliedImport, compiledImports);
        }
      } catch (Throwable ex) {
        rowdyInstance.handleException(ex, false);
      }
    });
  }
  
  public void getImports(BaseNode root, List<String> importPaths) {
    ArrayList<BaseNode> children = root.getAll();
    int currentID;
    for (int i = 0; i < children.size(); i++) {
      BaseNode importTreeNodes = children.get(i);
      currentID = importTreeNodes.symbol().id();
      switch (currentID) {
        case SINGLE_IMPORT:
          if (importTreeNodes != null && importTreeNodes.hasSymbols()) {
            Node importConstant = importTreeNodes.get(CONSTANT, false);
            if (importConstant != null) {
              String importPath = ((Terminal) importConstant.symbol()).getValue().replaceAll("\\.", "/").replaceAll("\"", "");
              importPaths.add(importPath);
            }
          }
          break;
        case IMPORTS:
          getImports(importTreeNodes, importPaths);
          break;
      }
    }
  }
  
  public void loadNativeJava(Class c) throws IllegalAccessException, 
          IllegalArgumentException, InvocationTargetException {
    for (Method method : c.getMethods()) {
      if (method.isAnnotationPresent(NativeJava.class)) {
        NativeJavaCode nativeCode = (NativeJavaCode) method.invoke(null);
        rowdyInstance.allocateIfExists(method.getName(), new Value(nativeCode, true));
      }
    }
  }
  
  /**
   * Pulls in all the Jars found in the given path
   * @param path
   * @return 
   */
  public List<String> getJarFileNames(String path) {
    File[] files = new File(path).listFiles((File dir, String name) -> name.endsWith(".jar"));
    List<String> fileNames = new ArrayList<>();
    for (File file : files) {
      fileNames.add(file.getName());
    }
    return fileNames;
  }

  /**
   * Load in all the JAR libs for Native Java code
   * @param pathToJars
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws URISyntaxException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException 
   */
  public void loadJarLibs(String pathToJars) throws IOException, 
          ClassNotFoundException, URISyntaxException, IllegalAccessException, 
          IllegalArgumentException, InvocationTargetException {
    
    List<String> jarFileNames = getJarFileNames(pathToJars);
    
    for (String jarName : jarFileNames ) {
      String pathToJar = pathToJars + jarName;
      try (JarFile jarFile = new JarFile(pathToJar)) {
        Enumeration<JarEntry> enumerations = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (enumerations.hasMoreElements()) {
          JarEntry je = enumerations.nextElement();
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
}
