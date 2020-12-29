package com.github.fanzezhen.common.core.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
public class CommonUtil {
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
