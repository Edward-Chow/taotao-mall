package com.taotao.portal.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.portal.pojo.PortalItem;
import com.taotao.portal.service.ItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ItemServiceImpl implements ItemService{
    @Value("${REST_BASE_URL}")
    private String REST_BASE_URL;
    @Value("${REST_ITEM_BASE_URL}")
    private String REST_ITEM_BASE_URL;
    @Value("${REST_ITEM_DESC_URL}")
    private String REST_ITEM_DESC_URL;
    @Value("${REST_ITEM_PARAM_URL}")
    private String REST_ITEM_PARAM_URL;

    //根据商品服务查询商品基本信息
    @Override
    public TbItem getItemById(Long itemId) {
        //根据商品服务查询商品id基本信息
        String json = HttpClientUtil.doGet(REST_BASE_URL+REST_ITEM_BASE_URL+itemId);
        //转换成java对象
        TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, PortalItem.class);
        //取商品的对象
        TbItem item = (TbItem) taotaoResult.getData();
        return item;
    }


    @Override
    public String getItemDescById(Long itemId) {
        //根据商品调用taotao-rest服务获取数据
        String json = HttpClientUtil.doGet(REST_BASE_URL+REST_ITEM_DESC_URL+itemId);
        //转换成java对象
        TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemDesc.class);
        //取商品描述信息
        TbItemDesc itemDesc = (TbItemDesc) taotaoResult.getData();
        String desc = itemDesc.getItemDesc();
        return desc;
    }

    @Override
    public String getItemParamById(Long itemId) {
        //根据商品id取规格参数
        String json  = HttpClientUtil.doGet(REST_BASE_URL+REST_ITEM_PARAM_URL+itemId);
        //转换成java对象
        TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItemParamItem.class);
        //取规格参数
        TbItemParamItem itemParamItem = (TbItemParamItem) taotaoResult.getData();
        String paramJson = itemParamItem.getParamData();
        //把规格参数的json数据转换成java对象
        List<Map> paramList = JsonUtils.jsonToList(paramJson, Map.class);
        //遍历list生成html
        StringBuffer sb = new StringBuffer();
        sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"1\" class=\"Ptable\">\n");
        sb.append(" <tbody>\n");
        for (Map map : paramList) {
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
