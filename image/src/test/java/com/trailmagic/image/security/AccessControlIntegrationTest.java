package com.trailmagic.image.security;

import com.trailmagic.image.ImageFrame;
import com.trailmagic.image.ImageGroup;
import com.trailmagic.image.ImageService;
import com.trailmagic.image.Photo;
import com.trailmagic.image.impl.ImageInitializer;
import com.trailmagic.user.NoSuchUserException;
import com.trailmagic.user.User;
import com.trailmagic.user.UserRepository;
import com.trailmagic.user.security.ToolkitUserDetails;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-global.xml",
                                   "classpath:com/trailmagic/image/applicationContext-test.xml",
                                   "classpath:applicationContext-user.xml",
                                   "classpath:applicationContext-imagestore.xml",
                                   "classpath:applicationContext-imagestore-authorization.xml"})
@Transactional
public class AccessControlIntegrationTest {
    @Autowired
    private ImageSecurityService service;
    @Autowired
    private ImageService imageService;
    @Autowired
    private UserRepository userRepository;
    private User testUser;
    @Autowired
    private ImageInitializer imageInitializer;

    @Test(expected = AuthenticationException.class)
    public void testCreateDefaultImageFailsWithoutUser() throws IOException {
        setupNoAuthenticatedUser();
        imageService.createDefaultImage("foo.jpg");
    }

    @Test
    public void testCreateDefaultImageSucceedsWithUser() throws IOException {
        setupAuthenticatedUser();
        imageService.createDefaultImage("foo.jpg");
    }

    @Test(expected = AuthenticationException.class)
    public void testCreateImageAtPositionFailsWithoutUser() throws IOException {
        setupNoAuthenticatedUser();
        imageService.createImageAtPosition("foo.jpg", new ByteArrayInputStream(new byte[]{}), 1);
    }

    @Test
    public void testCreateImageAtPositionSucceedsWithUser() throws IOException {
        setupAuthenticatedUser();
        imageService.createImageAtPosition("foo.jpg", new ByteArrayInputStream(new byte[]{}), 1);
    }

    private ImageFrame makeFrame(Photo photo, ImageGroup group, int position) {
        ImageFrame frame = new ImageFrame(photo);
        frame.setPosition(position);
        group.addFrame(frame);
        return frame;
    }

    private ImageGroup makeRoll() {
        ImageGroup group = new ImageGroup("testGroup", testUser, ImageGroup.Type.ROLL);
        group.setDisplayName("test group");
        group.setUploadDate(new Date());
        imageInitializer.saveNewImageGroup(group);
        return group;
    }


    private Photo makePhoto(String name) {
        final Photo photo = new Photo();
        photo.setName(name);
        photo.setOwner(testUser);
        photo.setDisplayName("test display");
        imageInitializer.saveNewImage(photo);
        return photo;
    }

    private User setupAuthenticatedUser() {
        try {
            testUser = userRepository.getByScreenName("test");
            System.out.println("Loaded test user: " + testUser);
        } catch (NoSuchUserException e) {
            testUser = new User("test");
            testUser.setFirstName("Testy");
            testUser.setLastName("McTesterton");
            testUser.setPrimaryEmail("test@example.com");
            testUser.setPassword("password");
            userRepository.save(this.testUser);
            System.out.println("Saved new user:" + testUser);
        }

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(new ToolkitUserDetails(this.testUser), "password",
                                                        Arrays.asList(new GrantedAuthorityImpl("ROLE_EVERYONE")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return this.testUser;
    }

    private void setupNoAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
