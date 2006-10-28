/*
 * Copyright (c) 2006 Oliver Stewart.  All Rights Reserved.
 *
 * This file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.trailmagic.image.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import java.util.List;

import com.trailmagic.user.User;
import com.trailmagic.image.*;

@SuppressWarnings("unchecked") // for query.list()
public class HibernateImageGroupFactory implements ImageGroupFactory {
    private static final String ALBUM_BY_OWNER_AND_NAME_QRY =
        "albumByOwnerAndName";
    private static final String IMGFRAME_BY_IMG_GROUP_AND_IMAGE_QRY =
        "imageFrameByImageGroupAndImageId";
    private static final String ALBUMS_BY_OWNER_NAME_QRY =
        "albumsByOwnerScreenName";
    private static final String ALBUM_OWNERS_QRY =
        "albumOwners";
    private static final String ROLLS_BY_OWNER_NAME_QRY =
        "rollsByOwnerScreenName";
    private static final String ROLL_OWNERS_QRY =
        "rollOwners";
    private static final String GROUP_OWNERS_QRY =
        "groupOwnersByType";
    private static final String GROUPS_BY_OWNER_NAME_QRY =
        "groupsByOwnerScreenNameAndType";
    private static final String GROUP_BY_OWNER_NAME_TYPE_QRY =
        "groupByOwnerGroupNameAndType";
    private static final String ROLL_BY_OWNER_AND_NAME_QRY =
        "rollByOwnerAndName";
    private static final String GROUPS_BY_IMAGE_QRY =
        "groupsByImage";
    private static final String ROLL_FOR_IMAGE_QRY =
        "rollForImage";
    private static final String FRAMES_CONTAINING_IMAGE_QRY =
        "framesContainingImage";
    private static final String ALL_GROUPS_QUERY =
        "allImageGroups";

    private SessionFactory m_sessionFactory;
    private HibernateTemplate m_hibernateTemplate;

    public SessionFactory getSessionFactory() {
        return m_sessionFactory;
    }

    public void setSessionFactory(SessionFactory sf) {
        m_sessionFactory = sf;
    }

    public void setHibernateTemplate(HibernateTemplate template) {
        m_hibernateTemplate = template;
    }

    public ImageGroup newInstance(int type) {
        return new ImageGroup();
    }

    public ImageGroup getAlbumByOwnerAndName(User owner, String albumName) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUM_BY_OWNER_AND_NAME_QRY);
            qry.setEntity("owner", owner);
            qry.setString("albumName", albumName);
            return (ImageGroup)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }


    public ImageGroup getRollByOwnerAndName(User owner, String rollName) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_BY_OWNER_AND_NAME_QRY);
            qry.setEntity("owner", owner);
            qry.setString("rollName", rollName);
            return (ImageGroup)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageFrame getImageFrameByImageGroupAndImageId(ImageGroup group,
                                                          long imageId) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry =
                session.getNamedQuery(IMGFRAME_BY_IMG_GROUP_AND_IMAGE_QRY);
            qry.setEntity("imageGroup", group);
            qry.setLong("imageId", imageId);
            return (ImageFrame)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageFrame> getFramesContainingImage(Image image) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry =
                session.getNamedQuery(FRAMES_CONTAINING_IMAGE_QRY);
            qry.setEntity("image", image);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getAlbumsByOwnerScreenName(String screenName) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUMS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", screenName);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<User> getAlbumOwners() {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ALBUM_OWNERS_QRY);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getRollsByOwnerScreenName(String screenName) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLLS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", screenName);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }
    public List<User> getRollOwners() {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_OWNERS_QRY);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<User> getOwnersByType(String groupType) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(GROUP_OWNERS_QRY);
            qry.setString("groupType", groupType);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getByOwnerScreenNameAndType(String screenName,
                                                        String groupType) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(GROUPS_BY_OWNER_NAME_QRY);
            qry.setString("screenName", screenName);
            qry.setString("groupType", groupType);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getByOwnerNameAndType(User owner, String groupName,
                                            String groupType) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(GROUP_BY_OWNER_NAME_TYPE_QRY);
            qry.setEntity("owner", owner);
            qry.setString("groupName", groupName);
            qry.setString("groupType", groupType);
            return (ImageGroup)qry.uniqueResult();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getByImage(Image image) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(GROUPS_BY_IMAGE_QRY);
            qry.setEntity("image", image);
            return qry.list();
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getRollForImage(Image image) {
        // images should always only be in one roll
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);
            Query qry = session.getNamedQuery(ROLL_FOR_IMAGE_QRY);
            qry.setEntity("image", image);
            List results = qry.list();
            if (results.size() < 1) {
                return null;
            }

            return (ImageGroup) results.get(0);
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public ImageGroup getById(long id) {
        try {
            Session session =
                SessionFactoryUtils.getSession(m_sessionFactory, false);

            return (ImageGroup)session.get(ImageGroup.class, new Long(id));
        } catch (HibernateException e) {
            throw SessionFactoryUtils.convertHibernateAccessException(e);
        }
    }

    public List<ImageGroup> getAll() {
        return (List<ImageGroup>)
            m_hibernateTemplate.execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        Query qry = session.getNamedQuery(ALL_GROUPS_QUERY);
                        return qry.list();
                    }
                });
    }
}
