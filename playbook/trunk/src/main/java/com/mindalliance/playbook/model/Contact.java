/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import com.mindalliance.playbook.model.Medium.MediumType;
import org.hibernate.annotations.Index;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A contact.
 */
@Entity
@Indexed
@org.hibernate.annotations.Table( 
    appliesTo = "Contact",
    indexes = { @Index( name = "byName", columnNames = { "ACCOUNT_ID", "FAMILYNAME", "GIVENNAME" } ) } )
public class Contact implements Serializable, Comparable<Contact> {

    @Id
    @GeneratedValue
    private long id;

    private String familyName;

    private String givenName;

    private String additionalNames;

    private String prefixes;
    
    private String nickname;

    private String suffixes;

    private String organization;

    private String role;

    private String title;

    private String note;

    @Lob
    @Basic( fetch = FetchType.LAZY )
    private byte[] photo;

    @OneToMany( mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Medium> media;

    @ManyToOne( fetch = FetchType.LAZY )
    private Account account;

    private static final Collator COLLATOR = Collator.getInstance();

    public Contact() {
    }

    public Contact( Account account ) {
        this();
        this.account = account;
        media = new ArrayList<Medium>();
    }

    public Contact( Account account, String email ) {
        this( account );
        media.add( new OtherMedium( this, "EMAIL", email ) );
    }

    @Transient
    public boolean isNamed() {
        return prefixes != null || givenName != null || additionalNames != null || familyName != null
               || suffixes != null;
    }

    /**
     * @param step
     * @param you
     * @return ConfirmationReq
     */
    public ConfirmationReq confirmReq( Step step, Contact you ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @return Ack
     */
    public Ack ack( ConfirmationReq req ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @param reason
     * @return NAck
     */
    public NAck nack( ConfirmationReq req, String reason ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @param other
     * @return RedirectAck
     */
    public RedirectAck redirect( ConfirmationReq req, Contact other ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @param other
     * @return RedirectReq
     */
    public RedirectReq redirectReq( ConfirmationReq req, Contact other ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @return RedirectAck
     */
    public RedirectAck redirectAck( RedirectReq req ) {
        // TODO
        return null;
    }

    /**
     * @param req
     * @param reason
     * @return RedirectNAck
     */
    public RedirectNAck redirectNack( RedirectReq req, String reason ) {
        // TODO
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    @Field( boost = @Boost( 3.0F )  )
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName( String familyName ) {
        this.familyName = familyName;
    }

    @Field( boost = @Boost( 2.0F )  )
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName( String givenName ) {
        this.givenName = givenName;
    }

    @Field
    public String getAdditionalNames() {
        return additionalNames;
    }

    public void setAdditionalNames( String additionalNames ) {
        this.additionalNames = additionalNames;
    }

    @Field
    public String getPrefixes() {
        return prefixes;
    }

    public void setPrefixes( String prefixes ) {
        this.prefixes = prefixes;
    }

    @Field
    public String getSuffixes() {
        return suffixes;
    }

    public void setSuffixes( String suffixes ) {
        this.suffixes = suffixes;
    }

    @Field
    public String getOrganization() {
        return organization;
    }

    public void setOrganization( String organization ) {
        this.organization = organization;
    }

    @Field
    public String getRole() {
        return role;
    }

    public void setRole( String role ) {
        this.role = role;
    }

    @Field
    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote( String note ) {
        this.note = note;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto( byte[] photo ) {
        this.photo = photo;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia( List<Medium> media ) {
        this.media = media;
    }

    @Field
    public String getNickname() {
        return nickname;
    }

    public void setNickname( String nickname ) {
        this.nickname = nickname;
    }

    public Account getAccount() {
        return account;
    }

    public void addMedium( Medium medium ) {
        media.add( medium );
    }

    @Transient
    public List<Object> getEmails() {
        List<Object> result = new ArrayList<Object>();
        for ( Medium medium : media )
            if ( medium.getMediumType() == MediumType.OTHER 
                 && "EMAIL".equals( medium.getType() ) )
                result.add( ( (OtherMedium) medium ).getAddress() );

        return Collections.unmodifiableList( result );
    }


    /**
     * Test if this contact is mergeable with another.
     * @param other the other contact
     * @return false if there exists at least one non-null property of this contact which is different from the same
     * property in the other contact.
     */
    public boolean isMergeableWith( Contact other ) {
        String[] properties = { "givenName", "additionalNames", "familyName", "prefixes", "suffixes", "nickname",
                                "organization", "role", "title" };

        PropertyAccessor newContactWrapper  = new BeanWrapperImpl( other );
        PropertyAccessor oldContactWrapper  = new BeanWrapperImpl( this );
        for ( String property : properties ) {
            String newValue = (String) newContactWrapper.getPropertyValue( property );
            if ( newValue != null && !newValue.isEmpty() ) {
                String oldValue = (String) oldContactWrapper.getPropertyValue( property );
                if ( oldValue != null && !oldValue.isEmpty() && !newValue.equals( oldValue ) )
                    return false;
            }
        }

        return true;
    }

    /**
     * Merge information from another contact into this contact.
     *
     * @param contact the other contact
     */
    public void merge( Contact contact ) {
        if ( contact.getAdditionalNames() != null )
            additionalNames = contact.getAdditionalNames();
        if ( contact.getFamilyName() != null )
            familyName = contact.getFamilyName();
        if ( contact.getGivenName() != null )
            givenName = contact.getGivenName();
        if ( contact.getNickname() != null )
            nickname = contact.getNickname();
        if ( contact.getNote() != null )
            note = contact.getNote();
        if ( contact.getOrganization() != null )
            organization = contact.getOrganization();
        if ( contact.getPhoto() != null )
            photo = contact.getPhoto();
        if ( contact.getPrefixes() != null )
            prefixes = contact.getPrefixes();
        if ( contact.getRole() != null )
            role = contact.getRole();
        if ( contact.getSuffixes() != null )
            suffixes = contact.getSuffixes();
        if ( contact.getTitle() != null )
            title = contact.getTitle();

        Collection<Medium> mediumSet = new HashSet<Medium>( media );
        for ( Medium medium : contact.getMedia() )
            if ( !mediumSet.contains( medium ) )
                addMedium( medium.getMediumType() == MediumType.OTHER ?
                           new OtherMedium( this, (OtherMedium) medium )
                         : new AddressMedium( this, (AddressMedium) medium ) );
    }

    @Override
    public String toString() {

        return isNamed()            ? getFullName() : 
               organization != null ? organization 
                                    : super.toString();
    }
    
    @Transient
    public String getJob() {
        StringBuilder sb = new StringBuilder();

        if ( title != null )
            sb.append( title );

        if ( organization != null ) {
            if ( sb.length() > 0 )
                sb.append( " at " );
            sb.append( organization );
        }

        if ( role != null ) {
            if ( sb.length() > 0 )
                sb.append( ' ' );
            sb.append( '(' );
            sb.append( role );
            sb.append( ')' );
        }
        
        return sb.toString();
    }

    /**
     * Get a combined full name.
     * @return a concatenation of various name parts
     */
    @Transient
    public String getFullName() {
        StringBuilder sb = new StringBuilder();

        if ( isNamed() ) {
            if ( prefixes != null )
                sb.append( prefixes );

            if ( givenName != null ) {
                if ( sb.length() > 0 )
                    sb.append( ' ' );
                sb.append( givenName );
            }

            if ( additionalNames != null ) {
                if ( sb.length() > 0 )
                    sb.append( ' ' );
                sb.append( additionalNames );
            }

            if ( familyName != null ) {
                if ( sb.length() > 0 )
                    sb.append( ' ' );
                sb.append( familyName );
            }

            if ( suffixes != null ) {
                if ( sb.length() > 0 )
                    sb.append( ' ' );
                sb.append( suffixes );
            }
        }
        else
            sb.append( "(anonymous)" );

        return sb.toString();
    }

    @Override
    public int compareTo( Contact o ) {
        COLLATOR.setStrength( Collator.TERTIARY );
        return COLLATOR.compare( toString(), o.toString() );
    }

    public Medium findMedium( Medium using ) {
        for ( Medium medium : media ) {
            if ( medium.equals( using ) )
                return medium;
        }
        
        return null;
    }

    @Override
    public boolean equals( Object obj ) {
        return this == obj 
            || obj instanceof Contact && id == ( (Contact) obj ).getId();
    }

    @Override
    public int hashCode() {
        return (int) ( id ^ id >>> 32 );
    }
}
