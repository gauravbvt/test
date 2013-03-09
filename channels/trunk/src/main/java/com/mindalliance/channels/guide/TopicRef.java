package com.mindalliance.channels.guide;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 9:37 PM
 */
public class TopicRef implements Serializable {

    @XStreamAsAttribute
    @XStreamAlias("section")
    private String sectionId;

    @XStreamAsAttribute
    @XStreamAlias("topic")
    private String topicId;

    public TopicRef() {
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId( String sectionId ) {
        this.sectionId = sectionId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId( String topicId ) {
        this.topicId = topicId;
    }
}
