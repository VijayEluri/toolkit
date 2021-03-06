package com.trailmagic.image.impl;

import com.trailmagic.image.HeavyImageManifestation;
import com.trailmagic.image.Image;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageGroupRepository;
import com.trailmagic.image.ImageManifestationRepository;
import com.trailmagic.image.ImageRepository;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
@Service("imageInitializer")
public class ImageInitializerImpl implements ImageInitializer {
    private ImageGroupRepository imageGroupRepository;
    private ImageRepository imageRepository;
    private ImageSecurityService imageSecurityService;
    private ImageManifestationRepository imageManifestationRepository;
    private SecurityUtil securityUtil;

    private static Logger log = LoggerFactory.getLogger(ImageInitializerImpl.class);

    @Autowired
    public ImageInitializerImpl(ImageGroupRepository imageGroupRepository,
                            ImageRepository imageRepository,
                            ImageSecurityService imageSecurityService,
                            ImageManifestationRepository imageManifestationRepository,
                            SecurityUtil securityUtil) {
        this.imageGroupRepository = imageGroupRepository;
        this.imageRepository = imageRepository;
        this.imageSecurityService = imageSecurityService;
        this.imageManifestationRepository = imageManifestationRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public void saveNewImage(Image image) {
        log.info("Saving image: " + image);
        if (image.getOwner() == null) {
            if (securityUtil.getCurrentUser() != null) {
                image.setOwner(securityUtil.getCurrentUser());
            } else {
                throw new IllegalStateException("Can't save an image with no owner");
            }
        }

        imageRepository.saveNew(image);
        imageSecurityService.addOwnerAcl(image);
    }

    @Override
    public void saveNewImageGroup(ImageGroup imageGroup) throws IllegalStateException {
        log.info("Saving image group: " + imageGroup);
        if (imageGroup.getOwner() == null) {
            if (securityUtil.getCurrentUser() != null) {
                imageGroup.setOwner(securityUtil.getCurrentUser());
            } else {
                throw new IllegalStateException("Can't save a roll with no owner");
            }
        }

        if (imageGroup.getPreviewImage() == null
            && imageGroup.getFrames() != null
            && imageGroup.getFrames().size() > 0) {
            imageGroup.setPreviewImage(imageGroup.getFrames().first().getImage());
            if (log.isDebugEnabled()) {
                log.debug("Set missing preview image to first image on group: "
                          + imageGroup.getName());
            }
        }

        imageGroupRepository.saveNewGroup(imageGroup);
        imageSecurityService.addOwnerAcl(imageGroup);
    }

    @Override
    public void saveNewImageManifestation(HeavyImageManifestation imageManifestation) {
        // for now we'll clear/evict here since the normal case is probably
        // that we want this out of memory quickly
        saveNewImageManifestation(imageManifestation, true);
    }

    @Override
    public void saveNewImageManifestation(HeavyImageManifestation imageManifestation, boolean clearFromSession) {
        imageManifestationRepository.saveNewImageManifestation(imageManifestation);

        log.info("Saved image manifestation"
                 + (clearFromSession ? " (before flush/evict)" : "")
                 + ": " + imageManifestation);
        if (clearFromSession) {
            imageManifestationRepository.cleanFromSession(imageManifestation);
        }
    }
}