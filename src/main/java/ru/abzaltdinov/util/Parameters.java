package ru.abzaltdinov.util;

public class Parameters {
    public static final String PATH_PROJECT = System.getProperty("user.dir");

    public static final String PATH_LKH = PATH_PROJECT + "/lib/LKH-2.0.9/LKH";

    public static final String PATH_RESOURCES_RELATIVE = "src/main/resources";
    public static final String PATH_RESOURCES = PATH_PROJECT + "/" + PATH_RESOURCES_RELATIVE;

    public static final String PATH_RESOURCES_LKH = PATH_RESOURCES + "/LKH";
    public static final String PATH_LKH_PARAMS = PATH_RESOURCES_LKH + "/LKH.properties";

    public static final String PATH_RESOURCES_TSP = PATH_RESOURCES + "/TSP";
    public static final String PATH_TSP_TOURS_RELATIVE = PATH_RESOURCES_RELATIVE + "/TSP/tours";
    public static final String PATH_TSP_TOURS_ABSOLUTE = PATH_PROJECT + "/" + PATH_TSP_TOURS_RELATIVE;

}
