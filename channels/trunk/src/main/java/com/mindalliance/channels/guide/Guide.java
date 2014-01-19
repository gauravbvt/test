package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
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
        if ( userRole == null ) {
            LOG.warn( "User role " + userRoleId + " not found in guide " + getName() );
            userRole = getUserRoles().get( 0 );
        }
        return userRole;
    }

/*
    public Section derefSection( final String groupId ) {
        return (Section) CollectionUtils.find(
                sections,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Section) object ).getId().equals( groupId );
                    }
                } );
    }

    public int findSectionIndex( Section nextGroup ) {
        return getSections().indexOf( nextGroup );
    }

    public Section findSection( final String sectionId ) {
        Section section = null;
        if ( sectionId != null ) {
            section = (Section) CollectionUtils.find( sections,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Section) object ).getId().equals( sectionId );
                        }
                    } );
        }
        if ( section == null ) {
            LOG.warn( "Section " + sectionId + " not found in guide " + getName() );
            section = getSections().get( 0 );
        }
        return section;
    }
*/

    public void setContext( Map<String, Object> map ) {
        this.context = new LazyDynaMap( map );
    }

    public Object getContext() {
        return context;
    }

    public Topic findTopic( UserRole userRole, String sectionId, String topicId ) {
        Section section = userRole.findSection( sectionId );
        if ( section != null ) {
            return section.findTopic( topicId );
        } else {
            return null;
        }
    }

    public Map<String, TopicItem> getGlossary( UserRole userRole ) {
        Map<String, TopicItem> glossary = new HashMap<String, TopicItem>();
        Topic topic = findTopic( userRole, userRole.getGlossarySection(), userRole.getGlossaryTopic() );
        if ( topic != null ) {
            for ( TopicRef topicRef : topic.getDefinitions() ) {
                Topic definitionTopic = findTopic( userRole, topicRef.getSectionId(), topicRef.getTopicId() );
                if ( definitionTopic != null ) {
                    if (! definitionTopic.getTopicItems().isEmpty() )
                        glossary.put( definitionTopic.getName().toLowerCase(), definitionTopic.getTopicItems().get( 0 ) );
                }
            }
        }
        return glossary;
    }
}
