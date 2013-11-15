package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.UserCommandChainsDiagramPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 2:26 PM
 */
public class UserCommandChainsPanel extends AbstractResizableDiagramPanel implements Guidable {

    private UserCommandChainsDiagramPanel userCommandChainsDiagramPanel;

    private IModel<ChannelsUser> userModel;
    private int[] diagramSize;

    public UserCommandChainsPanel( String id,
                                   IModel<ChannelsUser> userModel,
                                   Set<Long> expansions,
                                   String prefixDomIdentifier,
                                   double[] diagramSize ) {
        super( id, expansions, prefixDomIdentifier, diagramSize );
        this.userModel = userModel;
        init();
    }

    public UserCommandChainsPanel( String id,
                                   IModel<ChannelsUser> userModel,
                                   Set<Long> expansions,
                                   String prefixDomIdentifier ) {
        super( id, expansions, prefixDomIdentifier );
        this.userModel = userModel;
        init();
    }

    @Override
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            userCommandChainsDiagramPanel = new UserCommandChainsDiagramPanel(
                    "diagram",
                    userModel,
                    null,
                    getDomIdentifier() );
        } else {
            userCommandChainsDiagramPanel = new UserCommandChainsDiagramPanel(
                    "diagram",
                    userModel,
                    getDiagramSize(),
                    getDomIdentifier() );
        }
        userCommandChainsDiagramPanel.setOutputMarkupId( true );
        addOrReplace( userCommandChainsDiagramPanel );
    }

    @Override
    protected AbstractDiagramPanel getDiagramPanel() {
        return userCommandChainsDiagramPanel;
    }

    @Override
    public String getHelpSectionId() {
        return "plan_participation";
    }

    @Override
    public String getHelpTopicId() {
        return "command_chains";
    }
}
