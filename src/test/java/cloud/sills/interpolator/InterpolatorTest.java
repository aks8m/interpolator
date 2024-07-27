package cloud.sills.interpolator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.Collections;
import java.util.List;

public class InterpolatorTest {

    final static String QUALIFIED_CLASS_NAME = "cloud.sills.interpolator.TestClass";

    private String sourceCode =
                    """
                        package cloud.sills.interpolator;
                        
                        public class TestClass implements InMemoryClass{
                            
                            public void runCode() {
                                System.out.println("code is running...");
                            }
                        }        
                    """;

    @Test
    public void whenStringIsCompiled_ThenCodeShouldExecute() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

        List<JavaFileObject> sourceFiles = Collections.singletonList(new JavaSourceFromString(QUALIFIED_CLASS_NAME, sourceCode));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

        boolean result = task.call();

        if (!result) {
            diagnostics.getDiagnostics().forEach(System.out::println);
        }

        ClassLoader classLoader = manager.getClassLoader(null);
        classLoader.loadClass(InMemoryClass.class.getName());
        Class<?> clazz = classLoader.loadClass(QUALIFIED_CLASS_NAME);

        InMemoryClass instanceOfClass = (InMemoryClass) clazz.newInstance();

        Assertions.assertInstanceOf(InMemoryClass.class, instanceOfClass);

        instanceOfClass.runCode();

    }

}
