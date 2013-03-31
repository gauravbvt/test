package com.mindalliance.channels.pages.components.segment.checklist;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.components.AbstractFloatingCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
        addChecklistEditor();
    }

    private void addChecklistTitle() {
        getContentContainer().add(
                new Label( "checklistTitle", getPart().getChecklist().isConfirmed()
                        ? "Confirmed checklist"
                        : "Unconfirmed checklist" ) );
    }

    private void addPartTitle() {
        getContentContainer().add( new Label( "partTitle", getPart().getTask() ) );
    }

    private void addChecklistEditor() {
        checklistEditor = new ChecklistEditorPanel( "editor", new PropertyModel<Part>( this, "part" ) );
        getContentContainer().addOrReplace( checklistEditor );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
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
        return getPart().getTask() + " - checklist";
    }

    public Part getPart() {
        return (Part) getModel().getObject();
    }

}
