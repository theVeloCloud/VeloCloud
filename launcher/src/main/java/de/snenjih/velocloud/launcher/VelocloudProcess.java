package de.snenjih.velocloud.launcher;

import de.snenjih.velocloud.launcher.dependencies.DependencyProvider;
import de.snenjih.velocloud.launcher.lib.VelocloudLib;
import de.snenjih.velocloud.launcher.lib.VelocloudLibNotFoundException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class VelocloudProcess extends Thread {

    private final List<VelocloudLib> processLibs;
    private Process process;

    public VelocloudProcess() {
        this.processLibs = VelocloudLib.of(VelocloudParameters.REQUIRED_LIBS);
        this.processLibs.forEach(VelocloudLib::copyFromClasspath);
    }

    @Override
    public void run() {
        var dependencyProvider = new DependencyProvider();
        dependencyProvider.download();

        var processBuilder = new ProcessBuilder()
                .inheritIO()
                .command(arguments(dependencyProvider));

        // copy all environment variables from the current process
        String version = System.getProperty(VelocloudParameters.VERSION_ENV_ID);
        if (version != null) {
            processBuilder.environment().put(VelocloudParameters.VERSION_ENV_ID, version);
        }

        try {
            process = processBuilder.start();
            process.waitFor();
            process.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    private List<String> arguments(DependencyProvider dependencyProvider) {
        var arguments = new ArrayList<String>();
        var usedJava = System.getenv("java.home");

        var bootLib = processLibs.stream()
                .filter(lib -> VelocloudParameters.BOOT_LIB.equals(lib.name()))
                .findFirst()
                .orElseThrow(() -> new VelocloudLibNotFoundException(VelocloudParameters.BOOT_LIB));

        arguments.add(usedJava != null ? usedJava + "/bin/java" : "java");

        arguments.add("-cp");
        var classpathSeparator = windowsProcess() ? ";" : ":";
        var libClasspath = processLibs
                .stream().map(it -> it.target().toString())
                .collect(Collectors.joining(classpathSeparator));

        var dependencyClasspath = dependencyProvider.dependencies()
                .stream()
                .map(it -> VelocloudParameters.DEPENDENCY_DIRECTORY.resolve(it.file()).toString())
                .collect(Collectors.joining(classpathSeparator));

        arguments.add(libClasspath + classpathSeparator + dependencyClasspath);
        arguments.add(bootLib.mainClass());

        return arguments;
    }

    private boolean windowsProcess() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("win");
    }

    public void shutdown() {
        this.process.destroy();
    }
}
