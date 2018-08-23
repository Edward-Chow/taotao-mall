package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/content")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @RequestMapping("/query/list")
    @ResponseBody
    public EasyUIDataGridResult queryContentList(Long categoryId, int page, int rows) {
        return contentService.queryContentList(categoryId, page, rows);
    }

    @RequestMapping("/save")
    @ResponseBody
    public TaotaoResult insertContent(TbContent content) {
        return contentService.insertContent(content);
    }

    @RequestMapping("/edit")
    @ResponseBody
    public TaotaoResult updateContent(TbContent tbContent) {
        return  contentService.updateContent(tbContent);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public TaotaoResult deleteContent(@RequestParam(value="ids")List<Long> ids) {
        return contentService.deleteContent(ids);
    }
}
