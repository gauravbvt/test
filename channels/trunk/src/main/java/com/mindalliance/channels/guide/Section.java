package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:35 PM
 */
public class Section implements Serializable {

    @XStreamAsAttribute
    private String id;

    private String name;

    @XStreamImplicit( itemFieldName = "topic" )
    private List<Topic> topics;

    public Section() {
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

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics( List<Topic> topics ) {
        this.topics = topics;
    }

    public Topic derefTopic( final String topicId ) {
        return (Topic) CollectionUtils.find(
                topics,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Topic) object ).getId().equals( topicId );
                    }
                } );
    }

    public Topic findTopic( final String topicId ) {
        return (Topic)CollectionUtils.find( topics,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ((Topic)object).getId().equals( topicId );
                    }
                });
    }
}
