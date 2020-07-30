package com.github.fanzezhen.common.core.util;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SysUtil {
    private static final int CPU_TIME = 500;

    private static final int PERCENT = 100;

    private static final int FAULT_LENGTH = 10;

    //获取内存使用率
    public static String getMemoryMsg() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 总的物理内存+虚拟内存
        long totalSwapSpaceSize = operatingSystemMXBean.getTotalSwapSpaceSize();
        // 剩余的物理内存
        long freePhysicalMemorySize = operatingSystemMXBean.getFreePhysicalMemorySize();
        double compare = (1 - freePhysicalMemorySize * 1.0 / totalSwapSpaceSize) * 100;
        return "内存已使用:" + (int) compare + "%";
    }

    public static String getDiskRatingString() {
        double usableSpace = 0;
        double totalSpace = 0;
        File[] disks = File.listRoots();
        for (File file : disks) {
            usableSpace += file.getUsableSpace();
            totalSpace += file.getTotalSpace();
        }

        return new DecimalFormat("0.00").format(usableSpace / totalSpace * 100) + "%";
    }

    public static float getDiskRating() {
        float usableSpace = 0;
        float totalSpace = 0;
        File[] disks = File.listRoots();
        for (File file : disks) {
            usableSpace += file.getUsableSpace();
            totalSpace += file.getTotalSpace();
        }

        return usableSpace / totalSpace;
    }

    //获取文件系统使用率
    public static List<String> getDiskMsgList() {
        // 操作系统
        List<String> list = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            String dirName = c + ":/";
            File win = new File(dirName);
            if (win.exists()) {
                long total = win.getTotalSpace();
                long free = win.getFreeSpace();
                double compare = (1 - free * 1.0 / total) * 100;
                String str = c + ":盘  已使用 " + (int) compare + "%";
                list.add(str);
            }
        }
        return list;
    }

    //获得cpu使用率
    public static String getCpuRatioForWindowsMsg() {
        try {
            String procCmd = System.getenv("windir") +
                    "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount," +
                    "ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPU_TIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idleTime = c1[0] - c0[0];
                long busyTime = c1[1] - c0[1];
                return "CPU使用率:" + Double.valueOf(PERCENT * (busyTime) * 1.0 / (busyTime + idleTime)).intValue() + "%";
            } else {
                return "CPU使用率:" + 0 + "%";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "CPU使用率:" + 0 + "%";
        }
    }

    //读取cpu相关信息
    private static long[] readCpu(final Process proc) {
        long[] resultArr = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULT_LENGTH) {
                return null;
            }
            int capIdx = line.indexOf("Caption");
            int cmdIdx = line.indexOf("CommandLine");
            int rocIdx = line.indexOf("ReadOperationCount");
            int umtIdx = line.indexOf("UserModeTime");
            int kmtIdx = line.indexOf("KernelModeTime");
            int wocIdx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocIdx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capIdx, cmdIdx - 1).trim();
                String cmd = substring(line, cmdIdx, kmtIdx - 1).trim();
                if (cmd.contains("wmic.exe")) {
                    continue;
                }
                String s1 = substring(line, kmtIdx, rocIdx - 1).trim();
                String s2 = substring(line, umtIdx, wocIdx - 1).trim();
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    if (s1.length() > 0)
                        idletime += Long.parseLong(s1);
                    if (s2.length() > 0)
                        idletime += Long.parseLong(s2);
                    continue;
                }
                if (s1.length() > 0)
                    kneltime += Long.parseLong(s1);
                if (s2.length() > 0)
                    usertime += Long.parseLong(s2);
            }
            resultArr[0] = idletime;
            resultArr[1] = kneltime + usertime;
            return resultArr;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
     *
     * @param src       要截取的字符串
     * @param start_idx 开始坐标（包括该坐标)
     * @param end_idx   截止坐标（包括该坐标）
     * @return tgt.toString()
     */
    private static String substring(String src, int start_idx, int end_idx) {
        byte[] b = src.getBytes();
        StringBuilder tgt = new StringBuilder();
        for (int i = start_idx; i <= end_idx; i++) {
            tgt.append((char) b[i]);
        }
        return tgt.toString();
    }
}
