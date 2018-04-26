package com.sx.quality.activity;


import android.graphics.Paint;
import android.graphics.Rect;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        /*Rect rect = new Rect();
        pFont.setTextSize(16);
        pFont.getTextBounds("豆", 0, 1, rect);
        int oneSizeWidth = rect.width();
        int oneSizeHeight = rect.height();*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                Paint pFont = new Paint();
                System.out.print(computeMaxStringWidth(new String[] {"国" , "3"}, pFont) + "-----------------");
            }
        }).start();

    }

    private int computeMaxStringWidth(String[] strings, Paint p) {
        float maxWidthF = 0.0f;
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        return maxWidth;
    }
}