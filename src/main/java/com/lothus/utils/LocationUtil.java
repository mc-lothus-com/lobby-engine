package com.lothus.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static String getData(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location getLocation(String args) {
        World world = Bukkit.getWorld(args.split(",")[0]);
        double x = Double.parseDouble(args.split(",")[1]);
        double y = Double.parseDouble(args.split(",")[2]);
        double z = Double.parseDouble(args.split(",")[3]);
        float yaw = Float.parseFloat(args.split(",")[4]);
        float pitch = Float.parseFloat(args.split(",")[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
