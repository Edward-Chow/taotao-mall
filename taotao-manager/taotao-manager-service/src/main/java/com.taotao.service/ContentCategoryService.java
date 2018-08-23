package com.taotao.service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

import java.util.List;

public interface ContentCategoryService {
    List<EasyUITreeNode> getContentCatList(Long parentId);
    TaotaoResult insertCategory(Long parentId, String name);
    TaotaoResult updateCategory(Long id, String name);
    TaotaoResult deleteCategory(Long id);
}
