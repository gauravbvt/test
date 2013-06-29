/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import com.mindalliance.playbook.model.Medium.MediumType;
import org.apache.solr.analysis.ClassicFilterFactory;
import org.apache.solr.analysis.ClassicTokenizerFactory;
import org.apache.solr.analysis.DoubleMetaphoneFilterFactory;
import org.apache.solr.analysis.EdgeNGramFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.WordDelimiterFilterFactory;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A contact.
 */
@Entity
@Indexed
@AnalyzerDef( name = "fname",
              filters = { @TokenFilterDef( factory = ClassicFilterFactory.class ),
                          @TokenFilterDef( factory = WordDelimiterFilterFactory.class ),
                          @TokenFilterDef( factory = LowerCaseFilterFactory.class ),
                          @TokenFilterDef( factory = DoubleMetaphoneFilterFactory.class,
                                           params = @Parameter( name="inject", value = "true" ) ),
                          @TokenFilterDef( factory = EdgeNGramFilterFactory.class, 
                                           params = { @Parameter( name="minGramSize", value = "1" ),
                                                      @Parameter( name="maxGramSize", value = "8" ) } )
              },
              tokenizer = @TokenizerDef( factory = ClassicTokenizerFactory.class ) )
@Analyzer( definition = "fname" )
@Table( 
    appliesTo = "Contact",
    indexes = { @Index( name = "byName", columnNames = { "ACCOUNT_ID", "FAMILYNAME", "GIVENNAME" } ) } )

public class Contact implements Serializable, Comparable<Contact> {

    private static final long serialVersionUID = 197714519234753545L;

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
    
    private boolean main;

    @Lob
    @Basic( fetch = FetchType.LAZY )
    private byte[] photo;

    @OneToMany( mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Medium> media;

    @ManyToOne( fetch = FetchType.LAZY )
    private Account account;

    private static final Collator COLLATOR = Collator.getInstance();

    public Contact() {
        media = new ArrayList<Medium>();
    }

    public Contact( Medium medium ) {
        this();
        addMedium( medium );
    }

    /**
     * Create a minimal contact based on a foreign contact.
     * @param foreignContact the contact
     */
    public Contact( Contact foreignContact ) {
        this();

        prefixes = foreignContact.getPrefixes();
        givenName = foreignContact.getGivenName();
        additionalNames = foreignContact.getAdditionalNames();
        familyName = foreignContact.getFamilyName();
        suffixes = foreignContact.getSuffixes();
        organization = foreignContact.getOrganization();
        title = foreignContact.getTitle();
        photo = foreignContact.getPhoto();
    }

    @Transient
    public boolean isNamed() {
        return prefixes != null || givenName != null || additionalNames != null || familyName != null
               || suffixes != null;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    @Field( boost = @Boost( 3.0F ) )
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName( String familyName ) {
        this.familyName = familyName;
    }

    @Field( boost = @Boost( 2.0F ) )
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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization( String organization ) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole( String role ) {
        this.role = role;
    }

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

    void setAccount( Account account ) {
        this.account = account;
    }

    @Transient
    public List<String> getEmails() {
        List<String> result = new ArrayList<String>();
        for ( Medium medium : media )
            if ( medium.getMediumType() == MediumType.EMAIL )
                result.add( ( (EmailMedium) medium ).getAddress() );

        return Collections.unmodifiableList( result );
    }


    /**
     * Test if this contact is mergeable with another.
     * @param other the other contact
     * @return false if there exists at least one non-null property of this contact which is different from the same
     * property in the other contact.
     */
    public boolean isMergeableWith( Contact other ) {
        String[] properties = { "givenName", "additionalNames", "familyName", "prefixes", "suffixes", "nickname"  };

        Collator collator = Collator.getInstance();
        collator.setStrength( Collator.PRIMARY );
        
        PropertyAccessor newContactWrapper  = new BeanWrapperImpl( other );
        PropertyAccessor oldContactWrapper  = new BeanWrapperImpl( this );
        for ( String property : properties ) {
            String newValue = (String) newContactWrapper.getPropertyValue( property );
            if ( newValue != null && !newValue.isEmpty() ) {
                String oldValue = (String) oldContactWrapper.getPropertyValue( property );
                if ( oldValue != null && !oldValue.isEmpty() && !collator.equals( newValue, oldValue ) )
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
        if ( givenName == null )
            givenName = contact.getGivenName();
        if ( additionalNames == null )
            additionalNames = contact.getAdditionalNames();
        if ( familyName == null )
            familyName = contact.getFamilyName();
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

        for ( Medium medium : contact.getMedia() )
            addMedium( medium );
    }

    public boolean isMain() {
        return main;
    }

    public void setMain( boolean main ) {
        this.main = main;
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
            
            if ( nickname != null ) {
                if ( sb.length() > 0 )
                    sb.append( ' ' );
                sb.append( '\"' ).append( nickname ).append( '\"' );
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
    /**
     * Return corresponding medium in a contact. If not present, add one.
     *
     * @param using a foreign medium
     * @return a medium, possibly new
     */
    public Medium addMedium( Medium using ) {
        for ( Medium medium : media )
            if ( medium.equals( using ) )
                return medium;

        Medium result = Medium.copy( this, using );

        media.add( result );
        return result;
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

    @Transient
    public boolean hasPhoto() {
        return photo != null;
    }

    /**
     * Get media that could potentially be matched to an account. 
     * @return relevant media
     */
    @Transient
    public Set<Medium> getKeyMedia() {
        Set<Medium> keys = new HashSet<Medium>();
        for ( Medium medium : media )
            switch ( medium.getMediumType() ) {  
                case EMAIL:
                case FACEBOOK:
                case TWITTER:
                case LINKEDIN:
                    keys.add( medium );
                    break;
                default:
                    break;
                }
        return keys;
    }
}
