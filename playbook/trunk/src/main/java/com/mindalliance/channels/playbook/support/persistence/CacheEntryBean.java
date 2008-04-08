package com.mindalliance.channels.playbook.support.persistence;

import com.opensymphony.oscache.base.CacheEntry;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2008
 * Time: 2:49:31 PM
 */
public class CacheEntryBean {

    static final String CLASS_NAME_KEY = "_bean_class_";

    private static final byte NOT_YET = -1;
    private Object content = null;
    private Set groups = null;
    private String key;
    private long created = NOT_YET;
    private long lastUpdate = NOT_YET;

    public CacheEntryBean() {}

    public CacheEntryBean(CacheEntry cacheEntry) {
        this.key = cacheEntry.getKey();
        this.content = cacheEntry.getContent();
        this.groups = cacheEntry.getGroups();
        this.created = cacheEntry.getCreated();
        this.lastUpdate = cacheEntry.getLastUpdate();
    }

    public Object toCacheEntry() {
        CacheEntry ce = new CacheEntry(this.key);
        ce.setContent(this.content);
        ce.setGroups(this.groups);
        ce.setLastUpdate(this.lastUpdate);
        return ce;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Set getGroups() {
        return groups;
    }

    public void setGroups(Set groups) {
        this.groups = groups;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
