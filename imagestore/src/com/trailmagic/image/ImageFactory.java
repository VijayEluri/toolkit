package com.trailmagic.image;

import java.util.List;

public interface ImageFactory {

    public Image newInstance();
    public Image getById(long id);
    public List getAll();
    public List getByName(String name);
    public List getByNameAndGroup(String name, ImageGroup group);
}
