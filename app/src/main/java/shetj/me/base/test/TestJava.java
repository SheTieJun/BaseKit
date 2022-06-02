package shetj.me.base.test;


import java.util.LinkedHashSet;

class TestJava {


    public static void main(String[] args) {
        long time1 = System.currentTimeMillis() ;
        String[] strings = new String[]{"1","2","3","4","5","6","7","8"};
        setof(strings);
        System.out.printf("耗时："+(System.currentTimeMillis() -time1));
    }

    private static LinkedHashSet  setof(String... strings)  {
        LinkedHashSet set = new LinkedHashSet<String>();
        for (int i=0;  i<strings.length;i++){
            set.add(strings[i]);
        }
        return set;
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
























