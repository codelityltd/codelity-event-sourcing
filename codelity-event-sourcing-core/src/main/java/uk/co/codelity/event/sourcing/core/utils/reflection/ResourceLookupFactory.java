package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static java.util.Objects.requireNonNull;

class ResourceLookupFactory {
    public static final String JAR = "jar";
    public static final String FILE = "file";

    static Logger logger = LoggerFactory.getLogger(ResourceLookupFactory.class);

    private ResourceLookupFactory() {
    }

    public static ResourceLookup create(URL url, String packageName) {
        requireNonNull(url);
        requireNonNull(packageName);

        switch (url.getProtocol()) {
            case JAR:
                return new JarResourceLookup(url);
            case FILE:
                return new FileResourceLookup(url, packageName);
            default:
                logger.warn("Unsupported protocol (protocol: {} url: {})", url.getProtocol(), url.getPath());
                return null;
        }
    }
}
