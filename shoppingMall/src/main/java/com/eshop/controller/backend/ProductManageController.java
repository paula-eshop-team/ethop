package com.eshop.controller.backend;

import com.eshop.common.Const;
import com.eshop.common.ServerResponse;
import com.eshop.pojo.Product;
import com.eshop.service.IFileService;
import com.eshop.service.IProductService;
import com.eshop.service.IUserService;
import com.eshop.utilities.PropertiesUtil;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description: 
 * This is the controller to handle the request to ask for product management in background system:
 * 
 *      1.	add new product or update product's information
 *		2.	set the sale status of product to let the product on sale or off the shelves.
 *		3.	get the details of one product.
 *		4.	get the list of product by pageNum and pageSize
 *		5.	search product by filtering productName and productId
 *		6.	upload file(like text file) to ftp server
 *		7.	upload rich text and image to system server

 *      
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

	private static Logger logger = LoggerFactory.getLogger(ProductManageController.class);
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * Description: 
     * This method handler is to handle the request to add new product or update product's information in background system.
     * 
     * @param product
     * @return
     */
    @RequestMapping("save_product.do")
    @ResponseBody
   public ServerResponse saveProduct(Product product){
        return iProductService.saveOrUpdateProduct(product);
    }
  
    /**
     * Description: 
     * This method handler is to handle the request to set the sale status of product to let the product on sale or off the shelves.
     *    sale status: 1 - onsale,  2 - off the shelves
     * 
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(Integer productId,Integer status){
    	return iProductService.setSaleStatus(productId,status);
    }

    /**
     * Description: 
     * This method handler is to handle the request to get the details of one product.
     * 
     * @param productId
     * @return
     */
    @RequestMapping("get_product_details.do")
    @ResponseBody
    public ServerResponse getProductDetail(Integer productId){
    	
    	//get the details of product by filtering productId
    	return iProductService.manageProductDetail(productId);
    }
 
    /**
     * Description: 
     * This method handler is to handle the request to get the list of product by pageNum and pageSize in background system.
     * 	pageNum is default as 1, and pageSize is default as 10.
     * 
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_product_list.do")
    @ResponseBody
    public ServerResponse getProductList(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
    	 return iProductService.getProductList(pageNum,pageSize);
    }
    
    /**
     * Description: 
     * This method handler is to handle the request to search product by filtering productName and productId in background system.
     * 
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
    	return iProductService.searchProduct(productName,productId,pageNum,pageSize);
    }

    /**
     * Description: 
     * This method handler is to handle the request to upload file(like text file) to ftp server.
     * 
     * @param request
     * @param file
     * @return
     */
    @RequestMapping("upload_txt_file.do")
    @ResponseBody
  public ServerResponse uploadTxtFile(HttpServletRequest request, @RequestParam(value = "upload_file",required = false) MultipartFile file){
    	//Get the path of upload file(in this backend server) which temporarily save the upload_file
    	String path = request.getSession().getServletContext().getRealPath("upload");
    	//Upload the file from this backend server to ftp server
        String targetFileName = iFileService.upload(file,path);
        //split join the url of file in ftp server for responding
        String url = PropertiesUtil.getStringProperty("ftp.server.http.prefix")+targetFileName;

        //Response with fileMap
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.createBySuccess(fileMap);
    }

    /**
     * Description: 
     * This method handler is to handle the request to upload rich text and image to system server.
     * 
     * @param request
     * @param response
     * @param file
     * @return
     */
    /*
     * 富文本文件上传
     */
    @RequestMapping("upload_richtext_img.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "upload_file",required = false) MultipartFile file){
    	Map resultMap = Maps.newHashMap();
    	
    	//Get the path of upload file(in this backend server) which temporarily save the upload_file
    	String path = request.getSession().getServletContext().getRealPath("upload");
        
    	//Upload the file from this backend server to ftp server
    	String targetFileName = iFileService.upload(file,path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success",false);
            resultMap.put("msg",Const.ErrorMessage.UPLOAD_FILE_FAILED);
            return resultMap;
        }
        
        //split join the url of file in ftp server for responding
        String url = PropertiesUtil.getStringProperty("ftp.server.http.prefix")+targetFileName;
        resultMap.put("success",true);
        resultMap.put("msg",Const.ErrorMessage.UPLOAD_FILE_SUCCESS);
        resultMap.put("file_path",url);
        
        //Need to reset header in HttpServletResponse for the upload of rich text and image
        response.addHeader("Access-Control-Allow-Headers","X-File-Name");
        return resultMap;
    }
    
}
