package com.github.fanzezhen.common.core.util;

import com.github.fanzezhen.common.core.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ExtraDataRecord;
import net.lingala.zip4j.model.FileHeader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Slf4j
public class ZipUtil {
    private static final int BUFF_SIZE = 4096;

    /**
     * 获取ZIP文件中的文件名和目录名
     */
    public static List<String> getEntryNameList(String zipFilePath, String password) {
        List<String> entryList = new ArrayList<>();
        ZipFile zf;
        try {
            zf = new ZipFile(zipFilePath);
            // 默认UTF8，如果压缩包中的文件名是GBK会出现乱码
            zf.setCharset(Charset.forName("GBK"));
            if (zf.isEncrypted()) {
                zf.setPassword(password.toCharArray());
            }
            zf.getFileHeaders().forEach(v -> {
                String fileName = getFileNameFromExtraData(v);//文件名会带上层级目录信息
                entryList.add(fileName);
            });
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return entryList;
    }

    /**
     * 将ZIP包中的文件解压到指定目录
     */
    public static void extract(String zipFilePath, String password, String destDir) {
        InputStream is = null;
        OutputStream os = null;
        ZipFile zf;
        try {
            zf = new ZipFile(zipFilePath);
            // 默认UTF8，如果压缩包中的文件名是GBK会出现乱码，但不会影响解压文件内容
            zf.setCharset(Charset.forName("GBK"));
            if (zf.isEncrypted()) {
                zf.setPassword(password.toCharArray());
            }
            String s = "\\";
            if (!destDir.endsWith(File.separator) && !destDir.endsWith(s)) {
                destDir += File.separator;
            }
            for (Object obj : zf.getFileHeaders()) {
                FileHeader fileHeader = (FileHeader) obj;
                String pathname = destDir + getFileNameFromExtraData(fileHeader);
                File destFile = new File(pathname);
                if (fileHeader.isDirectory()) {
                    if (!destFile.exists()) {
                        log.info("{}创建目录：{}", destFile.mkdirs(), pathname);
                    }
                    continue;
                }
                if (!destFile.getParentFile().exists()) {
                    log.info("{}创建目录：{}", destFile.getParentFile().mkdirs(), destFile.getParentFile().getPath());
                }

                is = zf.getInputStream(fileHeader);
                os = new FileOutputStream(destFile);
                int readLen;
                byte[] buff = new byte[BUFF_SIZE];
                while ((readLen = is.read(buff)) != -1) {
                    os.write(buff, 0, readLen);
                }
            }
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage());
        } finally {
            //关闭资源
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignored) {
            }
        }

    }

    public static String getFileNameFromExtraData(FileHeader fileHeader) {
        if (fileHeader.getExtraDataRecords() != null) {
            for (ExtraDataRecord extraDataRecord : fileHeader.getExtraDataRecords()) {
                long identifier = extraDataRecord.getHeader();
                if (identifier == 0x7075) {
                    byte[] bytes = extraDataRecord.getData();
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    byte version = buffer.get();
                    assert (version == 1);
                    return new String(bytes, 5, buffer.remaining(), StandardCharsets.UTF_8);
                }
            }
        }
        return fileHeader.getFileName();
    }

    public static void main(String[] args) {
        System.out.println(getEntryNameList("D:\\Users\\andy\\Desktop\\test.zip", ""));
        extract("D:\\Users\\andy\\Desktop\\test.zip", "123", CommonConstant.TMP_PATH);
    }
}
