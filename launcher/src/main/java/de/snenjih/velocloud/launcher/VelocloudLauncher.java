package de.snenjih.velocloud.launcher;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class VelocloudLauncher {

    public static void main(String[] args) throws URISyntaxException, IOException {
        var ownPath = Paths.get(VelocloudProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        // we need to load the current version from the manifest data
        String version = VelocloudParameters.readManifest(VelocloudParameters.VERSION_ENV_ID, ownPath);
        if (version != null) {
            System.setProperty(VelocloudParameters.VERSION_ENV_ID, version);
        }

        Files.createDirectories(VelocloudParameters.LIB_DIRECTORY);

        var process = new VelocloudProcess();

        Runtime.getRuntime().addShutdownHook(new Thread(process::shutdown));

        // start the main context of the velocloud agent
        process.start();
    }
}
