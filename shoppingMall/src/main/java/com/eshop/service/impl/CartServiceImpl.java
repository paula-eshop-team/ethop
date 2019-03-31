package com.eshop.service.impl;

import com.eshop.common.Const;
import com.eshop.common.ResponseCode;
import com.eshop.common.ServerResponse;
import com.eshop.dao.CartMapper;
import com.eshop.dao.ProductMapper;
import com.eshop.pojo.Cart;
import com.eshop.pojo.Product;
import com.eshop.service.ICartService;
import com.eshop.utilities.BigDecimalUtil;
import com.eshop.utilities.PropertiesUtil;
import com.eshop.vo.CartProductVo;
import com.eshop.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author Paula Lin
 *
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
	private static Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
	
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * @param userId
	 * @param productId
	 * @param count
	 * @return
	 * 
	 */
    public ServerResponse<CartVo> addProduct (Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }


        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        if(cart == null){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insertCart(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateCartByPrimaryKeySelective(cart);
        }
        return this.listCart(userId);
    }

    /**
     * @param userId
	 * @param productId
	 * @param count
	 * @return
	 * 
	 */
    public ServerResponse<CartVo> updateProduct (Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId,productId);
        //cart不为空,则更新商品的数量
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateCartByPrimaryKey(cart);
        return this.listCart(userId);
    }
    
    /**
     * @param userId
	 * @param productId
	 * @return
	 * 
	 */
    public ServerResponse<CartVo> deleteProduct (Integer userId,String productIds){
    	//用逗号","分隔字符串productIds
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIds(userId,productList);
        return this.listCart(userId);
    }

    /**
     * @param userId
	 * @return
	 * 
	 */
    public ServerResponse<CartVo> listCart (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    /**
     * @param userId
	 * @param productId
	 * @param checked
	 * @return
	 * 
	 */
    public ServerResponse<CartVo> selectOrUnSelectProduct (Integer userId,Integer productId,Integer checked){
        cartMapper.checkedCartOrUncheckedCartProduct(userId,productId,checked);
        return this.listCart(userId);
    }

    /**
     * @param userId
	 * @return
	 * 
	 */
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartTotalProductCount(userId));
    }

    /**
     * @param userId
	 * @return
	 * 
	 */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectOneCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectProductMapperByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateCartByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                	if (cartProductVo != null && cartProductVo.getProductTotalPrice() != null && cartTotalPrice != null) {
                		cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                	}
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        //商品是否全选
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getStringProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    /**
     * @param userId
	 * @return
	 * 
	 */
    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        //cartMapper.selectCartProductCheckedStatusByUserId(userId) ->查询是否有未勾选的商品
        return cartMapper.selectDetailsCartProductCheckedStatusByUserId(userId) == 0;

    }

	/* (non-Javadoc)
	 * @see com.eshop.service.ICartService#selectCartListInfo()
	 */
	@Override
	public List<Cart> findCartListInfo() {
		return cartMapper.findCartListInfo();
	}


}
