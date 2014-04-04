package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.ModelSupplyChainsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 *
 * Supply chains floating panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 3:58 PM
 */
public class SupplyChainsFloatingPanel  extends AbstractFloatingCommandablePanel {

    private ModelSupplyChainsPanel modelSupplyChainsPanel;

    public SupplyChainsFloatingPanel( String id, Model<CollaborationModel> collaborationModel ) {
        super( id, collaborationModel );
        init();
    }

    private void init() {
        addHeading();
        addSupplyChainsPanel();
    }

     private void addHeading() {
        getContentContainer().add( new Label(
                "heading",
                "Supply chains in " + getPlanCommunity().getName() ) );
    }

    private void addSupplyChainsPanel() {
        modelSupplyChainsPanel = new ModelSupplyChainsPanel( "supplyChains" );
        getContentContainer().add( modelSupplyChainsPanel );
    }

    @Override
    protected String getTitle() {
        return "Supply chains";
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.SUPPLY_CHAINS );
        update( target, change );
    }

    @Override
    public String getHelpSectionId() {
        return "improving";
    }

    @Override
    public String getHelpTopicId() {
        return "supply-chains";
    }
}
