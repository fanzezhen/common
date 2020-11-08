package com.github.fanzezhen.common.leaned;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author zezhen.fan
 */
public class Demo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        String originFilePath = "E:\\tmp\\test.xlsx";
        int rowNum = 512;
        int colNum = 512;
        double rateOfChange = 0.8;
        double antiRateOfChange = 1 - rateOfChange;
        boolean isAnti = rateOfChange < 0.5;
        double realRate = isAnti ? antiRateOfChange : rateOfChange;

        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(originFilePath));
        List<List<Object>> readAll = reader.read();

        List<Integer> allMatrixPointList = new ArrayList<>(rowNum * colNum);
        List<Integer> idxMatrixPointList = new ArrayList<>(rowNum * colNum);
        for (int i = 0; i < rowNum * colNum; i++) {
            idxMatrixPointList.add(i);
        }

        readAll.forEach(list -> list.forEach(i -> allMatrixPointList.add(Integer.valueOf(String.valueOf(i)))));


        Random rand = new Random();

        for (int i = 0, count = rowNum * colNum; i < rowNum * colNum * realRate; i++, count--) {
            int idx = rand.nextInt(count);
            idxMatrixPointList.remove(idx);
        }

        idxMatrixPointList.forEach(idx -> allMatrixPointList.set(idx, allMatrixPointList.get(idx).equals(0) ? 1 : 0));

        int len = allMatrixPointList.size();
        System.out.println(len);
        List<List<Integer>> writeAll = new ArrayList<>(rowNum);
        for (int i = 0; i < 512; i++) {
            List<Integer> row = new ArrayList<>(512);
            for (int colIdx = 0; colIdx < 512; colIdx++) {
                int idx = i * 512 + colIdx;
                row.add(allMatrixPointList.get(idx));
            }
            writeAll.add(row);
        }

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("E:\\tmp\\writeTest.xlsx");
        //通过构造方法创建writer
        //ExcelWriter writer = new ExcelWriter("d:/writeTest.xls");
        //一次性写出内容，强制输出标题
        writer.write(writeAll);
        //关闭writer，释放内存
        writer.close();
    }
}
