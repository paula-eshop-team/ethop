package com.eshop.utilities;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author Paula Lin
 *
 */
public class FTPUtil {

    private static  final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getStringProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getStringProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getStringProperty("ftp.pass");
    
    /**
     * 
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }
    
    /**
     * 
     * @param fileList
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器,结束上传,上传结果:{}", result);
        return result;
    }

    /**
     * 
     * @param remotePath
     * @param fileList
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接FTP服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
        	//连接FTP服务器成功
            try {
            	//切换文件夹到Linux本地目录下的img
                ftpClient.changeWorkingDirectory(remotePath);
                //设置ftp缓冲区
                ftpClient.setBufferSize(1024);
                //设置Encoding
                ftpClient.setControlEncoding("UTF-8");
                //文件类型设置为二进制文件类型,可以防止乱码; 之前FTP配置为被动模式,并且对外开放了服务被动端口范围
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开FTP本地的被动模式
                ftpClient.enterLocalPassiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    //ftpClient调用storeFile来存储文件
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            } finally {
            	//释放流
                fis.close();
                //释放连接
                ftpClient.disconnect();
            }
        } else {
        	uploaded = false;
        }
        return uploaded;
    }

    /**
     * 
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    /*
     * FTP服务器连接
     */
    private boolean connectServer(String ip,int port,String user,String pwd){

        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }











    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
