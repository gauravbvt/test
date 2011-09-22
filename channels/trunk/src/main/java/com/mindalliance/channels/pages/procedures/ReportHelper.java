package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.QueryService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;

import java.util.List;

/**
 * Procedures report helper.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/15/11
 * Time: 9:50 PM
 */
public interface ReportHelper {

    void setAsDefaultModel( Component component );

    Part getPart();

    AttributeModifier newCssClass( String css );

    AttachmentManager getAttachmentManager();

    QueryService getPlanService();

    Assignment getAssignment();

    Component newFlowLink( Flow flow );

    Specable getActor();

    Component newFlowLink( Part part, Specable actor );

    String getFlowString( Part part );

    Component newTaskLink( Part part, Specable actor );

    Flow getFlow();

    ImagingService getImagingService();

    boolean isSending();

    Specable getFocusEntity();

    Assignments getNotifications( Assignments assignments, QueryService queryService );

    Assignments getRequests( Assignments assignments, QueryService queryService );

    Assignments getAssignments();

    List<Commitment> getCommitments( Flow flow );
}
