package de.snenjih.velocloud.launcher.lib;

import de.snenjih.velocloud.launcher.VelocloudParameters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

public class VelocloudLib {

    private final String name;
    private final Path path;

    public VelocloudLib(String name) {
        this.name = name;
        this.path = Paths.get(String.format("velocloud-%s-%s.jar", name, VelocloudParameters.velocloudVersion()));
    }

    public static List<VelocloudLib> of(String... names) {
        return of(null, names);
    }

    /**
     * Creates a list of VelocloudLib instances from the provided names.
     */
    public static List<VelocloudLib> of(String suffix, String... names) {
        List<VelocloudLib> libs = new ArrayList<>();
        for (String name : names) {
            libs.add(new VelocloudLib(name + (suffix != null ? "-" + suffix : "")));
        }
        return libs;
    }

    /**
     * Returns the path of the target file in the lib directory.
     */
    public Path target() {
        return VelocloudParameters.LIB_DIRECTORY.resolve(path);
    }

    /**
     * Reads the manifest of the Velocloud library and returns the value for the specified key.
     */
    public String mainClass() {
        return VelocloudParameters.readManifest("Main-Class", target());
    }

    /**
     * Copies the Velocloud library from the classpath to the target directory.
     */
    public void copyFromClasspath() {
        try (InputStream in = Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResourceAsStream(path.toString()))) {
            Files.copy(in, target(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to copy velocloud library from classpath: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String name() {
        return name;
    }
}
