package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Requirement edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 1:39 PM
 */
public class RequirementEditPanel extends AbstractCommandablePanel {

    private static final String INFO = "Info";
    private static final String SENDERS = "Sources";
    private static final String RECEIVERS = "Receivers";
    private static final String CARD = "Sources per receiver";
    private static final String ATTACHMENTS = "Attachments";
    private static final String ISSUES = "Issues";

    private static final String[] HEADERS = {INFO, SENDERS, RECEIVERS, CARD, ATTACHMENTS, ISSUES};
    private String selectedSection = INFO;
    private TextField<String> nameField;
    private TextArea<String> descField;
    private Component sectionPanel;
    private WebMarkupContainer sectionHeadersContainer;

    public RequirementEditPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        requestLockOn( getRequirement() );
        addEditedBy();
        addName();
        addDescription();
        // addTags();
        addSectionHeaders();
        addSection();
        adjustFields();
    }

    private void addEditedBy() {
        Label editedByLabel = isLockedByOtherUser( getRequirement() )
                ?  editedByLabel(
                    "editedBy",
                    getRequirement(),
                    getLockManager().getLockUser( getRequirement().getId() ) )
                : new Label("editedBy", "");
        editedByLabel.setOutputMarkupId( true ) ;
        makeVisible( editedByLabel, isLockedByOtherUser( getRequirement() ) );
        add( editedByLabel );
    }

    private void adjustFields() {
        Requirement requirement = getRequirement();
        nameField.setEnabled( isLockedByUserIfNeeded( requirement ) );
        descField.setEnabled( isLockedByUserIfNeeded( requirement ) );
    }

    private void addName() {
        nameField = new TextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        add( nameField );
    }

    private void addDescription() {
        descField =
                new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) );
        descField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getRequirement(), "description" ) );
            }
        } );
        add( descField );
    }

 /*   private void addTags() {
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<Taggable>( getRequirement() ) );
        add( tagsPanel );
    }
*/
    private void addSectionHeaders() {
        sectionHeadersContainer = new WebMarkupContainer( "headersContainer" );
        sectionHeadersContainer.setOutputMarkupId( true );
        ListView<String> sectionHeaders = new ListView<String>(
                "reqSections",
                new PropertyModel<List<? extends String>>( this, "sectionHeaders" ) ) {
            @Override
            protected void populateItem( final ListItem<String> item ) {
                AjaxLink<String> link = new AjaxLink<String>( "sectionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        selectedSection = item.getModelObject();
                        addSection();
                        target.add( sectionPanel );
                        addSectionHeaders();
                        target.add( sectionHeadersContainer );
                    }
                };
                String sectionName = makeSectionName( item.getModelObject() );
                Label sectionNameLabel = new Label(
                        "sectionName",
                        sectionName );
                link.add( sectionNameLabel );
                item.add( link );
                if ( item.getModelObject().equals( selectedSection ) ) {
                    item.add( new AttributeModifier( "class", new Model<String>( "selected" ) ) );
                }
            }
        };
        sectionHeaders.setOutputMarkupId( true );
        sectionHeadersContainer.add( sectionHeaders );
        addOrReplace( sectionHeadersContainer );
    }

    private String makeSectionName( String section ) {
        if ( section.equals( ISSUES ) ) {
            int count = getCommunityService().listUserIssues( getRequirement() ).size();
            return ISSUES + " (" + count + ")";
        } else if ( section.equals( ATTACHMENTS ) ) {
            int count = getRequirement().getAttachments().size();
            return ATTACHMENTS + " (" + count + ")";
        } else {
            return section;
        }
    }

    public List<String> getSectionHeaders() {
        return Arrays.asList( HEADERS );
    }

    private void addSection() {
        Requirement requirement = getRequirement();
        if ( selectedSection.equals( INFO ) ) {
            sectionPanel = new InfoRequiredPanel( "reqSection", new Model<Requirement>( requirement ) );
        } else if ( selectedSection.equals( SENDERS ) ) {
            sectionPanel = new AssignmentSpecPanel(
                    "reqSection",
                    new Model<Requirement>( requirement ),
                    SENDERS.equals( RECEIVERS ) );
        } else if ( selectedSection.equals( RECEIVERS ) ) {
            sectionPanel = new AssignmentSpecPanel( "reqSection",
                    new Model<Requirement>( requirement ),
                    !SENDERS.equals( RECEIVERS ) );
        } else if ( selectedSection.equals( CARD ) ) {
            sectionPanel = new CardinalityRequiredPanel(
                    "reqSection",
                    new Model<Requirement>( requirement ),
                    "cardinality" );
        } else if ( selectedSection.equals( ATTACHMENTS ) ) {
            sectionPanel = new AttachmentPanel( "reqSection", new Model<Requirement>( requirement ) );
        } else if ( selectedSection.equals( ISSUES ) ) {
            sectionPanel = new IssuesPanel( "reqSection", new Model<Requirement>( requirement ), getExpansions() );
        } else {
            throw new RuntimeException( "Unknown: " + selectedSection );
        }
        sectionPanel.setOutputMarkupId( true );
        addOrReplace( sectionPanel );
    }

    public String getName() {
        return getRequirement().getName();
    }

    public void setName( String val ) {
        if ( val != null && !val.trim().isEmpty() ) {
            String name = val.trim();
            doCommand( new UpdateModelObject( getUsername(), getRequirement(), "name", name ) );
        }
    }

    public String getDescription() {
        return getRequirement().getDescription();
    }

    public void setDescription( String val ) {
        String description = val == null ? "" : val.trim();
        doCommand( new UpdateModelObject( getUsername(), getRequirement(), "description", description ) );
    }

    private Requirement getRequirement() {
        Requirement requirement = (Requirement) getModel().getObject();
        requirement.initialize( getCommunityService() );
        return requirement;
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( Issue.class ) || change.isForProperty( "attachments" ) ) {
            target.add( sectionHeadersContainer );
        }
        super.updateWith( target, change, updated );
    }

}
