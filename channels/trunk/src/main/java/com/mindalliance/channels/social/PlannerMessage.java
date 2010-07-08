package com.mindalliance.channels.social;

import com.mindalliance.channels.command.ModelObjectRef;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.ModelObject;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2010
 * Time: 2:04:30 PM
 */
public class PlannerMessage extends PersistentObject {

    private long planId;
    private String fromUsername;
    // broadcast if null
    private String toUsername;
    private String text;
    private ModelObjectRef aboutModelObject;

    public PlannerMessage( String text ) {
        super();
        User user = User.current();
        this.text = text;
        fromUsername = user.getUsername();
        planId = user.getPlan().getId();
    }

    public PlannerMessage( String text, ModelObject modelObject ) {
        this( text );
        aboutModelObject = new ModelObjectRef( modelObject );
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId( long planId ) {
        this.planId = planId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername( String toUsername ) {
        this.toUsername = toUsername;
    }

    public String getText() {
        return text;
    }

     public ModelObjectRef getAboutModelObject() {
        return aboutModelObject;
    }

    public boolean isBroadcast() {
        return toUsername == null;
    }

}
