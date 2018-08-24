package com.taotao.controller;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/content")
public class ContentController {
    @Value("${REST_BASE_RUL}")
    private String REST_BASE_RUL;
    @Value("${REST_CONTENT_SYNC_URL}")
    private String REST_CONTENT_SYNC_URL;
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
        TaotaoResult result = contentService.insertContent(content);
        //调用taotao-rest发布的服务同步缓存
        HttpClientUtil.doGet(REST_BASE_RUL+REST_CONTENT_SYNC_URL+content.getCategoryId());
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public TaotaoResult updateContent(TbContent tbContent) {
        TaotaoResult result =  contentService.updateContent(tbContent);
        //调用taotao-rest发布的服务同步缓存
        HttpClientUtil.doGet(REST_BASE_RUL+REST_CONTENT_SYNC_URL+tbContent.getCategoryId());
        return result;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public TaotaoResult deleteContent(@RequestParam(value="ids")List<Long> ids) {
        TaotaoResult result = contentService.deleteContent(ids);
        for (Long id : ids) {
            HttpClientUtil.doGet(REST_BASE_RUL + REST_CONTENT_SYNC_URL + ids);
        }
        return result;
    }
}
