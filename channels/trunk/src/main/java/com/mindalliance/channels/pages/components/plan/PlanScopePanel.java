package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractIndexPanel;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
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
public class PlanScopePanel extends AbstractCommandablePanel {

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


    public PlanScopePanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
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
                target.addComponent( scopeIndexPanel );
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
                target.addComponent( scopeIndexPanel );
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
                target.addComponent( scopeIndexPanel );
                updateCheckBoxes( target );
            }
        } );
        expectedCheckBox.setOutputMarkupId( true );
        add( expectedCheckBox );
    }

    private void updateCheckBoxes( AjaxRequestTarget target ) {
        target.addComponent( expectedCheckBox );
        target.addComponent( involvedCheckBox );
        target.addComponent( uninvolvedCheckBox );
    }

    private void addToScope() {
        addInvolvedField = new AutoCompleteTextField<String>(
                "newInvolved",
                new PropertyModel<String>( this, "newInvolvedName" ) ) {
            List<String> choices = getCandidateOrganizationNames();

            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        addInvolvedField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                involveNewOrganization();
                newInvolvedName = null;
                addSelectedOrganization();
                target.addComponent( organizationContainer );
                addScopeIndex();
                target.addComponent( scopeIndexPanel );
                target.addComponent( addInvolvedField );
            }
        } );
        add( addInvolvedField );

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
                new Model<String>( getInvovementTitle() )
        );
        organizationContainer.add( involvementLabel );
        ModelObjectLink detailsLink = new ModelObjectLink(
                "detailsLink",
                new Model<Organization>( selectedOrganization ),
                new Model<String>( "See details..." ),
                "View the details of the organization" );
        organizationContainer.add( detailsLink );
        Link expectationActionLink = new AjaxFallbackLink( "expectationActionLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeExpectation();
                addScopeIndex();
                target.addComponent( scopeIndexPanel );
                addSelectedOrganization();
                target.addComponent( organizationContainer );
                // update( target, new Change( Change.Type.Updated, getPlan(), "organizations" ) );
            }
        };
        organizationContainer.add( expectationActionLink );
        Label expectationLabel = new Label(
                "expectationAction",
                new Model<String>( getExpectationAction() )
        );
        expectationLabel.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( getExpectationActionHint() ) ) );
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
                        new UpdatePlanObject( getPlan(),
                                "organizations",
                                selectedOrganization,
                                UpdateObject.Action.Remove ) );
                if ( getCommander().cleanup( Organization.class, selectedOrganization.getName() ) )
                    selectedOrganization = null;
            } else {
                doCommand(
                        new UpdatePlanObject( getPlan(),
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
                    new UpdatePlanObject( getPlan(),
                            "organizations",
                            organization,
                            UpdateObject.Action.Add ) );
        }
    }

    private String getInvovementTitle() {
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
        if ( change.isExpanded() && change.getSubject() instanceof Organization ) {
            if ( selectedOrganization == null
                    || !selectedOrganization.equals( change.getSubject() ) ) {
                selectedOrganization = (Organization) change.getSubject();
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
        if ( change.isSelected() && change.getSubject() instanceof Organization ) {
            addSelectedOrganization();
            target.addComponent( organizationContainer );
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
    private List<Organization> getIndexedOrganizations() {
        List<Organization> orgs = (List<Organization>) CollectionUtils.select(
                getQueryService().listActualEntities( Organization.class ),
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
            return getIndexedOrganizations();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Actor> findIndexedActors() {
            return new ArrayList<Actor>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Event> findIndexedEvents() {
            return new ArrayList<Event>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Phase> findIndexedPhases() {
            return new ArrayList<Phase>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<TransmissionMedium> findIndexedMedia() {
            return new ArrayList<TransmissionMedium>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Place> findIndexedPlaces() {
            return new ArrayList<Place>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Role> findIndexedRoles() {
            return new ArrayList<Role>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Flow> findIndexedFlows() {
            return new ArrayList<Flow>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            return new ArrayList<Part>();
        }

        /**
          * {@inheritDoc}
          */
         protected List<Segment> findIndexedSegments() {
             return new ArrayList<Segment>();
         }

    }

    private class TaskIndexPanel extends AbstractIndexPanel {

        private TaskIndexPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
            super( id, model, expansions );
        }

        /**
         * {@inheritDoc}
         */
        protected List<Organization> findIndexedOrganizations() {
            return new ArrayList<Organization>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Actor> findIndexedActors() {
            return new ArrayList<Actor>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Event> findIndexedEvents() {
            return new ArrayList<Event>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Phase> findIndexedPhases() {
            return new ArrayList<Phase>();
        }

        /**
          * {@inheritDoc}
          */
         protected List<TransmissionMedium> findIndexedMedia() {
             return new ArrayList<TransmissionMedium>();
         }

        /**
         * {@inheritDoc}
         */
        protected List<Place> findIndexedPlaces() {
            return new ArrayList<Place>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Role> findIndexedRoles() {
            return new ArrayList<Role>();
        }

        /**
         * {@inheritDoc}
         */
        protected List<Flow> findIndexedFlows() {
            return new ArrayList<Flow>();
        }

        /**
          * {@inheritDoc}
          */
         protected List<Segment> findIndexedSegments() {
             return new ArrayList<Segment>();
         }

        /**
         * {@inheritDoc}
         */
        protected List<Part> findIndexedParts() {
            return getQueryService().findAllPartsPlayedBy( (Organization) getModel().getObject() );
        }
    }
}
