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
package com.trailmagic.image;

import com.trailmagic.user.Owned;
import com.trailmagic.user.User;
import java.util.SortedSet;
import java.util.TreeSet;
import org.acegisecurity.acl.basic.AclObjectIdentity;
import org.acegisecurity.acl.basic.AclObjectIdentityAware;
import org.acegisecurity.acl.basic.NamedEntityObjectIdentity;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class Image implements Owned, AclObjectIdentityAware {
    private long m_id;
    private String m_name;
    private String m_displayName;
    private String m_caption;
    private String m_copyright;
    private String m_creator;
    private SortedSet<ImageManifestation> m_manifestations;
    private User m_owner;
    private ImageCD m_imageCD;
    private Integer m_number;

    public Image(long id) {
        super();
        // nothing for now
        m_id = id;
    }

    public Image() {
        m_manifestations = new TreeSet<ImageManifestation>();
    }

    public Image(Image image) {
        setName(image.getName());
        setDisplayName(image.getDisplayName());
        setCaption(image.getCaption());
        setCopyright(image.getCopyright());
        setCreator(image.getCreator());
        setOwner(image.getOwner());
        setImageCD(image.getImageCD());
        setManifestations(image.getManifestations());
        setNumber(image.getNumber());
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String name) {
        m_displayName = name;
    }

    public String getCaption() {
        return m_caption;
    }

    public void setCaption(String caption) {
        m_caption = caption;
    }

    public String getCopyright() {
        return m_copyright;
    }

    public void setCopyright(String copyright) {
        m_copyright = copyright;
    }

    public String getCreator() {
        return m_creator;
    }

    public void setCreator(String creator) {
        m_creator = creator;
    }

    public User getOwner() {
        return m_owner;
    }

    public void setOwner(User owner) {
        m_owner = owner;
    }

    public ImageCD getImageCD() {
        return m_imageCD;
    }

    public void setImageCD(ImageCD cd) {
        m_imageCD = cd;
    }

    public SortedSet<ImageManifestation> getManifestations() {
        return m_manifestations;
    }

    public void setManifestations(SortedSet<ImageManifestation>
                                  manifestations) {
        m_manifestations = manifestations;
    }

    public void addManifestation(ImageManifestation im) {
        im.setImage(this);
        m_manifestations.add(im);
    }

    public Integer getNumber() {
        return m_number;
    }

    public void setNumber(Integer number) {
        m_number = number;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Image) &&
            //            (this.getId() == ((Image)obj).getId());
            (this.getName().equals(((Image)obj).getName())) &&
            (this.getOwner().equals(((Image)obj).getOwner()));
    }

    public AclObjectIdentity getAclObjectIdentity() {
        return new NamedEntityObjectIdentity(Image.class.getName(),
                                             Long.toString(getId()));
    }

    public String toString() {
        return getClass().getName() + ": " + getName();
    }

}