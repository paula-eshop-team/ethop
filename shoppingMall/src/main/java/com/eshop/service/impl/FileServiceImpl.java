package com.eshop.service.impl;

import com.eshop.service.IFileService;
import com.eshop.utilities.FTPUtil;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 
 * @author Paula Lin
 *
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    /**
     * @param file
	 * @param path
	 * @return
	 * 
	 */
    public String upload(MultipartFile file,String path){
    	//获取上传的文件的文件名 -> String fileName
        String fileName = file.getOriginalFilename();
        //获取文件名的扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //给文件重命名,用UUID命名,这样可以避免重复名字的文件上传了,会把前一个文件覆盖
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        //logger.info用{}进行占位
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        //文件夹对象fileDir
        File fileDir = new File(path);
        //如果文件夹不存在,则创建
        if(!fileDir.exists()){
        	//设置文件夹可写的权限
            fileDir.setWritable(true);
            //创建文件夹->mkdir只是创建当前文件夹, 而mkdirs可以创建整个文件夹路径(包括当前文件夹及其子文件夹)
            fileDir.mkdirs();
        }
        //目标文件targetFile
        File targetFile = new File(path,uploadFileName);


        try {
        	//将文件MultipartFile内容传到目标文件targetFile中, 该目标文件在本项目系统.../WebRoot/upload/的路径下(在tomcat上)
            file.transferTo(targetFile);
            //文件已经上传成功了

            //将targetFile上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传到ftp服务器上

            //上传完后,删除.../WebRoot/upload/的路径下的targetFile, 该文件夹在tomcat上
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();//返回目标文件的文件名
    }

}
