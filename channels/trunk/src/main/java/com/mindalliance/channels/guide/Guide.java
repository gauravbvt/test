package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:34 PM
 */
public class Guide implements Serializable {

    private String name;

    private String description;

    @XStreamImplicit( itemFieldName = "section" )
    private List<Section> sections;
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

    public List<Section> getSections() {
        return sections;
    }

    public void setSections( List<Section> sections ) {
        this.sections = sections;
    }

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
        return (Section) CollectionUtils.find( sections,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Section) object ).getId().equals( sectionId );
                    }
                } );
    }

    public void setContext( Map<String, Object> map ) {
        this.context = new LazyDynaMap( map );
    }

    public Object getContext() {
        return context;
    }
}
