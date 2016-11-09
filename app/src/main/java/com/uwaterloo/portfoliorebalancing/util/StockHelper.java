package com.uwaterloo.portfoliorebalancing.util;

import com.uwaterloo.portfoliorebalancing.R;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by lucas on 15/10/16.
 */

public class StockHelper {
    private static int[] CIRCLE_COLORS = {R.color.circle_color1, R.color.circle_color2, R.color.circle_color3,
            R.color.circle_color4, R.color.circle_color5, R.color.circle_color6, R.color.circle_color7,
            R.color.circle_color8, R.color.circle_color9, R.color.circle_color10, R.color.circle_color11,
            R.color.circle_color12, R.color.circle_color13, R.color.circle_color14, R.color.circle_color15};

    private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

    public static int getColorResource(String value) {
        int counter = 0;
        for (int i = 0; i < value.length(); i++) {
            counter += value.charAt(i);
            counter %= CIRCLE_COLORS.length;
        }
        counter = Math.abs(counter);
        return CIRCLE_COLORS[counter];
    }

    public static String formatDouble(double x) {
        return "$" + decimalFormat.format(x);
    }

    public static String getFirstCharacter(String x) {
        if (x == null || x.isEmpty()) {
            return "";
        }
        return String.valueOf(Character.toUpperCase(x.charAt(0)));
    }

    public static double handleDouble(String x) {
        Double y;
        if (Pattern.matches("N/A", x)) {
            y = 0.00;
        } else {
            y = Double.parseDouble(x);
        }
        return y;
    }
    public static float handleFloat(String x) {
        Float y;
        if (Pattern.matches("N/A", x)) {
            y = 0.00f;
        } else {
            y = Float.parseFloat(x);
        }
        return y;
    }

    public static int handleInt(String x) {
        int y;
        if (Pattern.matches("N/A", x)) {
            y = 0;
        } else {
            y = Integer.parseInt(x);
        }
        return y;
    }

    public static String handleString(String x) {
        return x.replaceAll("\"", "");
    }
}
