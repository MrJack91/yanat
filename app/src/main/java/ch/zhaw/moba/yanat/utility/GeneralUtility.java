package ch.zhaw.moba.yanat.utility;

/**
 * Created by michael on 21.03.16.
 */
public class GeneralUtility {

    public static String formatHeight(float value) {

        return String.format("%.2f", (double) value);

        /*
        DecimalFormat myFormatter = new DecimalFormat("#.###");
        return myFormatter.format(value);
        */
    }


}
