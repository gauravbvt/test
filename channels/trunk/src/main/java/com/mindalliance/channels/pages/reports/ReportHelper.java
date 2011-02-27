package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
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

    PlanService getPlanService();

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
