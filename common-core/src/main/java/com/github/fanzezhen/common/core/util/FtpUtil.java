package com.github.fanzezhen.common.core.util;

import com.github.fanzezhen.common.core.model.response.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

@Slf4j
public class FtpUtil {
    private String hostname;
    private int port;
    private String username;
    private String password;
    private String rootPath = "/imageroot";
    private String address = "http://image.sgst.cn";
    private FTPClient ftpClient = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public FtpUtil(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public FtpUtil() {
    }

    public boolean uploadFile(String fileName, String originFilename) {
        boolean result;
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("utf-8");
        try {
            inputStream = new FileInputStream(new File(originFilename));
            // 连接ftp服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
//            ftpClient.login(username, password);
            // 设置二进制传输模式
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
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

    public R<String> uploadFile(String fullName, File file) {
        if (StringUtils.isBlank(fullName) || file == null) {
            return R.failed("参数不能为空");
        }
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            int reply = ftpClient.getReplyCode();   //获取状态码
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();        //结束连接
                return R.failed("ftp登录失败");
            }
            //将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                return R.failed("设置二进制传输模式失败");
            }
            //上传文件 成功true 失败 false
            if (!ftpClient.storeFile(fullName, inputStream)) {
                return R.failed("storeFile上传失败");
            }
            ftpClient.logout();
        } catch (IOException e) {
            log.warn(e.toString());
            return R.failed(e.getLocalizedMessage());
        } finally {
            doEnd();
        }
        return R.ok(address + fullName.replace(rootPath, ""));
    }

    public R<String> uploadFile(String fullPath, String fileName, File file) {
        if (StringUtils.isAnyBlank(fullPath, fileName) || file == null) return R.failed("参数不能为空");
        ftpClient = new FTPClient();
        // 设置utf-8编码
        ftpClient.setControlEncoding("UTF-8");
        try {
            // 连接FTP服务器// 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.connect(hostname, port);
            // 登录ftp服务器
            ftpClient.login(username, password);
            int reply = ftpClient.getReplyCode();   //获取状态码
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();        //结束连接
                return R.failed("登录失败");
            }
            //将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                return R.failed("设置二进制传输模式失败");
            }
            boolean changeWorkingDirectory = ftpClient.changeWorkingDirectory(fullPath);
            if (!changeWorkingDirectory) {
                System.out.println("目录不存在");
                boolean makeDirectory = ftpClient.makeDirectory(fullPath);
                changeWorkingDirectory = ftpClient.changeWorkingDirectory(fullPath);
                System.out.println("创建： " + makeDirectory + "; 切换： " + changeWorkingDirectory);
            } else {
                System.out.println("目录存在");
            }
            if (changeWorkingDirectory) {
                //上传文件 成功true 失败 false
                System.out.println("inputStream.available(): " + inputStream.available());
                String remote = fullPath;
                if (fullPath.contains("/") && !fullPath.endsWith("/")) remote += "/";
                remote += fileName;
                if (!ftpClient.storeFile(remote, inputStream)) return R.failed("上传失败");
            } else return R.failed("目录创建失败");
            ftpClient.logout();
        } catch (IOException e) {
            log.warn(e.toString());
            return R.failed(e.getLocalizedMessage());
        } finally {
            doEnd();
        }
        return R.ok(address + fullPath.replace(rootPath, "") + fileName);
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
                    File file = new File(localPath + "/" + ftpFile.getName());
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

    private void doEnd(FTPClient ftpClient, InputStream inputStream, OutputStream outputStream) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileUtil.doClose(inputStream, outputStream);
    }

    private void doEnd() {
        doEnd(ftpClient, inputStream, outputStream);
    }

    public static void main(String[] args) {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.uploadFile("/imageroot/tzrck/timg.jpg", "D:\\timg.jpg");
//		ftpUtil.downloadFile("a.txt", "C://Users//huangwending//Desktop//");
//		ftpUtil.deleteFile("timg.jpg");
    }
}
