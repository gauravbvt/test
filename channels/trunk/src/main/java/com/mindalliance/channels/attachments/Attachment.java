package com.mindalliance.channels.attachments;

import com.mindalliance.channels.model.ModelObject;

import java.io.Serializable;

/**
 * A record of a document attached to a model object.
 * Used in analysis.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 29, 2009
 * Time: 2:42:35 PM
 */
public class Attachment implements Serializable {
    /**
     * A ticket.
     */
    private String ticket;
    /**
     * A document.
     */
    private Document document;
    /**
     * A model object.
     */
    private ModelObject modelObject;

    public Attachment( Document document, ModelObject modelObject, String ticket ) {
        this.document = document;
        this.modelObject = modelObject;
        this.ticket = ticket;
    }

    public String getTicket() {
        return ticket;
    }

    public Document getDocument() {
        return document;
    }

    public ModelObject getModelObject() {
        return modelObject;
    }
}
