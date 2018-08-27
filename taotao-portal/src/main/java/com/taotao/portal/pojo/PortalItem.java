package com.taotao.portal.pojo;

import com.taotao.pojo.TbItem;

public class PortalItem extends TbItem {
    public String[] getImages() {
        String image = this.getImage();
        if(this.getImage() != null && !this.getImage().equals("")) {
            String[] images = image.split(",");
            return images;
        }
        return null;
    }
}
