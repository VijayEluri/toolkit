package com.trailmagic.image.impl;

import com.trailmagic.image.*;
import com.trailmagic.image.security.ImageSecurityService;
import com.trailmagic.image.security.SecurityTestHelper;
import com.trailmagic.resizer.ImageFileInfo;
import com.trailmagic.resizer.ImageResizeService;
import com.trailmagic.resizer.ResizeFailedException;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.util.SecurityUtil;
import com.trailmagic.util.TimeSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class ImageServiceImplTest {
    private ImageService imageService;
    @Mock private UserRepository userRepository;
    @Mock private ImageManifestationRepository imageManifestationRepository;
    @Mock private ImageGroupRepository imageGroupRepository;
    @Mock private ImageRepository imageRepository;
    @Mock private ImageSecurityService imageSecurityService;
    @Mock private ImageInitializer imageInitializer;
    @Mock private SecurityUtil securityUtil;
    @Mock private TimeSource timeSource;
    @Mock private ImageResizeService imageResizeService;
    @Mock private HibernateUtil hibernateUtil;
    private static final long DEFAULT_GROUP_ID = 1234L;
    private static final Date TEST_TIME = new Date();
    private ImageGroup defaultGroup;
    private static final String TEST_ROLL_NAME = "my-awesome-roll";
    private final SecurityTestHelper securityTestHelper = new SecurityTestHelper();
    private User testUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testUser = new User("testy");
        when(timeSource.today()).thenReturn(TEST_TIME);
        when(securityUtil.getCurrentUser()).thenReturn(testUser);
        imageService = new ImageServiceImpl(imageGroupRepository, imageRepository, imageSecurityService, userRepository, securityUtil, imageInitializer, timeSource, imageResizeService, hibernateUtil);
    }

    private void withCurrentUser(User currentUser, boolean hasDefaultGroup) {
        if (hasDefaultGroup) {
            defaultGroup = setupDefaultGroup(currentUser);
        }
        when(imageGroupRepository.getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(defaultGroup);
    }


    @Test
    public void testCreateImageWithDefaultGroup() {
        User currentUser = testUser;
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);

        assertEquals(currentUser, image.getOwner());
        assertNotNull(image.getId());
        assertEquals(defaultGroup, image.getRoll());

        verify(imageGroupRepository).getRollByOwnerAndName(currentUser, ImageGroup.DEFAULT_ROLL_NAME);
        verify(imageInitializer, Mockito.times(1)).saveNewImage(Mockito.any(Photo.class));
    }


    @Test
    public void testLooksUpRoll() {
        User currentUser = testUser;
        withCurrentUser(currentUser, true);
        ImageMetadata imageMetadata = setupImageMetadata();
        imageMetadata.setRollName(TEST_ROLL_NAME);
        final ImageGroup existingRoll = new ImageGroup(TEST_ROLL_NAME, currentUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(currentUser, TEST_ROLL_NAME)).thenReturn(existingRoll);

        final Photo image = imageService.createImage(imageMetadata);

        assertEquals(existingRoll, image.getRoll());
    }


    @Test
    public void testCreatesNewDefaultGroup() {
        User currentUser = testUser;
        withCurrentUser(currentUser, false);
        ImageMetadata imageMetadata = setupImageMetadata();

        final Photo image = imageService.createImage(imageMetadata);
        final ImageGroup assignedGroup = image.getRoll();
        assertNotNull(assignedGroup);
        assertEquals(ImageGroup.DEFAULT_ROLL_NAME, assignedGroup.getName());
        assertEquals(ImageGroup.Type.ROLL, assignedGroup.getType());
    }

    @Test
    public void testSavesImageMetaData() throws IOException, SQLException, ResizeFailedException {
        securityTestHelper.disableSecurityInterceptor();
        withCurrentUser(testUser, true);

        ImageMetadata imageMetadata = setupImageMetadata();

        File resizeTempFile = mock(File.class);
        when(resizeTempFile.delete()).thenReturn(true);
        ImageFileInfo fileInfo = testFileInfo(resizeTempFile);

        File srcFile = mock(File.class);
        when(srcFile.delete()).thenReturn(true);
        when(imageResizeService.writeFile(Mockito.<InputStream>any())).thenReturn(srcFile);
        when(imageResizeService.scheduleResize(srcFile)).thenReturn(Arrays.asList(fileInfo));
        Blob data = mock(Blob.class);
        when(hibernateUtil.toBlob(srcFile)).thenReturn(data);
        final Photo photo = imageService.createImage(imageMetadata, new ByteArrayInputStream(new byte[]{}), "image/jpeg");

        assertEquals(2, photo.getManifestations().size());
        final HeavyImageManifestation mf = (HeavyImageManifestation) photo.getManifestations().first();
        assertEquals("image/jpeg", mf.getFormat());
        assertEquals(photo, mf.getImage());
        assertEquals(data, mf.getData());

        verify(imageInitializer).saveNewImage(photo);
        verify(imageInitializer).saveNewImageManifestation((HeavyImageManifestation) photo.getManifestations().first(), false);
        verify(imageResizeService).scheduleResize(srcFile);
        verify(imageInitializer).saveNewImageManifestation((HeavyImageManifestation) photo.getManifestations().last(), false);
        verify(srcFile).delete();
        verify(resizeTempFile).delete();
    }

    private ImageFileInfo testFileInfo(File file) {
        ImageFileInfo fileInfo = new ImageFileInfo(128, 128, "image/jpeg");
        fileInfo.setFile(file);
        return fileInfo;
    }

    @Test
    public void testFindOrCreateRollCreatesDefaultRoll() {
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(null);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(null, testUser);

        verify(imageInitializer).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(ImageGroup.DEFAULT_ROLL_NAME, roll.getName());
        assertEquals("Uploads", roll.getDisplayName());
        assertEquals("Uploaded Images", roll.getDescription());
        assertEquals(ImageGroup.Type.ROLL, roll.getType());
        assertEquals(testUser, roll.getOwner());
        assertEquals(TEST_TIME, roll.getUploadDate());
        assertNull(roll.getSupergroup());
    }

    @Test
    public void testFindOrCreateRollFindsDefaultRoll() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(null, testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test
    public void testFindOrCreateRollFindsDefaultRollWithEmptyString() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, ImageGroup.DEFAULT_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll("", testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test
    public void testFindOrCreateRollFindsNamedRoll() {
        ImageGroup expectedGroup = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.getRollByOwnerAndName(testUser, TEST_ROLL_NAME)).thenReturn(expectedGroup);

        ImageGroup roll = imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser);

        verify(imageInitializer, never()).saveNewImageGroup(any(ImageGroup.class));

        assertEquals(expectedGroup, roll);
    }

    @Test(expected = ImageGroupNotFoundException.class)
    public void testFindOrCreateRollThrowsExceptionForMissingNamedRoll() {
        when(imageGroupRepository.getRollByOwnerAndName(testUser, "non-existent roll")).thenReturn(null);

        imageService.findNamedOrDefaultRoll(TEST_ROLL_NAME, testUser);
    }

    @Test
    public void testAppendImageToGroup() {
        ImageGroup group = new ImageGroup("test", testUser, ImageGroup.Type.ROLL);
        when(imageGroupRepository.findMaxPosition(group)).thenReturn(5);

        Photo image = new Photo("test", testUser);
        ImageFrame frame = imageService.addImageToGroup(image, group);

        assertEquals(6, frame.getPosition());
        assertEquals(group, frame.getImageGroup());
        assertEquals(image, frame.getImage());
        assertEquals(testUser, frame.getOwner());

        verify(imageGroupRepository).saveGroup(group);
    }

    private ImageGroup setupDefaultGroup(User currentUser) {
        final ImageGroup defaultGroup = new ImageGroup(ImageGroup.DEFAULT_ROLL_NAME, currentUser, ImageGroup.Type.ROLL);
        defaultGroup.setId(DEFAULT_GROUP_ID);
        return defaultGroup;
    }

    private ImageMetadata setupImageMetadata() {
        ImageMetadata imageMetadata = new ImageMetadata();
        imageMetadata.setShortName("testImage");
        imageMetadata.setDisplayName("Test Image");
        imageMetadata.setCaption("This is a test image.");
        imageMetadata.setCopyright("Copyright 2009");
        imageMetadata.setCreator("Oliver Stewart");
        return imageMetadata;
    }
}
