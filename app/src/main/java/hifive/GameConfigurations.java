package hifive;

import ch.aplu.jgamegrid.Location;

import java.util.Properties;

public class GameConfigurations {
    // Game constants (now static)
    public static final int SEED = 30008;
    public static final int FIVE_GOAL = 5;
    public static final int FIVE_POINTS = 100;
    public static final int SUM_FIVE_POINTS = 60;
    public static final int DIFFERENCE_FIVE_POINTS = 20;
    public static final int NB_PLAYERS = 4;
    public static final int NB_START_CARDS = 2;
    public static final int NB_FACE_UP_CARDS = 2;
    public static final int HAND_WIDTH = 400;
    public static final int TRICK_WIDTH = 40;
    public static final String VERSION = "1.0";

    // Locations for UI elements (now static)
    public static final Location[] HAND_LOCATIONS = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    public static final Location[] SCORE_LOCATIONS = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            new Location(575, 575)
    };
    public static final Location TRICK_LOCATION = new Location(350, 350);
    public static final Location TEXT_LOCATION = new Location(350, 450);

    // Configurable properties (instance variables)
    public final boolean isAuto;
    public final int thinkingTime;
    public final int delayTime;
    public final String[] playerTypes;
    public final Properties properties;

    GameConfigurations(Properties properties) {
        this.properties = properties;
        this.isAuto = Boolean.parseBoolean(properties.getProperty("isAuto", "false"));
        this.thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        this.delayTime = Integer.parseInt(properties.getProperty("delayTime", "60"));
        this.playerTypes = properties.getProperty("playerTypes", "human,random,basic,clever").split(",");
    }
}
