package com.taotao.controller;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/content/category")
public class ContentCategoryController {
    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(value = "id", defaultValue = "0") Long id) {
        return contentCategoryService.getContentCatList(id);
    }

    @RequestMapping("/create")
    @ResponseBody
    public TaotaoResult createNode(Long parentId, String name){
        return contentCategoryService.insertCategory(parentId, name);
    }

    @RequestMapping("/update")
    @ResponseBody
    public TaotaoResult updateNode(Long id, String name) {
        return contentCategoryService.updateCategory(id, name);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public TaotaoResult deleteNode(Long id) {
        return contentCategoryService.deleteCategory(id);
    }

}
