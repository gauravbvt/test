package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.ChecklistFlowDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 7:51 PM
 */
public class ChecklistFlowFloatingPanel  extends AbstractFloatingCommandablePanel {

    private ChecklistFlowDiagramPanel checklistFlowDiagramPanel;

    /**
     * DOM identifier for resizeable element.
     */
    private static final String DOM_IDENTIFIER = ".checklist-flow .picture";
    /**
     * Width, height dimension constraints on the plan map diagram. In inches. None if any is 0.
     */
    private double[] diagramSize = new double[2];


    public ChecklistFlowFloatingPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "info-sharing";
    }

    @Override
    public String getHelpTopicId() {
        return "checklist-flow";
    }

    private void init() {
        addPartTitle();
        addChecklistFlowDiagramPanel();
    }

    private void addPartTitle() {
        getContentContainer().add( new Label( "partTitle", getPart().getTaskLabel() ) );
    }

    private void addChecklistFlowDiagramPanel() {
        Settings settings = diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ?
                new Settings( DOM_IDENTIFIER, null, null, true, true ) :
                new Settings( DOM_IDENTIFIER, null, diagramSize, true, true );
        checklistFlowDiagramPanel = new ChecklistFlowDiagramPanel( "checklistFlow",
                new PropertyModel<Part>( this, "part" ),
                settings,
                true );
        checklistFlowDiagramPanel.setOutputMarkupId( true );
        getContentContainer().addOrReplace( checklistFlowDiagramPanel );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getPart(), "checklist-flow" );
        update( target, change );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isUnknown() || change.isModified() | change.isRefresh() ) {
            addChecklistFlowDiagramPanel();
            target.add( checklistFlowDiagramPanel );
        }
    }


    @Override
    protected String getTitle() {
        return getPart().getTaskLabel() + " - checklist flow";
    }

    public Part getPart() {
        return (Part) getModel().getObject();
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Part.class ) && change.isUpdated() && change.isForProperty( "checklist" ) ) {
            addChecklistFlowDiagramPanel();
            target.add( checklistFlowDiagramPanel );
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
