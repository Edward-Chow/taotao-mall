package com.taotao.service.impl;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;
    @Override
    public List<EasyUITreeNode> getContentCatList(Long parentId) {
        //根据parentId查询子节点列表
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //执行查询
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        //转换
        List<EasyUITreeNode> resultList = new ArrayList<>();
        for (TbContentCategory t: list) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(t.getId());
            node.setText(t.getName());
            node.setState(t.getIsParent() ? "closed" : "open");
            //添加到结果列表
            resultList.add(node);
        }
        return resultList;
    }

    @Override
    public TaotaoResult insertCategory(Long parentId, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setParentId(parentId);
        contentCategory.setName(name);
        //1正常 2删除
        contentCategory.setStatus(1);
        contentCategory.setIsParent(false);
        //排列序号
        contentCategory.setSortOrder(1);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //插入数据
        contentCategoryMapper.insert(contentCategory);
        //判断是否是父节点
        TbContentCategory parentCat  = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parentCat.getIsParent()) {
            parentCat.setIsParent(true);
            contentCategoryMapper.updateByPrimaryKey(parentCat);
        }
        //取返回主键
        Long id = contentCategory.getId();
        return TaotaoResult.ok(id);
    }

    @Override
    public TaotaoResult updateCategory(Long id, String name) {
        TbContentCategory contentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        //判断新的name值与原来的值是否相同，如果相同则不用更新
        if(name != null && name.equals(contentCategory.getName())){
            return TaotaoResult.ok();
        }
        contentCategory.setUpdated(new Date());
        contentCategory.setName(name);
        contentCategoryMapper.updateByPrimaryKey(contentCategory);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteCategory(Long id) {
        deleteCategoryAndChildNode(id);
        return TaotaoResult.ok();
    }

    private List<TbContentCategory> getChildNodeList(Long id) {
        //查询所有id=id的父节点
        TbContentCategoryExample contentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        //返回结果List
        return contentCategoryMapper.selectByExample(contentCategoryExample);
    }

    //递归删除
    private void deleteCategoryAndChildNode(Long id) {
        TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        //判断该节点下是否还有子节点 若有 递归删除
        if (tbContentCategory.getIsParent()) {
            List<TbContentCategory> list = getChildNodeList(id);
            for (TbContentCategory category :list) {
                deleteCategoryAndChildNode(category.getId());
            }
        }
        //判断父节点下是否还有其他节点
        if (getChildNodeList(tbContentCategory.getParentId()).size() == 1) {
            TbContentCategory parentContentCategory = contentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
            parentContentCategory.setIsParent(false);
            contentCategoryMapper.updateByPrimaryKey(parentContentCategory);
        }
        //删除节点
        contentCategoryMapper.deleteByPrimaryKey(id);
        return;
    }
}
