package com.mindalliance.channels.core.orm.model;

import com.mindalliance.channels.core.dao.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.text.DateFormat;
import java.util.Date;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/2/12
 * Time: 11:20 AM
 */
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
@Entity
public class AbstractPersistentPlanObject implements PersistentPlanObject {

    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    private Date created;
    private String username;
    private Date lastModified;
    private String planUri;


    public AbstractPersistentPlanObject() {
        created = new Date();
        User user = User.current();
        username = user.getUsername();
        planUri = user.getPlanUri();
    }
    
    public AbstractPersistentPlanObject( String uri, String username ) {
        created = new Date( );
        this.username = username;
        planUri = uri;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public long getId() {
        return id;
    }

    public String toString() {
        return "at " + DateFormat.getInstance().format( getCreated() );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    @Override
    public String getPlanUri() {
        return planUri;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified( Date date ) {
        lastModified = date;
    }



}
