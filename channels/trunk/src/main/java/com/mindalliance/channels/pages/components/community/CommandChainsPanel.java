package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.CommandChainsDiagramPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;

import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 2:26 PM
 */
public class CommandChainsPanel extends AbstractResizableDiagramPanel implements Guidable {

    private CommandChainsDiagramPanel commandChainsDiagramPanel;

    private ChannelsUser user;
    private Agent agent;
    private int[] diagramSize;

    public CommandChainsPanel( String id,
                               ChannelsUser user,
                               Set<Long> expansions,
                               String prefixDomIdentifier,
                               double[] diagramSize ) {
        super( id, expansions, prefixDomIdentifier, diagramSize );
        this.user = user;
        init();
    }

    public CommandChainsPanel( String id,
                               ChannelsUser user,
                               Set<Long> expansions,
                               String prefixDomIdentifier ) {
        super( id, expansions, prefixDomIdentifier );
        this.user = user;
        init();
    }

    public CommandChainsPanel( String id,
                               Agent agent,
                               Set<Long> expansions,
                               String prefixDomIdentifier,
                               double[] diagramSize ) {
        super( id, expansions, prefixDomIdentifier, diagramSize );
        this.agent = agent;
        init();
    }

    public CommandChainsPanel( String id,
                               Agent agent,
                               Set<Long> expansions,
                               String prefixDomIdentifier ) {
        super( id, expansions, prefixDomIdentifier );
        this.agent = agent;
        init();
    }


    @Override
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            if ( user != null )
                commandChainsDiagramPanel = new CommandChainsDiagramPanel(
                        "diagram",
                        user,
                        null,
                        getDomIdentifier() );
            else
                commandChainsDiagramPanel = new CommandChainsDiagramPanel(
                        "diagram",
                        agent,
                        null,
                        getDomIdentifier() );
        } else {
            if ( user != null )
                commandChainsDiagramPanel = new CommandChainsDiagramPanel(
                        "diagram",
                        user,
                        getDiagramSize(),
                        getDomIdentifier() );
            else
                commandChainsDiagramPanel = new CommandChainsDiagramPanel(
                        "diagram",
                        agent,
                        getDiagramSize(),
                        getDomIdentifier() );
        }
        commandChainsDiagramPanel.setOutputMarkupId( true );
        addOrReplace( commandChainsDiagramPanel );
    }

    @Override
    protected AbstractDiagramPanel getDiagramPanel() {
        return commandChainsDiagramPanel;
    }

    @Override
    public String getHelpSectionId() {
        return "community_participation";
    }

    @Override
    public String getHelpTopicId() {
        return "command_chains";
    }
}
