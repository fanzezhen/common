package com.github.fanzezhen.common.core.util;

import cn.hutool.core.date.DateUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author zezhen.fan
 */
@Slf4j
public class CommonUtil {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Class<T> classType, Object value) {
        try {
            T result = classType.getDeclaredConstructor().newInstance();
            if (value == null) {
                return result;
            }
            if (value instanceof String) {
                String valueString = String.valueOf(value);
                if (classType == Date.class) {
                    result = (T) DateUtil.parse(valueString);
                } else if (classType == Long.class) {
                    result = (T) Long.valueOf(valueString);
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("类型转化失败", e);
        }
        return null;
    }

    public static void response(HttpServletResponse response, File file) {
        // 清空输出流
        response.reset();
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        try {
            // 设置文件名
            response.addHeader("Content-Disposition", "attachment;fileName=" +
                    new String(file.getName().getBytes(StandardCharsets.UTF_8), "8859_1"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "导出文件名设置失败！");
        }
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            // 获取response输出流
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (FileNotFoundException e) {
            throw new ServiceException(CoreExceptionEnum.FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "输出流读取失败！");
        } finally {
            CommonUtil.doClose(fis, bis);
        }
    }

    public static void doClose(Closeable... closeAbles) {
        for (Closeable closeable : closeAbles) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.warn(e.getLocalizedMessage());
                }
            }
        }
    }
}
