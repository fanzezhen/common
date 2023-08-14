package com.github.fanzezhen.common.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 * @date 2023/8/7
 */
@Slf4j
@Ignore
public class Test {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>();
        nums.add(1);
        nums.add(2);
        nums.add(3);

        List<List<Integer>> subsets = getSubsets(nums);
        for (List<Integer> subset : subsets) {
            System.out.println(subset);
        }
    }

    public static List<List<Integer>> getSubsets(List<Integer> nums) {
        List<List<Integer>> subsets = new ArrayList<>();
        generateSubsets(nums, 0, new ArrayList<>(), subsets);
        return subsets;
    }

    public static void generateSubsets(List<Integer> nums, int index, List<Integer> current, List<List<Integer>> subsets) {
        subsets.add(new ArrayList<>(current));
        for (int i = index; i < nums.size(); i++) {
            current.add(nums.get(i));
            generateSubsets(nums, i + 1, current, subsets);
            current.remove(current.size() - 1);
        }
    }
}
