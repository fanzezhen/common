package com.github.fanzezhen.common.core.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.HashMap;

public class FtpUtil {
    private String hostname;
    private int port;
    private String username;
    private String password;
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

    public HashMap<String, String> uploadFile(String fullName, File file) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("code", "error");
        }};
        if (fullName == null || file == null) {
            result.put("msg", "参数不能为空");
            return result;
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
                result.put("msg", "登录失败");           //根据状态码判断是否登录成功
                return result;
            }
            //将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                result.put("msg", "设置二进制传输模式失败");
                return result;
            }
            ;
            //上传文件 成功true 失败 false
            if (!ftpClient.storeFile(fullName, inputStream)) {
                result.put("msg", "上传失败");
            } else {
                result.put("code", "success");
                result.put("msg", "上传成功");
            }
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doEnd();
        }
        return result;
    }

    public HashMap<String, String> uploadFile(String fullPath, String fileName, File file) {
        HashMap<String, String> result = new HashMap<String, String>() {{
            put("code", "error");
        }};
        if (fullPath == null || fileName == null || file == null) {
            result.put("msg", "参数不能为空");
            return result;
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
                result.put("msg", "登录失败");           //根据状态码判断是否登录成功
                return result;
            }
            //将客户端设置为被动模式
            ftpClient.enterLocalPassiveMode();
            inputStream = new FileInputStream(file);
            // 设置二进制传输模式
            if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                result.put("msg", "设置二进制传输模式失败");
                return result;
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
                if (!ftpClient.storeFile(fullPath + fileName, inputStream)) {
                    result.put("msg", "上传失败");
                } else {
                    result.put("code", "success");
                    result.put("msg", "上传成功");
                }
            } else
                result.put("msg", "目录创建失败");
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doEnd();
        }
        return result;
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

    private void doEnd() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.uploadFile("/imageroot/tzrck/timg.jpg", "D:\\timg.jpg");
//		ftpUtil.downloadFile("a.txt", "C://Users//huangwending//Desktop//");
//		ftpUtil.deleteFile("timg.jpg");
    }
}
