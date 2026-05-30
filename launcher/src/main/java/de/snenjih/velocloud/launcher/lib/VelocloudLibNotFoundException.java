package de.snenjih.velocloud.launcher.lib;

public class VelocloudLibNotFoundException extends RuntimeException {

    public VelocloudLibNotFoundException(String lib) {
        super("Lib not found: " + lib);

    }
}
