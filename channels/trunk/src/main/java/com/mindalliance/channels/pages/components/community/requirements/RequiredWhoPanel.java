package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.community.participation.Agent;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Assignment spec resource edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/4/11
 * Time: 12:24 PM
 */
public class RequiredWhoPanel extends AbstractCommandablePanel {


    private final boolean isBeneficiary;

    private AutoCompleteTextField<String> agencyField;
    private AutoCompleteTextField<String> placeholderField;
    private AutoCompleteTextField<String> agentField;

    public RequiredWhoPanel( String id, Model<Requirement> requirementModel, boolean isBeneficiary ) {
        super( id, requirementModel );
        this.isBeneficiary = isBeneficiary;
        init();
    }

    private void init() {
        addAgentField();
        addJurisdictionField();
        addAgencyField();
        addPlaceholderField();
        adjustFields();
    }

    private void adjustFields( AjaxRequestTarget target ) {
        adjustFields();
        target.add( agencyField );
        target.add( placeholderField );
    }


    private void adjustFields() {
        Requirement requirement = getRequirement();
        Requirement.AgentSpec agentSpec = getAgentSpec();
        agentField.setEnabled( isLockedByUserIfNeeded( requirement ) );
        agencyField.setEnabled( isLockedByUserIfNeeded( requirement ) && !agentSpec.isAgencyImplied() ); // agency implied?
        placeholderField.setEnabled( isLockedByUserIfNeeded( requirement ) && !agentSpec.isPlaceholderImplied() ); // placeholder implied?
        addTipTitle( agencyField, agentSpec.isAgencyImplied() ? "This is implied by the agent" : "" );
        addTipTitle( placeholderField, agentSpec.isPlaceholderImplied() ? "This is implied by the agency" : "" );
    }

    private void addAgentField() {
        final List<String> choices = getAllAgentNames();
        agentField = new AutoCompleteTextField<String>(
                "agent",
                new PropertyModel<String>( this, "agentName" )
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( input.trim().isEmpty() || Matcher.matches( choice, input ) ) {
                        candidates.add( choice );
                    }
                }
                return candidates.iterator();
            }
        };
        agentField.setOutputMarkupId( true );
        agentField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addJurisdictionField();
                addAgencyField();
                addPlaceholderField();
                adjustFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        addOrReplace( agentField );
    }

    private void addJurisdictionField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                "jurisdiction",
                new PropertyModel<Part>( this, "requirement" ),
                getAllPlaceNames(),
                getAgentSpecPath( "jurisdiction" ),
                Place.class );
        addOrReplace( field );
    }

    private void addAgencyField() {
        final List<String> choices = getAllAgencyNames();
        agencyField = new AutoCompleteTextField<String>(
                "agency",
                new PropertyModel<String>( this, "agencyName" )
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( input.trim().isEmpty() || Matcher.matches( choice, input ) ) {
                        candidates.add( choice );
                    }
                }
                return candidates.iterator();
            }
        };
        agencyField.setOutputMarkupId( true );
        agencyField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addPlaceholderField();
                adjustFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        addOrReplace( agencyField );
    }

    private void addPlaceholderField() {
        final List<String> choices = getAllPlaceholderNames();
        placeholderField = new AutoCompleteTextField<String>(
                "placeholder",
                new PropertyModel<String>( this, "placeholderName" )
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( input.trim().isEmpty() || Matcher.matches( choice, input ) ) {
                        candidates.add( choice );
                    }
                }
                return candidates.iterator();
            }


        };
        placeholderField.setOutputMarkupId( true );
        placeholderField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        addOrReplace( placeholderField );
    }


    private String getAgentSpecPath( String property ) {
        return ( isBeneficiary ? "beneficiarySpec.agentSpec" : "committerSpec.agentSpec" ) + "." + property;
    }

    private List<String> getAllAgentNames() {
        List<String> names = new ArrayList<String>();
        for ( Agent agent : getPlanCommunity().getParticipationManager().getAllKnownAgents( getPlanCommunity() ) ) {
            names.add( agent.getName() );
        }
        Collections.sort( names );
        return names;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getAllAgencyNames() {
        List<String> names = new ArrayList<String>();
        for ( Agency agency :
                getPlanCommunity().getParticipationManager().getAllKnownAgencies( getPlanCommunity() ) ) {
            if ( agency.isFixedOrganization() || agency.isParticipatingAsPlaceholder() ) {
                names.add( agency.getName() );
            }
        }
        Collections.sort( names );
        return names;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getAllPlaceholderNames() {
        List<String> names = new ArrayList<String>();
        for ( Organization organization : getPlanCommunity().getPlanService().listActualEntities( Organization.class ) ) {
            if ( organization.isPlaceHolder() ) {
                names.add( organization.getName() );
            }
        }
        Collections.sort( names );
        return names;
    }


    public List<String> getAllPlaceNames() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public String getAgentName() {
        Agent agent = getAgentSpec().getAgent();
        return agent == null ? null : agent.getName();
    }

    private Requirement.AgentSpec getAgentSpec() {
        return isBeneficiary
                ? getRequirement().getBeneficiarySpec().getAgentSpec()
                : getRequirement().getCommitterSpec().getAgentSpec();
    }

    public void setAgentName( String name ) {
        PlanCommunity planCommunity = getPlanCommunity();
        Requirement requirement = getRequirement();
        if ( name != null && !name.isEmpty() ) {
            Agent agent = planCommunity.getParticipationManager().findAgentNamed( name, planCommunity );
            if ( agent != null ) {
                MultiCommand multiCommand = new MultiCommand( getUsername(), "Set required agent" );
                multiCommand.makeUndoable( false );
                multiCommand.setChange( new Change( Change.Type.Updated, requirement ) ); // set the summary change else produces none and no journaling
                multiCommand.addCommand( new UpdatePlanObject(
                        getUsername(),
                        requirement,
                        getAgentSpecPath( "actor" ),
                        agent.getActor() ) );
                if ( agent.isFromOrganizationParticipation() ) {
                    multiCommand.addCommand( new UpdatePlanObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "orgParticipationId" ),
                            agent.getOrganizationParticipation().getId() ) );
                } else {
                    multiCommand.addCommand( new UpdatePlanObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "orgParticipationId" ),
                            null ) );
                }
                doCommand( multiCommand );
            }
        } else {
            doCommand( new UpdatePlanObject(
                    getUsername(),
                    requirement,
                    getAgentSpecPath( "actor" ),
                    null ) );
        }
        requirement.initialize( planCommunity );
    }

    public String getAgencyName() {
        Agency agency = getAgentSpec().getAgency();
        return agency == null ? null : agency.getName();
    }

    public void setAgencyName( String name ) {
        PlanCommunity planCommunity = getPlanCommunity();
        Requirement requirement = getRequirement();
        if ( name != null && !name.isEmpty() ) {
            Agency agency = planCommunity.getParticipationManager().findAgencyNamed( name, planCommunity );
            if ( agency != null ) {
                if ( agency.isFixedOrganization() ) {
                    doCommand( new UpdatePlanObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "fixedOrgId" ),
                            agency.getFixedOrganization().getId()
                    ) );
                } else if ( agency.isParticipatingAsPlaceholder() ) {
                    doCommand( new UpdatePlanObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "orgParticipationId" ),
                            agency.getOrganizationParticipation().getId()
                    ) );
                }
            }
        } else {
            MultiCommand multiCommand = new MultiCommand( getUsername(), "Unset required agency"  );
            multiCommand.setChange( new Change( Change.Type.Updated, requirement ) ); // set the summary change else produces none and no journaling
            multiCommand.makeUndoable( false );
            multiCommand.addCommand( new UpdatePlanObject(
                    getUsername(),
                    requirement,
                    getAgentSpecPath( "fixedOrgId" ),
                    null
            ) );
            if ( getAgentName() == null ) {
                multiCommand.addCommand( new UpdatePlanObject(
                        getUsername(),
                        requirement,
                        getAgentSpecPath( "orgParticipationId" ),
                        null
                ) );
            }
            doCommand( multiCommand );
        }
        requirement.initialize( planCommunity );
    }

    public String getPlaceholderName() {
        Organization placeholder = getAgentSpec().getPlaceholder();
        return placeholder == null ? null : placeholder.getName();
    }

    public void setPlaceholderName( String name ) {
        PlanCommunity planCommunity = getPlanCommunity();
        Requirement requirement = getRequirement();
        Organization placeholder = null;
        if ( name != null && !name.isEmpty() ) {
            placeholder = planCommunity.getPlanService().findActualEntity( Organization.class, name );
            if ( placeholder != null )
                placeholder = placeholder.isPlaceHolder() ? placeholder : null;
        }
        doCommand( new UpdatePlanObject(
                getUsername(),
                requirement,
                getAgentSpecPath( "placeholder" ),
                placeholder ) );
        requirement.initialize( planCommunity );
    }

    public Requirement getRequirement() {
        Requirement req = (Requirement) getModel().getObject();
        req.initialize( getPlanCommunity() );
        return req;
    }
}
