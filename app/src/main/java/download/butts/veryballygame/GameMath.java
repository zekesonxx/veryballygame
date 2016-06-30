package download.butts.veryballygame;

/**
 * Created by student on 2016-06-28.
 */
public class GameMath {

    public static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }
}
