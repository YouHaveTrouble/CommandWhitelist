package eu.endermite.commandwhitelist.api;

import java.util.List;
import java.util.Random;

public class RandomStuff {

    /**
     *
     * @param list List of strings to pick a random one from
     * @param single String that will be used as fallback
     * @return Randomized message
     */

    public static String getMessage(List<String> list, String single) {

        if (list == null || list.size() == 0) {
            return single;
        }

        Random random = new Random();
        int r = random.nextInt(list.size());
        return list.get(r);
    }

}
