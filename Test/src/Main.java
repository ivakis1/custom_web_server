import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JarUnzipService service = new JarUnzipService();

        File jarFileAsFile = new File("javache.jar");

        service.unzipJar(jarFileAsFile);
    }
}
