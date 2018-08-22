package com.taotao.rest.service.impl;

import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.ItemCatResult;
import com.taotao.rest.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Override
    public ItemCatResult getItemCatService() {
        List catList = getItemCatList(0l);
        ItemCatResult result = new ItemCatResult();
        result.setData(catList);
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
