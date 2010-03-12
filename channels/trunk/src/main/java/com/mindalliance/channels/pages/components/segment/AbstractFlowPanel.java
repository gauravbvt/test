package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.model.Flow;
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


}
