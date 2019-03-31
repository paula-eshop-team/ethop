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
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insertCart(cartItem);
        }else{
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
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateCartByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                	if (cartProductVo != null && cartProductVo.getProductTotalPrice() != null && cartTotalPrice != null) {
                		cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                	}
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
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
        return cartMapper.selectDetailsCartProductCheckedStatusByUserId(userId) == 0;
    }

	@Override
	public List<Cart> findCartListInfo() {
		return cartMapper.findCartListInfo();
	}
}
