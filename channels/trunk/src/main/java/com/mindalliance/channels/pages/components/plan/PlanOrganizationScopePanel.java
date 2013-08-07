package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan scope panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 26, 2009
 * Time: 1:29:03 PM
 */
public class PlanOrganizationScopePanel extends AbstractCommandablePanel implements Guidable {

    private boolean involvedOnly = false;
    private boolean uninvolvedOnly = false;
    private boolean expectedOnly = false;
    private Organization selectedOrganization;
    private ScopeIndexPanel scopeIndexPanel;
    private CheckBox involvedCheckBox;
    private CheckBox uninvolvedCheckBox;
    private CheckBox expectedCheckBox;
    private WebMarkupContainer organizationContainer;
    private String newInvolvedName;
    private TextField addInvolvedField;


    public PlanOrganizationScopePanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "scoping";
    }

    @Override
    public String getHelpTopicId() {
        return "organizations";
    }

    private void init() {
        addInvolvement();
        addExpectation();
        addToScope();
        addSelectedOrganization();
        addScopeIndex();
    }


    private void addInvolvement() {
        // Involved only
        involvedCheckBox = new CheckBox(
                "involved",
                new PropertyModel<Boolean>( this, "involvedOnly" ) );
        involvedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        involvedCheckBox.setOutputMarkupId( true );
        add( involvedCheckBox );
        // Uninvolved only
        uninvolvedCheckBox = new CheckBox(
                "uninvolved",
                new PropertyModel<Boolean>( this, "uninvolvedOnly" ) );
        uninvolvedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        uninvolvedCheckBox.setOutputMarkupId( true );
        add( uninvolvedCheckBox );
    }

    private void addExpectation() {
        // expected only
        expectedCheckBox = new CheckBox(
                "expected",
                new PropertyModel<Boolean>( this, "expectedOnly" ) );
        expectedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addScopeIndex();
                target.add( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        expectedCheckBox.setOutputMarkupId( true );
        add( expectedCheckBox );
    }

    private void updateCheckBoxes( AjaxRequestTarget target ) {
        target.add( expectedCheckBox );
        target.add( involvedCheckBox );
        target.add( uninvolvedCheckBox );
    }

    private void addToScope() {
        WebMarkupContainer newInvolvedContainer = new WebMarkupContainer( "newInvolvedContainer" );
        add( newInvolvedContainer );
        addInvolvedField = new AutoCompleteTextField<String>(
                "newInvolved",
                new PropertyModel<String>( this, "newInvolvedName" ),
                getAutoCompleteSettings() ) {
            List<String> choices = getCandidateOrganizationNames();

            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        addInvolvedField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                involveNewOrganization();
                newInvolvedName = null;
                addSelectedOrganization();
                target.add( organizationContainer );
                addScopeIndex();
                target.add( scopeIndexPanel );
                target.add( addInvolvedField );
            }
        } );
        addInputHint( addInvolvedField, "The name of an actual organization (press enter)" );
        newInvolvedContainer.add( addInvolvedField );
        newInvolvedContainer.setVisible( isLockedByUser( Channels.ALL_ORGANIZATIONS ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getCandidateOrganizationNames() {
        List<Organization> candidates = (List<Organization>) CollectionUtils.select(
                getQueryService().listActualEntities( Organization.class ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        Organization org = (Organization) obj;
                        return !org.isUnknown() && !getQueryService().isInvolvementExpected( org );
                    }
                }
        );
        return (List<String>) CollectionUtils.collect(
                candidates,
                TransformerUtils.invokerTransformer( "getName" )
        );
    }

    private void addSelectedOrganization() {
        organizationContainer = new WebMarkupContainer( "organizationContainer" );
        organizationContainer.setOutputMarkupId( true );
        makeVisible( organizationContainer, selectedOrganization != null );
        addOrReplace( organizationContainer );
        Label nameInTitleLabel = new Label(
                "nameInTitle",
                new Model<String>(
                        selectedOrganization != null
                                ? selectedOrganization.getName()
                                : "" ) );
        organizationContainer.add( nameInTitleLabel );
        Label nameLabel = new Label(
                "name",
                new Model<String>(
                        selectedOrganization != null
                                ? selectedOrganization.getName()
                                : "" ) );
        organizationContainer.add( nameLabel );
        Label involvementLabel = new Label(
                "involvement",
                new Model<String>( getInvolvementTitle() )
        );
        organizationContainer.add( involvementLabel );
        ModelObjectLink detailsLink = new ModelObjectLink(
                "detailsLink",
                new Model<Organization>( selectedOrganization ),
                new Model<String>( "See profile" ),
                "View the profile of the organization",
                "window" );
        organizationContainer.add( detailsLink );
        AjaxLink expectationActionLink = new AjaxLink( "expectationActionLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeExpectation();
                addScopeIndex();
                target.add( scopeIndexPanel );
                addSelectedOrganization();
                target.add( organizationContainer );
                // update( target, new Change( Change.Type.Updated, getPlan(), "organizations" ) );
            }
        };
        expectationActionLink.setVisible( isLockedByUser( Channels.ALL_ORGANIZATIONS ) );
        organizationContainer.add( expectationActionLink );
        Label expectationLabel = new Label(
                "expectationAction",
                new Model<String>( getExpectationAction() )
        );
        addTipTitle( expectationLabel, new Model<String>( getExpectationActionHint() ) );
        expectationActionLink.add( expectationLabel );
        WebMarkupContainer tasksContainer = new WebMarkupContainer( "tasksContainer" );
        tasksContainer.setVisible(
                selectedOrganization != null
                        && !getQueryService().findAllPartsPlayedBy( selectedOrganization ).isEmpty() );
        organizationContainer.add( tasksContainer );
        TaskIndexPanel taskIndexPanel = new TaskIndexPanel(
                "taskIndex",
                new Model<Organization>( selectedOrganization ),
                null
        );
        tasksContainer.add( taskIndexPanel );
    }

    private String getExpectationAction() {
        if ( selectedOrganization != null ) {
            if ( getQueryService().isInvolvementExpected( selectedOrganization ) ) {
                return "Remove expectation";
            } else {
                return "Add expectation";
            }
        } else {
            return "";
        }
    }

    private String getExpectationActionHint() {
        if ( selectedOrganization != null ) {
            if ( getQueryService().isInvolvementExpected( selectedOrganization ) ) {
                return "Remove expectation that the organization should have tasks";
            } else {
                return "Add expectation that the organization should have tasks";
            }
        } else {
            return "";
        }
    }

    private void changeExpectation() {
        if ( selectedOrganization != null ) {
            if ( getQueryService().isInvolvementExpected( selectedOrganization ) ) {
                doCommand(
                        new UpdatePlanObject( getUser().getUsername(), getPlan(),
                                "organizations",
                                selectedOrganization,
                                UpdateObject.Action.Remove ) );
                if ( getCommander().cleanup( Organization.class, selectedOrganization.getName() ) )
                    selectedOrganization = null;
            } else {
                doCommand(
                        new UpdatePlanObject( getUser().getUsername(), getPlan(),
                                "organizations",
                                selectedOrganization,
                                UpdateObject.Action.Add ) );
            }
        }
    }

    private void involveNewOrganization() {
        if ( newInvolvedName != null && !newInvolvedName.trim().isEmpty() ) {
            Organization organization = getQueryService().safeFindOrCreate(
                    Organization.class,
                    newInvolvedName );
            selectedOrganization = organization;
            doCommand(
                    new UpdatePlanObject( getUser().getUsername(), getPlan(),
                            "organizations",
                            organization,
                            UpdateObject.Action.Add ) );
        }
    }

    private String getInvolvementTitle() {
        String s = "";
        if ( selectedOrganization != null ) {
            boolean involved = getQueryService().isInvolved( selectedOrganization );
            boolean expected = getQueryService().isInvolvementExpected( selectedOrganization );
            if ( involved && expected ) s = "has tasks in this plan as expected.";
            else if ( involved && !expected ) s = "has tasks in this plan even though it does not have to.";
            else if ( !involved && expected ) s = "does not have tasks in this plan even though it is expected to.";
        }
        return s;
    }

    private void addScopeIndex() {
        scopeIndexPanel = new ScopeIndexPanel(
                "scopeIndex",
                getModel(),
                null
        );
        scopeIndexPanel.setOutputMarkupId( true );
        addOrReplace( scopeIndexPanel );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isExpanded() && change.isForInstanceOf( Organization.class ) ) {
            if ( selectedOrganization == null
                    || !selectedOrganization.equals( change.getSubject( getCommunityService() ) ) ) {
                selectedOrganization = (Organization) change.getSubject( getCommunityService() );
                change.setType( Change.Type.Selected );
            } else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.isForInstanceOf( Organization.class ) ) {
            addSelectedOrganization();
            target.add( organizationContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }


    public boolean isInvolvedOnly() {
        return involvedOnly;
    }

    public void setInvolvedOnly( boolean val ) {
        this.involvedOnly = val;
        if ( val )
            uninvolvedOnly = false;
    }

    public boolean isUninvolvedOnly() {
        return uninvolvedOnly;
    }

    public void setUninvolvedOnly( boolean val ) {
        this.uninvolvedOnly = val;
        if ( val )
            involvedOnly = false;
    }

    public boolean isExpectedOnly() {
        return expectedOnly;
    }

    public void setExpectedOnly( boolean val ) {
        this.expectedOnly = val;
    }


    public Organization getSelectedOrganization() {
        return selectedOrganization;
    }

    public void setSelectedOrganization( Organization selectedOrganization ) {
        this.selectedOrganization = selectedOrganization;
    }

    public String getNewInvolvedName() {
        return newInvolvedName;
    }

    public void setNewInvolvedName( String newInvolvedName ) {
        this.newInvolvedName = newInvolvedName;
    }

    @SuppressWarnings( "unchecked" )
    private List<Organization> getIndexedOrganizations( boolean mustBeReferenced ) {
        List<Organization> orgs = (List<Organization>) CollectionUtils.select(
                getQueryService().listActualEntities( Organization.class, mustBeReferenced ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !( (Organization) obj ).isUnknown();
                    }
                } );
        if ( involvedOnly || uninvolvedOnly ) {
            orgs = (List<Organization>) CollectionUtils.select(
                    orgs,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Organization org = (Organization) obj;
                            boolean involved = getQueryService().isInvolved( org );
                            return ( involvedOnly && involved )
                                    || ( uninvolvedOnly && !involved );
                        }
                    }
            );
        }
        if ( expectedOnly ) {
            orgs = (List<Organization>) CollectionUtils.select(
                    orgs,
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            Organization org = (Organization) obj;
                            boolean expected = getQueryService().isInvolvementExpected( org );
                            return !expectedOnly || expected;
                        }
                    }
            );
        }
        return orgs;
    }

    /**
     * Scope index panel.
     */
    private class ScopeIndexPanel extends AbstractIndexPanel {

        private ScopeIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
            super( id, model, expansions );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Organization> findIndexedOrganizations() {
            return getIndexedOrganizations( isMustBeReferenced() );
        }
    }

    private class TaskIndexPanel extends AbstractIndexPanel {

        private TaskIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
            super( id, model, expansions );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            Organization org = (Organization) getModel().getObject();
            if ( org == null )
                return new ArrayList<Part>();
            else
                return getQueryService().findAllPartsPlayedBy( org );
        }
    }
}
