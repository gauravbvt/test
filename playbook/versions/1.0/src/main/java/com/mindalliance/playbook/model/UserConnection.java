// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.model;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.net.URL;

/**
 * ORM mapping for Spring Social user connections.
 * Just here to simplify table creation.
 * 
 * 
 create table UserConnection (userId varchar(255) not null,
 providerId varchar(255) not null,
 providerUserId varchar(255),
 rank int not null,
 displayName varchar(255),
 profileUrl varchar(512),
 imageUrl varchar(512),
 accessToken varchar(255) not null,					
 secret varchar(255),
 refreshToken varchar(255),
 expireTime bigint,
 primary key (userId, providerId, providerUserId));
 create unique index UserConnectionRank on UserConnection(userId, providerId, rank);
 */
@Entity
@Table(
    appliesTo = "UserConnection",    
    indexes = { @Index( name = "UserConnectionRank", columnNames = { "USERID", "PROVIDERID", "RANK" } ) } )
public class UserConnection implements Serializable {

    private static final long serialVersionUID = 1942785325805820704L;

    @Id
    private String userId;
    
    @Id
    private String providerId;

    @Id
    private String providerUserId;

    private int rank;

    private String displayName;

    @Column( length = 512 )
    private URL profileUrl;

    @Column( length = 512 )
    private URL imageUrl;

    @Basic( optional = false )
    private String accessToken;

    private String secret;

    private String refreshToken;
    
    private Long expireTime;

    public UserConnection( String userId, String providerId, String providerUserId ) {
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
    }

    public UserConnection() {
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        UserConnection that = (UserConnection) obj;

        return providerId.equals( that.getProviderId() ) 
            && providerUserId.equals( that.getProviderUserId() )
            && userId.equals( that.getUserId() );
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + providerId.hashCode();
        result = 31 * result + providerUserId.hashCode();
        return result;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getUserId() {
        return userId;
    }
}
