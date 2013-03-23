package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/13
 * Time: 11:42 AM
 */
public class UserRole implements Serializable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Guide.class );


    @XStreamAsAttribute
    private String id;

    private String name;

    @XStreamImplicit(itemFieldName = "section")
    private List<Section> sections;

    public UserRole() {
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections( List<Section> sections ) {
        this.sections = sections;
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
            LOG.warn( "Section " + sectionId + " not found for user role " + getName() );
            section = getSections().get( 0 );
        }
        return section;
    }

    public String toString() {
        return "User role " + getId();
    }

}
