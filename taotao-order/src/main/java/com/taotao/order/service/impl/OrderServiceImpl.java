package com.taotao.order.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.component.JedisClient;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper shippingMapper;
    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_ORDER_GEN_KEY}")
    private String REDIS_ORDER_GEN_KEY;
    @Value("${ORDER_ID_BEGIN}")
    private String ORDER_ID_BEGIN;
    @Value("${REDIS_ORDER_DETAIL_GEN_KEY}")
    private String REDIS_ORDER_DETAIL_GEN_KEY;

    @Override
    public TaotaoResult createOrder(OrderInfo orderInfo) {
        //向订单表中插入记录
        //获得订单号
        String id = jedisClient.get(REDIS_ORDER_GEN_KEY);
        if (StringUtils.isBlank(id)) {
            jedisClient.set(REDIS_ORDER_GEN_KEY, ORDER_ID_BEGIN);
        }
        Long orderId = jedisClient.incr(REDIS_ORDER_GEN_KEY);
        //补全pojo的属性
        orderInfo.setOrderId(orderId.toString());
        //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setStatus(1);
        Date date = new Date();
        orderInfo.setCreateTime(date);
        orderInfo.setUpdateTime(date);
        //0：未评价 1：已评价
        orderInfo.setBuyerRate(0);
        //向订单表插入数据
        orderMapper.insert(orderInfo);
        //插入订单明细
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem tbOrderItem : orderItems) {
            //补全订单明细
            //取订单明细id
            Long orderDetailId = jedisClient.incr(REDIS_ORDER_DETAIL_GEN_KEY);
            tbOrderItem.setId(orderDetailId.toString());
            tbOrderItem.setOrderId(orderId.toString());
            //向订单明细插入记录
            orderItemMapper.insert(tbOrderItem);
        }
        //插入物流表
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        //补全物流表的属性
        orderShipping.setOrderId(orderId.toString());
        orderShipping.setCreated(date);
        orderShipping.setUpdated(date);
        shippingMapper.insert(orderShipping);

        return TaotaoResult.ok(orderId);
    }
}
