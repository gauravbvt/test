package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:36 PM
 */
public class Topic implements Serializable {

    @XStreamAsAttribute
    private String id;

    private String name;

    @XStreamImplicit( itemFieldName = "item" )
    private List<TopicItem> topicItems;

    @XStreamImplicit( itemFieldName = "next" )
    private List<TopicRef> nextTopics;

    @XStreamAlias( value = "document" )
    private TopicDocument document;


    public Topic() {
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

    public List<TopicItem> getTopicItems() {
        return topicItems;
    }

    public void setTopicItems( List<TopicItem> topicItems ) {
        this.topicItems = topicItems;
    }

    public List<TopicRef> getNextTopics() {
        return nextTopics == null ? new ArrayList<TopicRef>() : nextTopics;
    }

    public void setNextTopics( List<TopicRef> nextTopics ) {
        this.nextTopics = nextTopics;
    }

    public TopicDocument getDocument() {
        return document;
    }

    public void setDocument( TopicDocument document ) {
        this.document = document;
    }
}
