package com.mindalliance.channels.core.orm.model;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.text.SimpleDateFormat;
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

    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "M/d/yyyy HH:mm" );


    @Id @GeneratedValue(strategy = GenerationType.TABLE)
    protected long id;
    private Date created;
    private String username;
    private Date lastModified;
    @Column(length=1000)
    private String planUri;
    private int planVersion;


    public AbstractPersistentPlanObject() {
    }

    public AbstractPersistentPlanObject( String username ) {
        this(null , 0, username );
    }

    public AbstractPersistentPlanObject( Plan plan, ChannelsUser user ) {
        this( plan.getUri(), plan.getVersion(), user.getUsername() );
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
        return getTypeName() + "[" + getId() + "] created " +  getFormattedCreated();
    }

    protected String getFormattedCreated() {
        return dateFormat.format( getCreated() );
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
    public String getClassLabel() {
        return getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return getTypeName() + "[" + getId() + "]";
    }

    @Override
    public boolean equals( Object obj ) {
        return obj.getClass().isAssignableFrom( getClass() )
                && getClass().isAssignableFrom( obj.getClass() )
                && getId() == ((AbstractPersistentPlanObject)obj).getId();
    }

    @Override
    public int hashCode() {
        return new Long( getId() ).hashCode();
    }

    public boolean isPersisted() {
        return getId() != 0;
    }
}
