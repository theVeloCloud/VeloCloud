package de.snenjih.velocloud.launcher.dependencies;

import de.snenjih.velocloud.launcher.VelocloudParameters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

public record Dependency (
        String group,
        String name,
        String version,
        String file,
        String url,
        String sha256 
) {

    public void download() {
        var targetFile = VelocloudParameters.DEPENDENCY_DIRECTORY.resolve(file);

        if(Files.exists(targetFile)) {
            return;
        }

        System.out.println("Downloading dependency: " + name + " version: " + version);

        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("Url is null or empty");
        }

        try (var in = new URI(url).toURL().openStream()) {
            Files.copy(in, targetFile);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
