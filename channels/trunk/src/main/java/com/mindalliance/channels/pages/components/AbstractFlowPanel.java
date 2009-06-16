package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.pages.components.menus.FlowActionsMenuPanel;
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
     * Whether outcome (send) flow view.
     */
    private boolean outcome;
    private boolean collapsed;

    public AbstractFlowPanel( String id, IModel<Flow> flowModel, boolean outcome, boolean collapsed) {
        this( id, flowModel, outcome, collapsed, null);
        
    }
    public AbstractFlowPanel(
            String id,
            IModel<Flow> flowModel,
            boolean outcome,
            boolean collapsed,
            Set<Long> expansions ) {
        super( id, flowModel, expansions );
        this.flowModel = flowModel;
        this.outcome = outcome;
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

    public boolean isOutcome() {
        return outcome;
    }

    public final void setOutcome( boolean outcome ) {
         this.outcome = outcome;
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
                    isOutcome(),
                    isCollapsed() );
        flowActionMenu.setOutputMarkupId( true );
        addOrReplace( flowActionMenu );
    }


}
