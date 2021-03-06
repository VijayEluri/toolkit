package com.trailmagic.image.util;

import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetPreviewImageImpl implements SetPreviewImage {
    private ImageGroupRepository imageGroupRepository;

    private static Logger s_log =
        LoggerFactory.getLogger(SetPreviewImageImpl.class);

    @Required
    public void setImageGroupRepository(ImageGroupRepository imageGroupRepository) {
        this.imageGroupRepository = imageGroupRepository;
    }

    public void setDefaultPreviewImage(ImageGroup group) {
        group.setPreviewImage(group.getFrames().first().getImage());
    }

    public void setPreviewImage(ImageGroup group, Image image) {
        group.setPreviewImage(image);
    }
    
    public void setAllDefault() {
        List<ImageGroup> allGroups = imageGroupRepository.getAll();
        for (ImageGroup group : allGroups) {
            s_log.info("Setting preview image for " + group.getName());
            setDefaultPreviewImage(group);
        }
        
    }
    
    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext =
            new ClassPathXmlApplicationContext(new String[]
                {"applicationContext-global.xml",
                 "applicationContext-user.xml",
                 "applicationContext-imagestore.xml",
                 "applicationContext-imagestore-authorization.xml",
                 "applicationContext-standalone.xml"});

        SetPreviewImage spi =
            (SetPreviewImage) appContext.getBean("setPreviewImage");
        
        spi.setAllDefault();
    }

}
