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

    private String sourceCode =
                    """
                        package cloud.sills;
                        
                        public class TestClass implements InMemoryClass {
                            
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

        List<JavaFileObject> sourceFiles = Collections.singletonList(new JavaSourceFromString("TestClass", sourceCode));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

        boolean result = task.call();

        if (!result) {
            diagnostics.getDiagnostics().forEach(diagnostic -> System.out.println(String.valueOf(diagnostic) ));
        }

        ClassLoader classLoader = manager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass("TestClass");
        InMemoryClass instanceOfClass = (InMemoryClass) clazz.newInstance();

        Assertions.assertInstanceOf(InMemoryClass.class, instanceOfClass);

        instanceOfClass.runCode();

    }

}
