package com.github.fanzezhen.common.leaned;

import com.alibaba.fastjson.JSON;

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
                if (i4 < 1 || arr4[i4 - 1] != arr2[i2])  // if避免arr4中出现重复
                    arr4[i4++] = arr2[i2];
            }
            if (i2 >= arr2.length) arr4[i4++] = arr1[i1];
        }
        System.out.println(JSON.toJSON(arr1));
        System.out.println(JSON.toJSON(arr2));
        System.out.println(JSON.toJSON(arr3));
        System.out.println(JSON.toJSON(arr4));
    }
}
