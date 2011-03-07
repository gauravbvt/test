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
    private int index;

    public AbstractFlowPanel( String id, IModel<Flow> flowModel, boolean isSend, boolean collapsed, int index) {
        this( id, flowModel, isSend, collapsed, null, index);
        
    }
    public AbstractFlowPanel(
            String id,
            IModel<Flow> flowModel,
            boolean send,
            boolean collapsed,
            Set<Long> expansions,
            int index) {
        super( id, flowModel, expansions );
        this.flowModel = flowModel;
        this.send = send;
        this.collapsed = collapsed;
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public void setIndex( int index ) {
        this.index = index;
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

    protected String getCssClasses(  ) {
        String flowType = getFlowTypeCssClass();
        String priority = getFlowPriorityCssClass();
        return "pointer "
                + flowType
               // + ( errorType.isEmpty() ? "" : ( " " + errorType ) )
                + ( priority.isEmpty() ? "" : ( " " + priority ) );
    }

    private String getFlowPriorityCssClass() {
        Flow flow = getFlow();
        return flow.isSharing()
                ? getQueryService().computeSharingPriority( flow ).getName().toLowerCase()
                : "";
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
