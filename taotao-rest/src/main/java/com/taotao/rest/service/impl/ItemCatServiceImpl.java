package com.taotao.rest.service.impl;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.rest.component.JedisClient;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.ItemCatResult;
import com.taotao.rest.service.ItemCatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private TbItemCatMapper itemCatMapper;

    //先去applicationContext-service.xml中打开jedisClient实现类的注入的注释
    //再打开此处注释注入jedisClient使用redis
//    @Autowired
//    private JedisClient jedisClient;
//    @Value("${REDIS_CONTENT_CAT_KEY}")
//    private String REDIS_CONTENT_CAT_KEY;

    @Override
    public ItemCatResult getItemCatService() {
        //从缓存中取内容
//        try {
//            String result = jedisClient.hget(REDIS_CONTENT_CAT_KEY, "0");
//            if (!StringUtils.isBlank(result)) {
//                //把字符串转换成list
//                return JsonUtils.jsonToPojo(result, ItemCatResult.class);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        List catList = getItemCatList(0l);
        ItemCatResult result = new ItemCatResult();
        result.setData(catList);

        //向缓存中添加内容
//        try {
//            //把list转换成字符串
//            String cacheString = JsonUtils.objectToJson(result);
//            jedisClient.hset(REDIS_CONTENT_CAT_KEY, "0", cacheString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

    private List getItemCatList(Long parentId) {
        //根据parentId查询列表
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        List resultList = new ArrayList();
        int index = 0;
        for (TbItemCat tbItemCat : list) {
            if (index >= 14)
                break;
            //如果是父节点
            if (tbItemCat.getIsParent()) {
                CatNode node = new CatNode();
                node.setUrl("/products/" + tbItemCat.getId() + ".html");
                //一级节点
                if (tbItemCat.getParentId() == 0) {
                    node.setName("<a href='/products/" + tbItemCat.getId() + ".html'>" + tbItemCat.getName() + "</a>");
                    //一级节点不超过14个
                    index++;
                } else
                    node.setName(tbItemCat.getName());
                node.setItems(getItemCatList(tbItemCat.getId()));
                resultList.add(node);
            } else {
                String item = "/products/"+tbItemCat.getId()+".html|"+tbItemCat.getName();
                resultList.add(item);
            }
        }
        return resultList;
    }
}
