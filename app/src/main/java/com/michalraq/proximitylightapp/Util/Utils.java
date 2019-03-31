package com.michalraq.proximitylightapp.Util;

import android.graphics.Color;

import  com.michalraq.proximitylightapp.R;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class Utils {

   public static int getEstimoteColor(String colorName) {
        switch (colorName) {
            case "ice":
                return Color.rgb(109, 170, 199);

            case "blueberry":
                return Color.rgb(36, 24, 93);

            case "mint":
                return Color.rgb(155, 186, 160);

            default:
                return R.color.defaultContentBackground;
        }
    }
}
