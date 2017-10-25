package com.sx.quality.activity;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String level_1 = "工序描述：" + "桥梁工程→来毛沟中桥→基础及下步→桩基→0-0桩基→装维测量放羊抽检→桩基→0-0桩基→装维测量放羊抽检";
        int len = level_1.length();

        // 计算每行应该有多少内容
        List<String> list = new ArrayList<>();
        int lenSize = 17*3; // 水印图上每行显示多少个字
        for (int i = 0; i < len; i++) {
            int byteLen = level_1.substring(0, i).getBytes().length;
            if (list.size() > 0) {
                byteLen-=15;
            }

            if (byteLen % lenSize == 0 || (byteLen + 1) % lenSize == 0 || (byteLen + 2) % lenSize == 0) {
                if (i != 0 && (byteLen / lenSize == 1 || (byteLen + 1) / lenSize == 1) || (byteLen + 2) / lenSize == 1) {
                    list.add(level_1.substring(0, i));
                    lenSize = 12*3;
                } else if (i != 0 && (byteLen / lenSize > 1 || (byteLen + 1) / lenSize > 1) || (byteLen + 2) / lenSize > 1) {
                    int otherLen = 0;
                    for (int j = 0; j < list.size(); j++) {
                        otherLen+=list.get(j).length();
                    }
                    list.add(level_1.substring(otherLen, i));
                }
            }
        }

        // 判断是否还有一行
        int nowLen = 0;
        for (String s : list) {
            nowLen+=s.length();
        }

        if (nowLen < len) {
            list.add(level_1.substring(nowLen));
        }

        for (String sList : list) {
            System.out.println(sList);
        }
    }
}