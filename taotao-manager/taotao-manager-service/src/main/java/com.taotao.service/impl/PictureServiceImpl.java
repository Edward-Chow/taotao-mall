package com.taotao.service.impl;

import com.taotao.common.pojo.PictureResult;
import com.taotao.common.utils.FastDFSClient;
import com.taotao.service.PictureService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureServiceImpl implements PictureService{
    @Override
    public PictureResult uploadPic(MultipartFile picFile) {
        PictureResult result = new PictureResult();
        if (picFile.isEmpty()) {
            result.setError(1);
            result.setMessage("图片为空！");
            return result;
        }
        //不为空上传到图片服务器
        try {
            String originalFileName = picFile.getOriginalFilename();
            String extName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            String confUrl=this.getClass().getClassLoader().getResource("/fdfs_client.properties").getPath();
            FastDFSClient fastDFSClient=new FastDFSClient(confUrl);
            String url = fastDFSClient.uploadFile(picFile.getBytes(), extName);
            result.setError(0);
            result.setUrl(url);
        } catch (Exception e) {
            result.setError(1);
            result.setMessage("图片上传失败！");
            e.printStackTrace();
        }
        return null;
    }
}
