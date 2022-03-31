package shetj.me.base.test;


import me.shetj.base.ktx.DataExtKt;

class TestJava {


    public static void main(String[] args) {
        System.out.printf("测试kt方法"+DataExtKt.getRandomString(3)+"\n");
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
























