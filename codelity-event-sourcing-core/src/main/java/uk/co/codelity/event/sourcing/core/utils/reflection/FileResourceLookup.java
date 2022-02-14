package uk.co.codelity.event.sourcing.core.utils.reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

class FileResourceLookup implements ResourceLookup {
    static Logger logger = LoggerFactory.getLogger(FileResourceLookup.class);

    private final URL url;
    private final String packageName;

    public FileResourceLookup(URL url, String packageName) {
        this.url = url;
        this.packageName = packageName;
    }

    @Override
    public Set<Class<?>> getClasses() throws URISyntaxException {
        requireNonNull(url);
        requireNonNull(packageName);

        File directory = new File(url.toURI());
        if (directory.isFile()) {
             return Collections.emptySet();
        }

        Set<Class<?>> result = new HashSet<>();
        getClasses(directory, packageName, result);
        return result;
    }

    private static void getClasses(File directory, String pkgName, Set<Class<?>> fileNames) {
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                fileNames.add(getClass(file.getName(), pkgName));
            } else {
                getClasses(file, pkgName + "." + file.getName(), fileNames);
            }
        }
    }

    private static Class<?> getClass(String className, String pkgName) {
        try {
            String fqn = pkgName + "."
                    + className.substring(0, className.lastIndexOf('.'));

            return Class.forName(fqn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
