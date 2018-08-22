package com.taotao.rest.controller;


import com.taotao.common.utils.JsonUtils;
import com.taotao.rest.pojo.ItemCatResult;
import com.taotao.rest.service.ItemCatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/item/cat")
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;

    //请求做json处理而不是html
    //也可使用springmvc4.1以上版本中提供的mappingJacksonValue实现
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE+";charset=utf-8")
    @ResponseBody
    public String getItemCatList(String callback) {
       ItemCatResult result = itemCatService.getItemCatService();
        if (StringUtils.isBlank(callback)) {
            //result转字符串
            String json = JsonUtils.objectToJson(result);
            return json;
        }
        //字符串非空 jsonp调用
        String json = JsonUtils.objectToJson(result);
        return "category.getDataService" + "(" + json + ");";
    }
}
