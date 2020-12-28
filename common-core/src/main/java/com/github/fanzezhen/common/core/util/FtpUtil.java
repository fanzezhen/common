package com.github.fanzezhen.common.core.util;

import com.github.fanzezhen.common.core.model.response.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@AllArgsConstructor
public class FtpUtil {
    private String hostname;
    private int port;
    private String username;
    private String password;
    private String rootPath = "/imageroot";
    private String address = "http://image.sgst.cn";
    private String pathSeparator = "/";
    private FTPClient ftpClient = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public FtpUtil() {
    }

    public FtpUtil(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public FtpUtil(int port, String hostname, String username, String password, String rootPath, String address, String pathSeparator) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.rootPath = rootPath;
        this.address = address;
        this.pathSeparator = pathSeparator;
    }

    public boolean uploadFile(String fileName, String originFilename) {
        boolean result;
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("utf-8");
        try {
            inputStream = new FileInputStream(originFilename);
            // 连接ftp服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            // 设置二进制传输模式
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            result = ftpClient.login(username, password) && ftpClient.setFileType(FTP.BINARY_FILE_TYPE) && ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            doEnd();
        }
        return result;
    }

    public Result<String> uploadFile(String fullName, File file) {
        if (StringUtils.isBlank(fullName) || file == null) {
            return Result.failed("参数不能为空");
        }
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            // 获取状态码
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();        //结束连接
                return Result.failed("ftp登录失败");
            }
            // 将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                return Result.failed("设置二进制传输模式失败");
            }
            // 上传文件 成功true 失败 false
            if (!ftpClient.storeFile(fullName, inputStream)) {
                return Result.failed("storeFile上传失败");
            }
            ftpClient.logout();
        } catch (IOException e) {
            log.warn(e.toString());
            return Result.failed(e.getLocalizedMessage());
        } finally {
            doEnd();
        }
        return Result.ok(address + fullName.replace(rootPath, ""));
    }

    public Result<String> uploadFile(String fullPath, String fileName, File file) {
        if (StringUtils.isAnyBlank(fullPath, fileName) || file == null) {
            return Result.failed("参数不能为空");
        }
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器, 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            // 获取状态码
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();        //结束连接
                return Result.failed("登录失败");
            }
            // 将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                return Result.failed("设置二进制传输模式失败");
            }
            boolean changeWorkingDirectory = changeWorkingDirectory(fullPath);
            if (!changeWorkingDirectory) {
                log.info("创建/切换目录失败：{}", fullPath);
            }
            if (changeWorkingDirectory) {
                // 上传文件 成功true 失败 false
                log.debug("inputStream.available(): " + inputStream.available());
                String remote = fullPath;
                if (fullPath.contains(pathSeparator) && !fullPath.endsWith(pathSeparator)) {
                    remote += pathSeparator;
                }
                remote += fileName;
                if (!ftpClient.storeFile(remote, inputStream)) {
                    return Result.failed("上传失败");
                }
            } else {
                return Result.failed("目录创建/切换失败：{}", fullPath);
            }
            ftpClient.logout();
        } catch (IOException e) {
            log.warn(e.toString());
            return Result.failed(e.getLocalizedMessage());
        } finally {
            doEnd();
        }
        String url = address + fullPath.replace(rootPath, "");
        if (!url.endsWith(pathSeparator)) {
            url += pathSeparator;
        }
        url += fileName;
        return Result.ok(url);
    }

    public void downloadFile(String fileName, String localPath) {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(username, password);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (fileName.equalsIgnoreCase(ftpFile.getName())) {
                    File file = new File(localPath + pathSeparator + ftpFile.getName());
                    outputStream = new FileOutputStream(file);
                    ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            doEnd();
        }
    }

    public void deleteFile(String fileName) {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(username, password);
            ftpClient.dele(fileName);
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean changeWorkingDirectory(String fullPath) {
        boolean changeWorkingDirectory = false;
        try {
            changeWorkingDirectory = ftpClient.changeWorkingDirectory(fullPath);
            if (changeWorkingDirectory) {
                log.debug("切换目录成功：" + fullPath);
                changeWorkingDirectory = true;
            } else {
                boolean makeDirectory = ftpClient.makeDirectory(fullPath);
                if (makeDirectory) {
                    changeWorkingDirectory = ftpClient.changeWorkingDirectory(fullPath);
                } else {
                    int lastIndexOfPathSeparator = fullPath.lastIndexOf(pathSeparator);
                    if (lastIndexOfPathSeparator > 0) {
                        changeWorkingDirectory = changeWorkingDirectory(fullPath.substring(0, lastIndexOfPathSeparator));
                    }
                }
                log.debug("目录{}不存在，创建：{}， 切换：{}", fullPath, makeDirectory, changeWorkingDirectory);
            }
        } catch (IOException e) {
            log.warn(e.getLocalizedMessage());
        }
        return changeWorkingDirectory;
    }

    private void doEnd(FTPClient ftpClient, InputStream inputStream, OutputStream outputStream) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CommonUtil.doClose(inputStream, outputStream);
    }

    private void doEnd() {
        doEnd(ftpClient, inputStream, outputStream);
    }

    public static void main(String[] args) {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.uploadFile("/imageroot/tzrck/timg.jpg", "D:\\timg.jpg");
    }
}
