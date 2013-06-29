/*
 * Copyright (c) 2012. Mind-Alliance Systems LLC.
 * All rights reserved.
 * CONFIDENTIAL
 */

package com.mindalliance.playbook.model;

import org.apache.solr.analysis.ClassicFilterFactory;
import org.apache.solr.analysis.ClassicTokenizerFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.PorterStemFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A play in a playbook.
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
@DiscriminatorColumn( discriminatorType = DiscriminatorType.STRING )
@DiscriminatorValue( "play" )
@Indexed
@AnalyzerDef( name = "plays",
    filters = { 
        @TokenFilterDef( factory = ClassicFilterFactory.class ),
        @TokenFilterDef( factory = LowerCaseFilterFactory.class ),
        @TokenFilterDef( factory = PorterStemFilterFactory.class )
    },
    tokenizer = @TokenizerDef( factory = ClassicTokenizerFactory.class ) )
@Analyzer( definition = "plays" )
public class Play implements Serializable {

    private static final long serialVersionUID = -4665570662542782782L;

    @Id @GeneratedValue( strategy = GenerationType.TABLE )
    private long id;
    
    private String title;

    private String description;

    private String schedule;

    @ManyToOne
    private Playbook playbook;

    @ManyToMany( cascade = CascadeType.ALL )
    private Set<Tag> tags;

    @OneToMany( mappedBy = "play", cascade = CascadeType.ALL )
    @OrderBy( "sequence" )
    private List<Step> steps;

    //
    // Constructors
    //
    public Play() {
        tags = new HashSet<Tag>();
        steps = new ArrayList<Step>();
    }

    public Play( Playbook playbook, String title ) {
        this();
        this.title = title;
        this.playbook = playbook;
    }

    @Field
    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    @Field
    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule( String schedule ) {
        this.schedule = schedule;
    }

    public Playbook getPlaybook() {
        return playbook;
    }

    public void setPlaybook( Playbook playbook ) {
        this.playbook = playbook;
    }
                 
    @IndexedEmbedded
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags( Set<Tag> tags ) {
        this.tags = tags;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps( List<Step> steps ) {
        this.steps = steps;
    }

    public long getId() {
        return id;
    }

    public void addStep( Step step ) {
        steps.add( step );
    }

    public void removeStep( Step step ) {
        steps.remove( step );
    }

    /**
     * Get the owner of this play.
     * @return a contact, local to the owner.
     */
    @Transient
    public Contact getOwner() {
        return playbook.getMe();
    }

    @Transient
    public Account getAccount() {
        return playbook.getAccount();
    }
}
