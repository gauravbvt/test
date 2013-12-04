/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.engine.analysis.Doctor;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;
import java.util.Set;

/**
 * Editor on the details of a segment (name, description, etc).
 */
public class SegmentEditDetailsPanel extends AbstractCommandablePanel implements Guidable {

    /**
     * Plan manager.
     */
    @SpringBean
    private PlanManager planManager;

    /**
     * An issues panel for segment issues.
     */
    private IssuesPanel issuesPanel;

     public SegmentEditDetailsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "segment-details";
    }

    private void init() {
        setOutputMarkupId( true );
        addIdentityFields();
        addTagsPanel();
        add( new AttachmentPanel( "attachments", new PropertyModel<Segment>( this, "segment" ) ) );
        addIssuesPanel();
    }

    private void addTagsPanel() {
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<Taggable>( getSegment() ) );
        add( tagsPanel );
    }


    private void addIdentityFields() {
        TextField<String> nameField = new TextField<String>( "name", new PropertyModel<String>( this, "name" ) );
        add( new FormComponentLabel( "name-label", nameField ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getSegment(), "name" ) );
            }
        } );
        nameField.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        addInputHint( nameField, "The name of the segment" );
        add( nameField );

        TextArea<String> descField =
                new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) );
        add( new FormComponentLabel( "description-label", descField ) );
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getSegment(), "description" ) );
            }
        } );
        descField.setEnabled( isLockedByUserIfNeeded( getSegment() ) );
        addInputHint( descField, "A brief overview of the segment" );
        add( descField );
    }


    private void addIssuesPanel() {
        Doctor doctor = getCommunityService().getDoctor();
        issuesPanel = new IssuesPanel( "issues", new PropertyModel<ModelObject>( this, "segment" ), getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        makeVisible( issuesPanel, doctor.hasIssues( getCommunityService(), getSegment(), false ) );
    }


    /**
     * Get edited segment.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return (Segment) getModel().getObject();
    }

    /**
     * Get segment name.
     *
     * @return a string
     */
    public String getName() {
        return getSegment().getName();
    }

    /**
     * Set segment name via command.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null && !name.isEmpty() )
            doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "name", name ) );
    }

    /**
     * Get segment description.
     *
     * @return a string
     */
    public String getDescription() {
        return getSegment().getDescription();
    }

    /**
     * Set segment name via command.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getSegment(), "description", desc ) );
    }


    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Doctor doctor = getCommunityService().getDoctor();
        makeVisible( target, issuesPanel, doctor.hasIssues( getCommunityService(), getSegment(), false ) );
        target.add( issuesPanel );
        super.updateWith( target, change, updated );
    }

    public void refresh( AjaxRequestTarget target ) {
        Doctor doctor = getCommunityService().getDoctor();
        makeVisible( target, issuesPanel, doctor.hasIssues( getCommunityService(), getSegment(), false ) );
        target.add( issuesPanel );
    }
}
