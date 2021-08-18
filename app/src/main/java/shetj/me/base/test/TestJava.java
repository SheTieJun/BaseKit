package shetj.me.base.test;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.stream.Stream;

import me.shetj.base.ktx.StringExtKt;
import me.shetj.base.tools.time.DateUtils;

class TestJava {


    public static void main(String[] args) {
        int[] data = new int[]{4,5,6,1,3,2};
        insertionSort(data,6);
        for (int i = 0; i < data.length; i++) {
            System.out.printf(data[i]+"");
        }
    }



    public static void insertionSort(int[] a, int n) {
        if (n <= 1) return;

        for (int i = 1; i < n; ++i) {
            int value = a[i];
            int j = i - 1;
            for (; j >= 0; --j) {
                if (a[j] > value) {
                    a[j + 1] = a[j];
                } else {
                    break;
                }
            }
            a[j + 1] = value; // 插入数据
        }
    }
}
























