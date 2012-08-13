package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.RequiredNetworkingDiagramPanel;
import org.apache.wicket.model.Model;

import java.util.Set;

/**
 * Required inter-organizational networking panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 9:08 AM
 */
public class RequiredNetworkingPanel extends AbstractResizableDiagramPanel {

    private final Model<Phase.Timing> timingModel;
    private final Model<Event> eventModel;
    private final Organization selectedOrganization;
    private final RequirementRelationship selectedRequiredNetworkingRel;
    private RequiredNetworkingDiagramPanel requiredNetworkingDiagramPanel;

    public RequiredNetworkingPanel(
            String id,
            Model<Phase.Timing> timingModel,
            Model<Event> eventModel,
            Organization selectedOrganization,
            RequirementRelationship selectedRequiredNetworkingRel,
            Set<Long> expansions,
            String domPrefixIdentifier ) {
        super( id, expansions, domPrefixIdentifier );
        this.timingModel = timingModel;
        this.eventModel = eventModel;
        this.selectedOrganization = selectedOrganization;
        this.selectedRequiredNetworkingRel = selectedRequiredNetworkingRel;
        init();
    }

    @Override
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            requiredNetworkingDiagramPanel = new RequiredNetworkingDiagramPanel(
                    WICKET_ID,
                    timingModel,
                    eventModel,
                    selectedOrganization,
                    selectedRequiredNetworkingRel,
                    null,
                    getDomIdentifier()

            );
        } else {
            requiredNetworkingDiagramPanel = new RequiredNetworkingDiagramPanel(
                    WICKET_ID,
                    timingModel,
                    eventModel,
                    selectedOrganization,
                    selectedRequiredNetworkingRel,
                    getDiagramSize(),
                    getDomIdentifier()

            );
        }
        requiredNetworkingDiagramPanel.setOutputMarkupId( true );
        addOrReplace( requiredNetworkingDiagramPanel );
    }

    @Override
    protected AbstractDiagramPanel getDiagramPanel() {
        return requiredNetworkingDiagramPanel;
    }
}
