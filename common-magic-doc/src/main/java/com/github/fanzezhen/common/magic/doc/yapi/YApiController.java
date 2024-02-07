package com.github.fanzezhen.common.magic.doc.yapi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author zezhen.fan
 */
@RestController
@RequestMapping("/doc/y-api")
public class YApiController {
    @Resource
    private YApiFacade yApiFacade;

    @GetMapping(value = "/get")
    public JSONArray get(HttpServletRequest request) {
        return yApiFacade.getApiJSONArray(request);
    }

    @GetMapping(value = "/export")
    public void export(@RequestParam(required = false) String filename,
                       @RequestParam(required = false, defaultValue = "false") Boolean prettyFormat,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        JSONArray apis = yApiFacade.getApiJSONArray(request);
        if (CharSequenceUtil.isBlank(filename)) {
            filename = "y-api";
        }
        File tempFile = FileUtil.createTempFile(filename, ".json", true);
        FileUtil.writeString(JSON.toJSONString(apis, prettyFormat), tempFile, StandardCharsets.UTF_8);
        // 设置响应内容类型  
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setLocale(new java.util.Locale("zh", "CN"));
        response.setContentType("application/msexcel;charset=UTF-8");
        // 设置响应头，指定文件下载时的名称  
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + ".json");
        response.addHeader("Accept-Language", "zh-cn");
        try (InputStream in = Files.newInputStream(tempFile.toPath()); OutputStream out = response.getOutputStream()) {
            // 读取文件并写入响应输出流  
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    @GetMapping(value = "/sync")
    public JSONArray sync(HttpServletRequest request) {
        return yApiFacade.getApiJSONArray(request);
    }

}
