package com.github.fanzezhen.common.core;

import com.github.fanzezhen.common.core.util.SearchUtil;
import com.github.fanzezhen.common.core.util.SortUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
@Ignore
public class SearchUtilTest {
    int[] arr = new int[]{3, 1, 7, 4, 5, 2, 6};
    int[] arr2 = new int[]{3, 1, 7, 4, 5, 3, 6, 2};

    @Test
    @Ignore
    public void testBinarySearch() {
        SortUtil.quicksort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
        System.out.println(SearchUtil.binarySearch(arr, 5));
        SortUtil.quicksort(arr2, 0, arr2.length - 1);
        System.out.println(Arrays.toString(arr2));
        System.out.println(SearchUtil.binarySearch(arr2, 5));
    }
}
