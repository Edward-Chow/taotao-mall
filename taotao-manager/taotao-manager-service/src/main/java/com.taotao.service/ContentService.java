package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

import java.util.List;

public interface ContentService {
    EasyUIDataGridResult queryContentList(Long categoryId, int page, int rows);
    TaotaoResult insertContent(TbContent content);
    TaotaoResult updateContent(TbContent tbContent);
    TaotaoResult deleteContent(List<Long> ids);
}
