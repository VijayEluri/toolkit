package com.trailmagic.image;

import java.util.SortedSet;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;

import com.trailmagic.user.User;

public class Image {
    private long m_id;
    private String m_name;
    private String m_displayName;
    private String m_caption;
    private String m_copyright;
    private String m_creator;
    private SortedSet m_manifestations;
    private User m_owner;
    private ImageCD m_imageCD;

    public Image(long id) {
        // nothing for now
        m_id = id;
    }

    public Image() {
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

    public SortedSet getManifestations() {
        return m_manifestations;
    }

    public void setManifestations(SortedSet manifestations) {
        m_manifestations = manifestations;
    }

    public void addManifestation(ImageManifestation im) {
        im.setImage(this);
        m_manifestations.add(im);
    }

    public static Image findById(long id) throws HibernateException {
        Session sess = HibernateUtil.currentSession();
        Query query =
            sess.createQuery("select from com.trailmagic.image.Image " +
                             "as image where image.id = :id");
        query.setLong("id", id);
        return (Image)query.uniqueResult();
    }

    public boolean equals(Object obj) {
        if ( obj == null || !(obj instanceof Image) ) {
            return false;
        }
        return this.m_id == ((Image)obj).m_id;
    }
}
