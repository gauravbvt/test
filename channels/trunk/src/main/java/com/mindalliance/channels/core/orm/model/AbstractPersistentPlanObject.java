package com.mindalliance.channels.core.orm.model;

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
public abstract class AbstractPersistentPlanObject implements PersistentPlanObject {

    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    protected long id;
    private Date created;
    private String username;
    private Date lastModified;
    private String planUri;
    private int planVersion;


    public AbstractPersistentPlanObject() {
    }

    public AbstractPersistentPlanObject( String username ) {
        this(null , 0, username );
    }

    
    public AbstractPersistentPlanObject( String uri, int version, String username ) {
        created = new Date( );
        this.username = username;
        planUri = uri;
        planVersion = version;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public void setPlanUri( String planUri ) {
        this.planUri = planUri;
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

    public int getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion( int planVersion ) {
        this.planVersion = planVersion;
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified( Date date ) {
        lastModified = date;
    }

    // Identifiable

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getTypeName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean isModifiableInProduction() {
        return true;
    }

    @Override
    public String getName() {
        return getTypeName() + "[" + getId() + "]";
    }
}
