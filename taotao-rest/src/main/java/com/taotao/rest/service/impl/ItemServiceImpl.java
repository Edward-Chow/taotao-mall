package com.taotao.rest.service.impl;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.rest.component.JedisClient;
import com.taotao.rest.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

//商品管理service
@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private TbItemParamItemMapper paramItemMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${REDIS_ITEM_KEY}")
    private String REDIS_ITEM_KEY;
    @Value("${ITEM_BASE_INFO_KEY}")
    private String ITEM_BASE_INFO_KEY;
    @Value("${ITEM_EXPIRE_SEC}")
    private Integer ITEM_EXPIRE_SEC;
    @Value("${ITEM_DESC_KEY}")
    private String ITEM_DESC_KEY;
    @Value("${ITEM_PARAM_KEY}")
    private String ITEM_PARAM_KEY;

    @Override
    public TbItem getItemById(Long itemId) {
        //添加redis缓存
        //查询缓存 若有记录直接返回
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_BASE_INFO_KEY);
            //判断数据是否存在
            if (StringUtils.isNotBlank(json)) {
                TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //根据商品id查询商品基本信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //向redis中添加缓存
        //设置过期时间，防止冷门商品长时间驻留缓存
        //过期时间只能在hash的key上设置，不能在项上设置
        try {
            jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_BASE_INFO_KEY, JsonUtils.objectToJson(item));
            jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_BASE_INFO_KEY, ITEM_EXPIRE_SEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    //根据商品id查询详细信息
    @Override
    public TbItemDesc getItemDescById(Long itemId) {
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_DESC_KEY);
            //判断数据是否存在
            if (StringUtils.isNotBlank(json)) {
                TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return itemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        try {
            //向redis中添加缓存
            jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_DESC_KEY, JsonUtils.objectToJson(itemDesc));
            //设置过期时间
            jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_DESC_KEY, ITEM_EXPIRE_SEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemDesc;
    }

    @Override
    public TbItemParamItem getItemParamById(Long itemId) {
        try {
            String json = jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_PARAM_KEY);
            //判断数据是否存在
            if (StringUtils.isNotBlank(json)) {
                TbItemParamItem itemParamItem = JsonUtils.jsonToPojo(json, TbItemParamItem.class);
                return itemParamItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list = paramItemMapper.selectByExampleWithBLOBs(example);
        //取出规格参数
        if (list != null && list.size() > 0) {
            TbItemParamItem itemParamItem = list.get(0);
            try {
                //向redis中添加缓存
                jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_PARAM_KEY, JsonUtils.objectToJson(itemParamItem));
                //设置过期时间
                jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":"+ITEM_PARAM_KEY, ITEM_EXPIRE_SEC);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return itemParamItem;
        }
        return null;
    }
}
