package com.mindalliance.channels.db.data;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract class for all persisted data (as MongoDB documents).
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/25/13
 * Time: 1:56 PM
 */

public class AbstractChannelsDocument implements ChannelsDocument {
    /**
     * Simple date format.
     */
    private static String DATE_FORMAT_STRING = "M/d/yyyy HH:mm";

    @Id
    protected String uid;
    private Date created;
    @Indexed
    private String username;
    private List<DocumentModification> modifications;
    private Date lastModified;
    @Indexed
    private String communityUri;
    @Indexed
    private String planUri;
    private int planVersion;
    private String classLabel;


    public AbstractChannelsDocument() {
        uid = new ObjectId().toString();
        classLabel = this.getClass().getSimpleName();
        created = new Date( );
    }

    public AbstractChannelsDocument( String username ) {
        this(null , "", 0, username );
    }

    public AbstractChannelsDocument( PlanCommunity planCommunity, ChannelsUser user ) {
        this( planCommunity.getUri(),
                planCommunity.getPlanUri(),
                planCommunity.getPlanVersion(),
                user.getUsername() );
    }

    public AbstractChannelsDocument( PlanCommunity planCommunity, String username ) {
        this( planCommunity.getUri(),
                planCommunity.getPlanUri(),
                planCommunity.getPlanVersion(),
                username );
    }

    public AbstractChannelsDocument( String communityUri, String planUri, int planVersion, String username ) {
        this();
        modifications = new ArrayList<DocumentModification>();
        this.username = username;
        setCommunityUri( communityUri );
        setPlanUri( planUri );
        this.planVersion = planVersion;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public void setPlanUri( String planUri ) {
        this.planUri = StringUtils.abbreviate( planUri, 2000 );
    }

    public void setCommunityUri( String communityUri ) {
        this.communityUri = StringUtils.abbreviate( communityUri, 2000 );
    }

    public String getCommunityUri() {
        return communityUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid( String uid ) {
        this.uid = uid;
    }

    public String toString() {
        return getTypeName() + "[" + getUid() + "] created " +  getFormattedCreated();
    }

    protected String getFormattedCreated() {
        return new SimpleDateFormat( DATE_FORMAT_STRING ).format( getCreated() );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPlanUri() {
        return planUri;
    }

    public int getPlanVersion() {
        return planVersion;
    }

    public void setPlanVersion( int planVersion ) {
        this.planVersion = planVersion;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified( Date date ) {
        lastModified = date;
    }

    public void recordModifiedBy( String username ) {
        lastModified = new Date();
        modifications.add( new DocumentModification( lastModified, username ) );
    }

    // Identifiable

    public long getId() {
        return uid == null ? new ObjectId().hashCode() : uid.hashCode();
    }

    public String getDescription() {
        return "";
    }

    public String getTypeName() {
        return classLabel;
    }

    public boolean isModifiableInProduction() {
        return true;
    }

    public String getClassLabel() {
        return classLabel;
    }

    public String getName() {
        return getTypeName() + "[" + getUid() + "]";
    }

    @Override
    public boolean equals( Object obj ) {
        return obj != null
                && obj.getClass().isAssignableFrom( getClass() )
                && getClass().isAssignableFrom( obj.getClass() )
                && getUid().equals( ( (AbstractChannelsDocument) obj ).getUid() );
    }

    @Override
    public int hashCode() {
        return getUid().hashCode();
    }

    public boolean isPersisted() {
        return getUid() != null;
    }
}
