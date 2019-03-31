package com.eshop.controller.backend;

import com.eshop.common.ServerResponse;
import com.eshop.service.IOrderService;
import com.eshop.service.IUserService;
import com.eshop.vo.OrderVo;
import com.github.pagehelper.PageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 
 * This is the controller to handle the request on order management in background system:
 * 		1.	List all the orders by page.
 *		2.	Get detailed order by orderNo.
 *		3.	Search order by orderNo .
 *		4.	Set the order as shipped status.
 * 
 * @author Paula Lin
 *
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

	private static Logger logger = LoggerFactory.getLogger(OrderManageController.class);
	
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**
     * Description: 
     * This method handler is to list all the orders by page.
     * 
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list_all_orders.do")
    @ResponseBody
    public ServerResponse<PageInfo> listAllOrders(@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
    	return iOrderService.manageList(pageNum,pageSize);
    }

    /**
     * Description: 
     * This method handler is to get detailed order by orderNo .
     * 
     * @param orderNo
     * @return
     */
    @RequestMapping("get_order_detail.do")
    @ResponseBody
  public ServerResponse<OrderVo> getOrderDetail(Long orderNo){
    	return iOrderService.manageDetail(orderNo);
    }

    /**
     * Description: 
     * This method handler is to search order by orderNo .
     * 
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search_order.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchOrder(Long orderNo,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
    	return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }


    /**
     * Description: 
     * This method handler is to set the order as shipped status.
     * 
     * 发货
     * @param orderNo
     * @return
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(Long orderNo){
    	return iOrderService.manageSendGoods(orderNo);
    }
}
