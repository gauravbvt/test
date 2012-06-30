package com.mindalliance.channels.pages.reports.protocols;

import com.mindalliance.channels.api.procedures.TaskData;
import com.mindalliance.channels.api.procedures.TriggerData;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Subprocedure link panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/29/12
 * Time: 3:42 PM
 */
public class SubProcedureLinkPanel extends AbstractDataPanel {

    private TriggerData triggerData;

    public SubProcedureLinkPanel( String id, TriggerData triggerData, ProtocolsFinder finder ) {
        super( id, finder );
        this.triggerData = triggerData;
        init();
    }

    private void init() {
        addSubProcedureTrigger();
        addTaskLink();
    }

    private void addSubProcedureTrigger() {
        add(  new SubProcedureTriggerDataPanel( "trigger", triggerData, getFinder() ) );
    }

    private void addTaskLink() {
        WebMarkupContainer link = new WebMarkupContainer( "link" );
        link.add(  new AttributeModifier( "href", "#" + getSubOrFollowUpTask().getAnchor() ) );
        add( link );
        link.add( new Label( "taskName",
                "Do task \"" + getSubOrFollowUpTask().getLabel() + "\"" ) );

    }

    private TaskData getSubOrFollowUpTask() {
        return triggerData.isOnResearching()
                ? triggerData.getOnResearch().getResearchTask()
                : triggerData.getOnDiscovery().getFollowUpTask();
    }

}
