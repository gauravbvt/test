package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @XStreamImplicit( itemFieldName = "definition" )
    private List<TopicRef> definitions;

    private List<TopicRef> sortedDefinitions;

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

    public List<TopicRef> getDefinitions() {
        return definitions == null ? new ArrayList<TopicRef>(  ) : definitions;
    }

    public void setDefinitions( List<TopicRef> definitions ) {
        this.definitions = definitions;
    }

    public List<TopicRef> getSortedDefinitions( final Guide guide, final UserRole userRole ) {
        if ( sortedDefinitions == null ) {
            sortedDefinitions = new ArrayList<TopicRef>();
            List<TopicRef> definitions = guide.getAllDefinitions( userRole, this );
            sortedDefinitions.addAll( definitions );
            Collections.sort(
                    sortedDefinitions,
                    new Comparator<TopicRef>() {
                        @Override
                        public int compare( TopicRef tr1, TopicRef tr2 ) {
                            Topic t1 = userRole.deref( guide, tr1 );
                            Topic t2 = userRole.deref( guide, tr2 );
                            return ( t1 != null && t2 != null )
                                    ? t1.getName().compareTo( t2.getName() )
                                    : 0;
                        }
                    } );
        }
        return sortedDefinitions;
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

    public String toString() {
        return "Topic " + getId();
    }

}
