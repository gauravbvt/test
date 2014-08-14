package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Part checklist panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/26/13
 * Time: 11:45 AM
 */
public class ChecklistFloatingPanel extends AbstractFloatingCommandablePanel {

    private ChecklistEditorPanel checklistEditor;
    private Label checklistTitle;

    public ChecklistFloatingPanel( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }



    @Override
    public String getHelpSectionId() {
        return "info-sharing";
    }

    @Override
    public String getHelpTopicId() {
        return "edit-checklist";
    }

    private void init() {
        addChecklistTitle();
        addPartTitle();
        addChecklistFlowIcon();
        addChecklistEditor();
    }

    private void addChecklistTitle() {
        checklistTitle =   new Label( "checklistTitle", getPart().getEffectiveChecklist().isEffectivelyConfirmed( getCommunityService() )
                ? "Confirmed checklist"
                : "Unconfirmed checklist" );
        checklistTitle.setOutputMarkupId( true );
        getContentContainer().addOrReplace( checklistTitle );

    }

    private void addPartTitle() {
        getContentContainer().add( new Label( "partTitle", getPart().getTaskLabel() ) );
    }

    private void addChecklistFlowIcon() {
        WebMarkupContainer checklistFlowIcon = new WebMarkupContainer( "checklist-flow-icon" );
        getContentContainer().add( checklistFlowIcon );
        checklistFlowIcon.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getPart(), "checklist-flow" ) );
            }
        } );
        addTipTitle(
                checklistFlowIcon,
                "Open the checklist flow diagram");
    }


    private void addChecklistEditor() {
        checklistEditor = new ChecklistEditorPanel( "editor", new PropertyModel<Part>( this, "part" ) );
        getContentContainer().addOrReplace( checklistEditor );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        getPart().getEffectiveChecklist().cleanUp();
        Change change = new Change( Change.Type.AspectClosed, getPart(), "checklist" );
        update( target, change );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isUnknown() || change.isModified() | change.isRefresh() ) {
            addChecklistEditor();
            target.add( checklistEditor );
        }
    }


    @Override
    protected String getTitle() {
        return getPart().getTaskLabel() + " - checklist";
    }

    public Part getPart() {
        return (Part) getModel().getObject();
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Part.class ) && change.isUpdated() && change.isForProperty( "checklist" ) ) {
            addChecklistTitle();
            target.add( checklistTitle );
        }
        super.updateWith( target, change, updated );
    }


}
