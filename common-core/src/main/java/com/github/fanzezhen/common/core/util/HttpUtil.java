package com.github.fanzezhen.common.core.util;

import cn.stylefeng.roses.kernel.model.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpUtil {

    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", "1956761");
        System.out.println(params);
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String ajaxFlag = request.getHeader("X-Requested-With");
        if (ajaxFlag != null) {
            return "XMLHttpRequest".equals(ajaxFlag);
        } else {
            return false;
        }
    }

    /**
     * POST请求
     */
    public static ResponseData doPost(String url, Map<String, String> params) {
        ResponseData result;
        OutputStream out = null;
        DataOutputStream dataOutputStream = null;
        InputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            // 默认情况下是false;
            httpUrlConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod(HttpMethod.POST.name());
            httpUrlConnection.connect();

            // 建立输入流，向指向的URL传入参数

            StringBuilder queryString = getQueryString(params);

            if (queryString.length() > 0) {
                queryString = new StringBuilder(queryString.substring(0, queryString.length() - 1));
                out = httpUrlConnection.getOutputStream();
                dataOutputStream = new DataOutputStream(out);
                dataOutputStream.writeBytes(queryString.toString());

                dataOutputStream.flush();
                out.flush();
            }

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                int len;
                in = httpUrlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                result = ResponseData.success(byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } else {
                result = ResponseData.error(responseCode, "请求异常");
            }
        } catch (IOException e) {
            result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getLocalizedMessage());
        } finally {
            if (!(closeStream(byteArrayOutputStream) && closeStream(in) && closeStream(out) && closeStream(dataOutputStream))) {
                result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求关闭异常");
            }
        }
        return result;
    }

    /**
     * GET请求
     */
    public static ResponseData doGet(String url, HashMap<String, String> params) {
        ResponseData result;

        InputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            // URL传入参数
            StringBuilder queryString = getQueryString(params);

            if (queryString.length() > 0) {
                queryString = new StringBuilder(queryString
                        .substring(0, queryString.length() - 1));

                url = url + "?" + queryString;
            }

            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            // 设置是否向httpUrlConnection输出，post请求，参数要放在http正文内，因此需要设为true,
            // 默认情况下是false;
            httpUrlConnection.setDoOutput(false);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // 忽略缓存
            httpUrlConnection.setUseCaches(false);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            // 获得响应状态
            int responseCode = httpUrlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                byte[] buffer = new byte[1024];
                int len;
                in = httpUrlConnection.getInputStream();
                while ((len = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                result = ResponseData.success(byteArrayOutputStream.toString(StandardCharsets.UTF_8));
            } else {
                result = ResponseData.error(responseCode, "请求异常");
            }
        } catch (IOException e) {
            result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getLocalizedMessage());
        } finally {
            if (!(closeStream(byteArrayOutputStream) && closeStream(in))) {
                result = ResponseData.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求关闭异常");
            }
        }

        return result;
    }

    public static ResponseData doRequest(String url, HashMap<String, String> params, String method) {
        if (HttpMethod.GET.name().equals(method)) return doGet(url, params);
        else return doPost(url, params);
    }

    private static StringBuilder getQueryString(Map<String, String> params) {
        StringBuilder queryString = new StringBuilder();

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),
                        StandardCharsets.UTF_8)).append("&");
            }
        }
        return queryString;
    }

    private static boolean closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error(e.toString());
                return false;
            }
        }
        return true;
    }
}
