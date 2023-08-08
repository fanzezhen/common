package com.github.fanzezhen.common.core;

import com.github.fanzezhen.common.core.util.SortUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
@Ignore
public class SortUtilTest {
    int[] arr = new int[]{3, 1, 7, 4, 5, 2, 6};
    int[] arr2 = new int[]{3, 1, 7, 4, 5, 3, 6, 2};

    @Test
    @Ignore
    public void testBubbleSort() {
        SortUtil.bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    @Test
    @Ignore
    public void testQuicksort() {
        SortUtil.quicksort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
        SortUtil.quicksort(arr2, 0, arr2.length - 1);
        System.out.println(Arrays.toString(arr2));
    }
}
