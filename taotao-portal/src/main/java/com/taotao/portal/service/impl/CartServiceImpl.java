package com.taotao.portal.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;
import com.taotao.portal.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemService itemService;
    @Value("${COOKIE_EXPIRE}")
    private Integer COOKIE_EXPIRE;

    @Override
    public TaotaoResult addCart(Long itemId, Integer itemNum, HttpServletRequest request, HttpServletResponse response) {
        //接受商品id
        //从cookie中取购物列表
        List<CartItem> list = getCartItemList(request);
        boolean flag = false;
        for (CartItem item : list) {
            //商品存在数量相加
            if (item.getId() == itemId) {
                item.setNum(item.getNum()+itemNum);
                flag = true;
                break;
            }
        }
        if (!flag) {
            TbItem item = itemService.getItemById(itemId);
            //转换成CartItem
            CartItem cartItem = new CartItem();
            cartItem.setId(itemId);
            cartItem.setNum(itemNum);
            cartItem.setPrice(item.getPrice());
            cartItem.setTitle(item.getTitle());
            if (StringUtils.isNotBlank(item.getImage())) {
                String image = item.getImage();
                String[] strings = image.split(",");
                cartItem.setImage(strings[0]);
            }
            //添加到购物长商品列表
            list.add(cartItem);
        }
        //写入cookie
        CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list), COOKIE_EXPIRE, true);
        return TaotaoResult.ok();
    }

    @Override
    public List<CartItem> getCartItems(HttpServletRequest request) {
        List<CartItem> list = getCartItemList(request);
        return list;
    }

    @Override
    public TaotaoResult updateCartItem(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        List<CartItem> list = getCartItemList(request);
        for (CartItem cartItem : list) {
            if (cartItem.getId() == itemId) {
                cartItem.setNum(num);
                break;
            }
        }
        //写入cookie
        CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list), COOKIE_EXPIRE, true);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteCartItem(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<CartItem> list = getCartItemList(request);
        for (CartItem cartItem : list) {
            if (cartItem.getId() == itemId) {
                list.remove(cartItem);
                break;
            }
        }
        //写入cookie
        CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(list), COOKIE_EXPIRE, true);
        return TaotaoResult.ok();
    }

    private List<CartItem> getCartItemList(HttpServletRequest request) {
        try {
            String json = CookieUtils.getCookieValue(request, "TT_CART", true);
            List<CartItem> list = JsonUtils.jsonToList(json, CartItem.class);
            return list == null ? new ArrayList<CartItem>() : list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CartItem>();
        }
    }
}
