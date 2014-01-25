package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:34 PM
 */
public class Guide implements Serializable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Guide.class );

    private String name;

    private String description;

    @XStreamImplicit(itemFieldName = "role")
    private List<UserRole> userRoles;

    private Object context = this;

    public Guide() {
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles( List<UserRole> userRoles ) {
        this.userRoles = userRoles;
    }

    public UserRole findUserRole( final String userRoleId ) {
        UserRole userRole = findUserRoleNoDefault( userRoleId );
        if ( userRole == null ) {
            LOG.warn( "User role " + userRoleId + " not found in guide " + getName() );
            userRole = getUserRoles().get( 0 );
        }
        return userRole;
    }

    public UserRole findUserRoleNoDefault( final String userRoleId ) {
        UserRole userRole = null;
        if ( userRoleId != null ) {
            userRole = (UserRole) CollectionUtils.find( userRoles,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (UserRole) object ).getId().equals( userRoleId );
                        }
                    } );
        }
        return userRole;
    }

    public void setContext( Map<String, Object> map ) {
        this.context = new LazyDynaMap( map );
    }

    public Object getContext() {
        return context;
    }

    public Section findSection( UserRole userRole, String sectionId ) {
        return userRole.findSection( this, sectionId );
    }

    public Topic findTopic( UserRole userRole, String sectionId, String topicId ) {
        Section section = userRole.findSection( this, sectionId );
        if ( section != null ) {
            return section.findTopic( topicId );
        } else {
            return null;
        }
    }

     public Map<String, String[]> getGlossary( UserRole userRole ) {
        Map<String, String[]> glossary = new HashMap<String, String[]>();
            for ( TopicRef topicRef : findAllGlossaryDefinitions( userRole ) ) {
                Topic definitionTopic = findTopic( userRole, topicRef.getSectionId(), topicRef.getTopicId() );
                if ( definitionTopic != null ) {
                    if (! definitionTopic.getTopicItems().isEmpty() ) {
                        String[] glossaryEntry = new String[2];
                        glossaryEntry[0] = topicRef.getSectionId();
                        glossaryEntry[1] = topicRef.getTopicId();
                        glossary.put( definitionTopic.getName().toLowerCase(), glossaryEntry );
                    }
                    else {
                        LOG.warn( "Glossary topic without a definition item: " + definitionTopic.getId() );
                    }
                }
         }
        return glossary;
    }

    public List<TopicRef> findAllGlossaryDefinitions( UserRole userRole ) {
       List<TopicRef> allDefinitions = new ArrayList<TopicRef>(  );
        Topic topic = getGlossaryTopic( userRole );
        if ( topic != null ) {
            allDefinitions.addAll( topic.getDefinitions() );
        }
        // Inherit glossary entries from isa user role
        if ( userRole.getIsa() != null ) {
            UserRole isaRole = findUserRoleNoDefault( userRole.getIsa() );
            allDefinitions.addAll( findAllGlossaryDefinitions( isaRole ) );
        }
        return allDefinitions;
    }

    public Topic getGlossaryTopic( UserRole userRole ) {
        return findTopic( userRole, userRole.getGlossarySection(), userRole.getGlossaryTopic() );
    }

    public List<TopicRef> getAllDefinitions( UserRole userRole, Topic topic ) {
        if ( getGlossaryTopic( userRole ).getId().equals( topic.getId() ) ) {
            return findAllGlossaryDefinitions( userRole );
        } else {
            return topic.getDefinitions();
        }
    }
}
