package com.taotao.search.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.ItemMapper;
import com.taotao.search.pojo.SearchItem;
import com.taotao.search.service.ItemService;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private SolrServer solrServer;
    @Autowired
    private ItemMapper itemMapper;

    //商品信息导入solr索引库
    @Override
    public TaotaoResult importItem() throws Exception{
        //查询数据库获取商品列表
        List<SearchItem> list = itemMapper.getItemList();
        //遍历列表
        for (SearchItem item : list) {
            //创建文档对象
            SolrInputDocument document = new SolrInputDocument();
            //添加域
            document.setField("id", item.getId());
            document.setField("item_title", item.getTitle());
            document.setField("item_sell_point", item.getSell_point());
            document.setField("item_price", item.getPrice());
            document.setField("item_image", item.getImage());
            document.setField("item_category_name", item.getCategory_name());
            document.setField("item_desc", item.getItem_desc());
            //写入索引库
            solrServer.add(document);
        }
        //提交
        solrServer.commit();
        return TaotaoResult.ok();
    }
}
