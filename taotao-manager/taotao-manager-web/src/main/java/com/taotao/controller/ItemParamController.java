package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.service.ItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/item/param")
public class ItemParamController {
    @Autowired
    private ItemParamService itemParamService;

    @RequestMapping("/list")
    @ResponseBody
    public EasyUIDataGridResult getItemParamList(Integer page, Integer rows) {
        EasyUIDataGridResult result = itemParamService.getItemParamList(page, rows);
        return result;
    }

    @RequestMapping("/query/itemcatid/{cid}")
    @ResponseBody
    public TaotaoResult getItemCatByCid(@PathVariable Long cid) {
        return itemParamService.getItemParamByCid(cid);
    }

    @RequestMapping("/save/{cid}")
    @ResponseBody
    public TaotaoResult insertItemParam(@PathVariable Long cid, String paramData) {
        return itemParamService.insertItemParam(cid, paramData);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public TaotaoResult deleteItemParam(@RequestParam("ids") List<Long> ids){
        //测试下传输过来的参数是否正确
        for (int i = 0; i < ids.size(); i++) {
            System.out.println(ids.get(i));
        }
        System.out.println(ids.size());
        return itemParamService.deleteItemParam(ids);
    }


}
