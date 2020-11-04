package com.github.fanzezhen.common.leaned;

import com.alibaba.fastjson.JSON;

/**
 * @author zezhen.fan
 */
public class Question {
    /**
     * 计算交集、并集并排序
     */
    public static void intersectionUnionSort() {
        int[] arr1 = {1, 3, 5, 2, 8};
        int[] arr2 = {2, 4, 6, 3, 5};
        int[] arr3 = new int[arr1.length + arr2.length];
        int[] arr4 = new int[arr1.length + arr2.length];

        Algorithm.quickSort(arr1, 0, arr1.length - 1);
        Algorithm.quickSort(arr2, 0, arr2.length - 1);

        int i1, i2 = 0, i3 = 0, i4 = 0;
        for (i1 = 0; i1 < arr1.length; i1++) {
            for (; i2 < arr2.length; i2++) {
                if (arr1[i1] < arr2[i2]) {
                    arr4[i4++] = arr1[i1];
                    break;
                }
                if (arr1[i1] == arr2[i2]) {
                    arr3[i3++] = arr1[i1];
                    arr4[i4++] = arr1[i1];
                    break;
                }
                // if避免arr4中出现重复
                if (i4 < 1 || arr4[i4 - 1] != arr2[i2])
                {
                    arr4[i4++] = arr2[i2];
                }
            }
            if (i2 >= arr2.length) {
                arr4[i4++] = arr1[i1];
            }
        }
        System.out.println(JSON.toJSON(arr1));
        System.out.println(JSON.toJSON(arr2));
        System.out.println(JSON.toJSON(arr3));
        System.out.println(JSON.toJSON(arr4));
        System.out.println("1");
        System.out.println("2");
        System.out.println("3");
        System.out.println("4");
        System.out.println("5");
        System.out.println("6");
        System.out.println("7");
        System.out.println("8");
        System.out.println("9");
        System.out.println("10");
        System.out.println("11");
        System.out.println("12");
        System.out.println("13");
        System.out.println("14");
        System.out.println("15");
        System.out.println("16");
        System.out.println("17");
        System.out.println("18");
        System.out.println("19");
        System.out.println("20");
        System.out.println("21");
        System.out.println("22");
        System.out.println("23");
        System.out.println("24");
        System.out.println("25");
        System.out.println("26");
        System.out.println("27");
        System.out.println("28");
        System.out.println("29");
        System.out.println("30");
        System.out.println("31");
        System.out.println("32");
        System.out.println("33");
        System.out.println("34");
    }
}
