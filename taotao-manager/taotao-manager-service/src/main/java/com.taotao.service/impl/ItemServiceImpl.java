package com.taotao.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;
    @Override
    public TbItem getItemById(Long itemId) {
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        return tbItem;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        //分页处理
        PageHelper.startPage(page, rows);
        //执行查询
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> list = tbItemMapper.selectByExample(tbItemExample);
        //分页信息
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult createItem(TbItem item, String desc, String itemParam) {
        long itemId = IDUtils.genItemId();
        item.setId(itemId);
        //1：正常，2：下架，3：删除
        item.setStatus((byte)1);
        Date date = new Date();
        item.setCreated(date);
        item.setUpdated(date);
        tbItemMapper.insert(item);
        TbItemDesc desc1 = new TbItemDesc();
        desc1.setItemId(itemId);
        desc1.setItemDesc(desc);
        desc1.setCreated(date);
        desc1.setUpdated(date);
        itemDescMapper.insert(desc1);
        TbItemParamItem paramItem = new TbItemParamItem();
        paramItem.setItemId(itemId);
        paramItem.setParamData(itemParam);
        paramItem.setCreated(new Date());
        paramItem.setUpdated(new Date());
        itemParamItemMapper.insert(paramItem);
        return TaotaoResult.ok();
    }

    @Override
    public String getItemParamHtml(Long itemId) {
        //根据商品id查询规格参数
        TbItemParamItemExample example  = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        //执行查询
        List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
        if (list == null || list.isEmpty()) {
            return "";
        }
        //取规格参数
        TbItemParamItem itemParamItem = list.get(0);
        //取json数据
        String paramData = itemParamItem.getParamData();
        //转换成java对象
        List<Map> mapList = JsonUtils.jsonToList(paramData, Map.class);
        StringBuffer sb = new StringBuffer();
        sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"1\" class=\"Ptable\">\n");
        sb.append(" <tbody>\n");
        for (Map map : mapList) {
            sb.append("     <tr>\n");
            sb.append("         <th class=\"tdTitle\" colspan=\"2\">"+map.get("group")+"</th>\n");
            sb.append("     </tr>\n");
            List<Map> mapList1 = (List<Map>) map.get("params");
            for (Map map1 : mapList1) {
                sb.append("     <tr>\n");
                sb.append("         <td class=\"tdTitle\">"+map1.get("k")+"</td>\n");
                sb.append("         <td>"+map1.get("v")+"</tb>\n");
                sb.append("     </tr>\n");
            }
        }
        sb.append(" </tbody>\n");
        sb.append("</table>");
        return sb.toString();
    }
}
