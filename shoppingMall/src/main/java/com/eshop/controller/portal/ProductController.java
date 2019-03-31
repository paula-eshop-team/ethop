package com.eshop.controller.portal;

import com.eshop.common.ServerResponse;
import com.eshop.service.IProductService;
import com.eshop.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 
 * This is the controller to handle the request to ask for listing products with searching criteria:
 *      1. List product details by filtering productId 
 * 		2. List products by filtering keyword and categoryId
 *      3. List products by filtering keyword
 *      4. List products by filtering categoryId
 * 
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

	private static Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private IProductService iProductService;

    /**
     * Description: 
     * This method handler is to list product details by filtering productId
     * 
     * @param productId
     * @return
     */
    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(@PathVariable Integer productId){
        return iProductService.getProductDetail(productId);
    }
  
    /**
     * Description: 
     * This method handler is to list product by filtering keyword and categoryId, and paginate it.
     * 
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping(value = "/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listProductsByKeywordAndCategory(@PathVariable(value = "keyword")String keyword,
                                         @PathVariable(value = "categoryId")Integer categoryId,
                                         @PathVariable(value = "pageNum") Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @PathVariable(value = "orderBy") String orderBy){
        if(pageNum == null){
            pageNum = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy = "price_asc";
        }

        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

  /**
   * Description: 
   * This method handler is to list product by filtering keyword, and paginate it.
   * 
   * @param keyword
   * @param pageNum
   * @param pageSize
   * @param orderBy
   * @return
   */
  @RequestMapping(value = "/keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
  @ResponseBody
  public ServerResponse<PageInfo> listProductsByKeyword(@PathVariable(value = "keyword")String keyword,
                                                     @PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                     @PathVariable(value = "orderBy") String orderBy){
      if(pageNum == null){
          pageNum = 1;
      }
      if(pageSize == null){
          pageSize = 10;
      }
      if(StringUtils.isBlank(orderBy)){
          orderBy = "price_asc";
      }

      return iProductService.getProductByKeywordCategory(keyword,null,pageNum,pageSize,orderBy);
  }

  /**
   * Description: 
   * This method handler is to list product by filtering categoryId, and paginate it.
   * 
   * @param categoryId
   * @param pageNum
   * @param pageSize
   * @param orderBy
   * @return
   */
  @RequestMapping(value = "/category/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
  @ResponseBody
  public ServerResponse<PageInfo> listProductsByCategory(@PathVariable(value = "categoryId")Integer categoryId,
                                                     @PathVariable(value = "pageNum") Integer pageNum,
                                                     @PathVariable(value = "pageSize") Integer pageSize,
                                                     @PathVariable(value = "orderBy") String orderBy){
      if(pageNum == null){
          pageNum = 1;
      }
      if(pageSize == null){
          pageSize = 10;
      }
      if(StringUtils.isBlank(orderBy)){
          orderBy = "price_asc";
      }

      return iProductService.getProductByKeywordCategory("",categoryId,pageNum,pageSize,orderBy);
  }

}
