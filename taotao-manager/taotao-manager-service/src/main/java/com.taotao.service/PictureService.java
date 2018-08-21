package com.taotao.service;

import com.taotao.common.pojo.PictureResult;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService {
    PictureResult uploadPic(MultipartFile picFile);
}
