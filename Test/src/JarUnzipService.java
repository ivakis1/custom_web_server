import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUnzipService {

    private void deleteFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            }

            file.delete();
        }
    }

    public void unzipJar(File jarFile) throws IOException {
        String cannonicalPath = jarFile.getCanonicalPath();

        JarFile fileAsJar = new JarFile(cannonicalPath);

        Enumeration<JarEntry> jarEntries = fileAsJar.entries();

        File jarFolder = new File(cannonicalPath.replace(".jar", ""));

        System.out.println(jarFolder.exists());
        System.out.println(jarFolder.isDirectory());

        if (jarFolder.exists() && jarFolder.isDirectory()) {
            deleteFolder(jarFolder);
        }

        jarFolder.mkdir();

        while (jarEntries.hasMoreElements()) {
            JarEntry currentEntry = jarEntries.nextElement();

            String currentEntryCannonicalPath =  jarFolder.getCanonicalPath() + File.separator + currentEntry.getRealName();

            File currentEntryAsFile = new File(currentEntryCannonicalPath);

            System.out.println(currentEntryAsFile.isDirectory());

            if (currentEntryAsFile.isDirectory()) {
                currentEntryAsFile.mkdir();
                continue;
            }

            InputStream currentEntryInputStream = fileAsJar.getInputStream(currentEntry);
            OutputStream currentEntryOutputStream = new FileOutputStream(currentEntryAsFile.getCanonicalPath());

            while (currentEntryInputStream.available() > 0) {
                currentEntryOutputStream.write(currentEntryInputStream.read());
            }

            currentEntryInputStream.close();
            currentEntryOutputStream.close();
        }
        fileAsJar.close();
    }
}
