package com.taotao.rest.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.mysql.jdbc.StringUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.rest.component.JedisClient;
import com.taotao.rest.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper contentMapper;
    //先去applicationContext-service.xml中打开jedisClient实现类的注入的注释
    //再打开此处注释注入jedisClient使用redis
    @Autowired
    private JedisClient jedisClient;
    @Value("${REDIS_CONTENT_KEY}")
    private String REDIS_CONTENT_KEY;


    @Override
    public List<TbContent> getContentList(Long cid) {
        //查询数据库之前查询redis缓存
        //如果有直接返回 否则查询数据库
        try {
            //从redis中取出缓存数据
            String json = jedisClient.hget(REDIS_CONTENT_KEY, cid+"");
            if(!org.apache.commons.lang3.StringUtils.isBlank(json)) {
                //json转list
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(cid);
        List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);

        //返回结果前 添加到redis缓存
        try {
            //规范key 使用hash
            //定义保存内容的key hash中每个项是cid
            //value是转换成json的list
            jedisClient.hset(REDIS_CONTENT_KEY,cid+"", JsonUtils.objectToJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //删除缓存 同步内容
    @Override
    public TaotaoResult sybcContent(Long cid) {
        jedisClient.hdel(REDIS_CONTENT_KEY, cid+"");
        return TaotaoResult.ok();
    }
}
