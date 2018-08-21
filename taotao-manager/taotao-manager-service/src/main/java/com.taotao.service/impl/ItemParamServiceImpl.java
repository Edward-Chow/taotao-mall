package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemParamExMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamExample;
import com.taotao.pojo.TbItemParamModel;
import com.taotao.service.ItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ItemParamServiceImpl implements ItemParamService{
    @Autowired
    private TbItemParamMapper tbItemParamMapper;
    @Autowired
    private TbItemParamExMapper tbItemParamExMapper;
    @Override
    public EasyUIDataGridResult getItemParamList(int page, int rows) {
        //分页处理
        PageHelper.startPage(page, rows);
        //执行查询
        List<TbItemParamModel> list = tbItemParamExMapper.selectItemParamList();
        //分页信息
        PageInfo<TbItemParamModel> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult getItemParamByCid(Long cid) {
        //根据cid查询的规格参数模板
        TbItemParamExample example = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = example.createCriteria();
        criteria.andItemCatIdEqualTo(cid);
        //执行查询
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
        //判断是否查询到结果
        if (list != null && list.size() > 0) {
            TbItemParam itemParam = list.get(0);
            return TaotaoResult.ok(itemParam);
        }
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult insertItemParam(Long cid, String paramData) {
        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setItemCatId(cid);
        tbItemParam.setParamData(paramData);
        tbItemParam.setCreated(new Date());
        tbItemParam.setUpdated(new Date());
        tbItemParamMapper.insert(tbItemParam);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteItemParam(List<Long> ids) {
        int result = tbItemParamMapper.deleteBatch(ids);
        if(result > 0)
            return TaotaoResult.ok(ids);
        return TaotaoResult.ok();
    }
}
