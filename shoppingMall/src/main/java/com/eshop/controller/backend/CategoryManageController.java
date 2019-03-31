package com.eshop.controller.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eshop.common.ServerResponse;
import com.eshop.service.ICategoryService;
import com.eshop.service.IUserService;

/**
 * Description: 
 * This is the controller to handle the request for product's category rule setting and query:
 * 		1.	add category rule for products.
 *		2.	reset category name for category.
 *		3.	get all the children categories of category.
 *		4.	get children categories which is directly under one parent category.
 *		5.	get deep children categories of the parent category
 * 
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

	private static Logger logger = LoggerFactory.getLogger(CategoryManageController.class);
	
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * Description: 
	 * This method handler is provided for the request to add category rule for products.
	 * 
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
    	
    	//AuthorityInterceptor will intercept the request and validate the authority of the user before add category rule.
    	return iCategoryService.addCategory(categoryName,parentId);
    }

    /**
     * 
     * Description: 
	 * This method handler is provided for the request to reset category name for one category.
	 * 
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
    	//AuthorityInterceptor will intercept the request and validate the authority of the user before add category rule.
    	return iCategoryService.updateCategoryName(categoryId,categoryName);
    }
    
    /**
     * 
     * Description: 
	 * This method handler is provided for the request to get children categories which is directly under one parent category.
	 * 
     * @param categoryId
     * @return
     */
    @RequestMapping("get_children_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
    	//AuthorityInterceptor will intercept the request and validate the authority of the user before add category rule.
    	return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 
     * Description: 
	 * This method handler is provided for the request to get deep children categories of the parent category
	 * including parent category's children categories and deep looping all children categories' children categories.
	 * 
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_childrencategory.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
    	//AuthorityInterceptor will intercept the request and validate the authority of the user before add category rule.
    	return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
