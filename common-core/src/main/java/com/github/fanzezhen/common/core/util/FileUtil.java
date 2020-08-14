package com.github.fanzezhen.common.core.util;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.constant.CommonConstant;
import com.github.fanzezhen.common.core.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
public class FileUtil {
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MultipartFile转File
     */
    public static File multipartFileToFile(MultipartFile multipartFile) {
        File f;
        if (multipartFile == null || multipartFile.getSize() <= 0) {
            return null;
        } else {
            InputStream ins;
            try {
                ins = multipartFile.getInputStream();
            } catch (IOException e) {
                throw new ServiceException(CoreExceptionEnum.FILE_READING_ERROR);
            }
            f = new File(SysConstant.TMP_PATH + multipartFile.getOriginalFilename());
            inputStreamToFile(ins, f);
        }
        return f;
    }

    /**
     * MultipartFile 转 File
     *
     * @param multipartFile 源文件
     * @param path          目标file路径
     * @return file
     */
    public static File multipartFileToFile(MultipartFile multipartFile, String path) throws IOException {
        return multipartFileToFile(multipartFile, path, multipartFile.getOriginalFilename());
    }

    /**
     * MultipartFile 转 File
     *
     * @param multipartFile 源文件
     * @param path          目标file路径
     * @param name          目标file名称
     * @return file
     */
    public static File multipartFileToFile(MultipartFile multipartFile, String path, String name) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {// 判断目录是否存在
            log.debug("目录{}不存在，正在创建...{}", path, dir.mkdirs());  //多层目录需要调用mkdirs
        }
        return multipartFileToFile(multipartFile, path + CommonConstant.SEPARATOR + name);
    }

    public static void doClose(Closeable... closeables) {
        for (Closeable closeable : closeables)
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage());
                }
            }
    }
}
