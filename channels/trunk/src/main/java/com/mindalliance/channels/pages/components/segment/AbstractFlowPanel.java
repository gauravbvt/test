package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.segment.menus.FlowActionsMenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Abstract flow panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 15, 2009
 * Time: 1:52:11 PM
 */
public class AbstractFlowPanel  extends AbstractCommandablePanel {

    /**
     * The underlying flow.
     */
    private IModel<Flow> flowModel;
    /**
     * Flow action menu.
     */
    private Component flowActionMenu;
    /**
     * Whether send flow view.
     */
    private boolean send;
    private boolean collapsed;

    public AbstractFlowPanel( String id, IModel<Flow> flowModel, boolean isSend, boolean collapsed) {
        this( id, flowModel, isSend, collapsed, null);
        
    }
    public AbstractFlowPanel(
            String id,
            IModel<Flow> flowModel,
            boolean send,
            boolean collapsed,
            Set<Long> expansions ) {
        super( id, flowModel, expansions );
        this.flowModel = flowModel;
        this.send = send;
        this.collapsed = collapsed;
    }

    public Flow getFlow() {
        return flowModel.getObject();
    }

    /**
     * Set flow.
     * (used by PropertyModel)
     *
     * @param flow a flow
     */
    public void setFlow( Flow flow ) {
        flowModel.setObject( flow );
    }


    public Component getFlowActionMenu() {
        return flowActionMenu;
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

    /**
     * Add flow action menu.
     */
    protected void addFlowActionMenu( ) {
        flowActionMenu = new FlowActionsMenuPanel(
                    "flowActionsMenu",
                    new PropertyModel<Flow>( this, "flow" ),
                    isSend(),
                    isCollapsed() );
        flowActionMenu.setOutputMarkupId( true );
        addOrReplace( flowActionMenu );
    }

    protected boolean hasIssues() {
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        return analyst.hasIssues( getFlow(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
    }

    protected String getErrorSummary() {
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        return analyst.getIssuesSummary( getFlow(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
    }

    protected String getCssClasses( boolean hasIssues, String summary ) {
        String flowType = getFlowTypeCssClass();
        String errorType = getFlowErrorCssClass( hasIssues, summary );
        return "pointer " + flowType + ( errorType.isEmpty() ? "" : ( " " + errorType ) );
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
            return flow.isCapability()
                    ? "capability"
                    : "sharing";
        } else {
            return flow.isNeed()
                    ? "need"
                    : "sharing";
        }

    }
    

}
