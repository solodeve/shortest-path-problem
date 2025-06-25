package common;

/**
 * Utility class for converting between time strings ("HH:mm:ss") and float values representing minutes.
 */
public class Common {
    /**
     * Converts a time string in the format "HH:mm:ss" to a float representing the total number of minutes.
     * Example: "02:10:30" → 130.5
     * @param horaire the time string in "HH:mm:ss" format
     * @return the time in minutes as a float, or -1f if the input is null
     */
    public static float HoraireToFloat(String horaire) {
        if (horaire != null) {
            String[] parts = horaire.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]) + Integer.parseInt(parts[2]) / 60f;
        } return -1f;
    }

    /**
     * Converts a time float in the format float to a "HH:mm:ss".
     * Example: "02:10:30" → 130.5
     * @param time the time string in float format
     * @return the time in "HH:mm:ss" format
     */
    public static String floatToHoraire(float time) {
        int hours = (int) (time / 60);
        int minutes = (int) (time % 60);
        int seconds = (int) ((time - hours * 60 - minutes) * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
