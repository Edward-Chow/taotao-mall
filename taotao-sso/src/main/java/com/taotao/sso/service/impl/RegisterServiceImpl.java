package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.service.RegisterService;
import javafx.scene.shape.Circle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.chrono.ThaiBuddhistEra;
import java.util.Date;
import java.util.List;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private TbUserMapper userMapper;

    @Override
    public TaotaoResult checkData(String param, int type) {
        //根据数据类型检查数据
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        //1，2，3分别代表username, phone, email
        if (type == 1)
            criteria.andUsernameEqualTo(param);
        else if (type == 2)
            criteria.andPhoneEqualTo(param);
        else if (type == 3)
            criteria.andEmailEqualTo(param);
        List<TbUser> list =  userMapper.selectByExample(example);
        //查询到结果返回false 查不到返回true
        if (list == null || list.isEmpty())
            return TaotaoResult.ok(true);
        return TaotaoResult.ok(false);
    }

    @Override
    public TaotaoResult register(TbUser tbUser) {
        //校验数据
        //用户名密码不为空
        if (StringUtils.isBlank(tbUser.getUsername()) || StringUtils.isBlank(tbUser.getPassword()))
            return TaotaoResult.build(400, "用户名或密码不能为空");
        //校验数据是否重复
        TaotaoResult result = checkData(tbUser.getUsername(), 1);
        if (!(boolean)result.getData()) {
            return TaotaoResult.build(400, "该用户名已被注册");
        }
        if (tbUser.getPhone() != null) {
            result = checkData(tbUser.getPhone(), 2);
            if (!(boolean)result.getData())
                return TaotaoResult.build(400, "该手机号已被注册");
        }
        if (tbUser.getEmail() != null) {
            result = checkData(tbUser.getEmail(), 3);
            if (!(boolean)result.getData())
                return TaotaoResult.build(400, "该邮箱已被注册");
        }
        //插入数据
        tbUser.setCreated(new Date());
        tbUser.setUpdated(new Date());
        //对密码进行md5加密
        tbUser.setPassword(DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes()));
        userMapper.insert(tbUser);
        return TaotaoResult.ok();
    }
}
