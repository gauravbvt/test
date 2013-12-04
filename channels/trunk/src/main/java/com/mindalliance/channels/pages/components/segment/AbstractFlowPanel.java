/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Abstract flow panel.
 */
public class AbstractFlowPanel extends AbstractCommandablePanel {

    /**
     * The underlying flow.
     */
    private IModel<Flow> flowModel;

    /**
     * Whether send flow view.
     */
    private boolean send;

    private boolean collapsed;

    private int index;

    /**
     * Whether flow was updated.
     */
    private boolean flowUpdated = false;

    public AbstractFlowPanel( String id, IModel<Flow> flowModel, boolean isSend, boolean collapsed, int index ) {
        this( id, flowModel, isSend, collapsed, null, index );
    }

    public AbstractFlowPanel( String id, IModel<Flow> flowModel, boolean send, boolean collapsed, Set<Long> expansions,
                              int index ) {
        super( id, flowModel, expansions );
        this.flowModel = flowModel;
        this.send = send;
        this.collapsed = collapsed;
        this.index = index;
    }

    public Flow getFlow() {
        return flowModel.getObject();
    }

    public boolean isFlowUpdated() {
        return flowUpdated;
    }

    public void setFlowUpdated( boolean flowUpdated ) {
        this.flowUpdated = flowUpdated;
    }

    /**
     * Set flow. (used by PropertyModel)
     *
     * @param flow a flow
     */
    public void setFlow( Flow flow ) {
        flowModel.setObject( flow );
    }

    public boolean isSend() {
        return send;
    }

    public final void setSend( boolean send ) {
        this.send = send;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
    }

    protected boolean hasIssues() {
        Doctor doctor = getCommunityService().getDoctor();
        return doctor.hasIssues( getCommunityService(), getFlow(), Doctor.INCLUDE_PROPERTY_SPECIFIC );
    }

    protected String getErrorSummary() {
        Doctor doctor = getCommunityService().getDoctor();
        return doctor.getIssuesSummary( getCommunityService(), getFlow(), Doctor.INCLUDE_PROPERTY_SPECIFIC );
    }

    public String getCssClasses() {
        String flowType = getFlowTypeCssClass();
        String priority = getFlowPriorityCssClass();
        return "title pointer " + flowType
               // + ( errorType.isEmpty() ? "" : ( " " + errorType ) )
               + ( priority.isEmpty() ? "" : " " + priority );
    }

    private String getFlowPriorityCssClass() {
        Flow flow = getFlow();
        return flow.isSharing() ? getQueryService().computeSharingPriority( flow ).getName().toLowerCase() : "";
    }

    protected String getRowTypeCssClass() {
        return index % 2 == 0 ? "evenFlow" : "oddFlow";
    }

    protected String getFlowErrorCssClass( boolean hasIssues, String summary ) {
        if ( !summary.isEmpty() ) {
            return "error";
        } else {
            if ( hasIssues ) {
                return "waived";
            } else {
                return "";
            }
        }
    }

    /**
     * Return flow css class.
     *
     * @return a string
     */
    protected String getFlowTypeCssClass() {
        Flow flow = getFlow();
        if ( isSend() ) {
            return flow.isCapability() ? "capability" : "sharing";
        } else {
            return flow.isNeed() ? "need" : "sharing";
        }
    }
}
