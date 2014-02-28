package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.MultiCommand;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
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
    private AutoCompleteTextField<String> jurisdictionField;

    public RequiredWhoPanel( String id, Model<Requirement> requirementModel, boolean isBeneficiary ) {
        super( id, requirementModel );
        this.isBeneficiary = isBeneficiary;
        init();
    }

    private void init() {
        addAgentField();
//        addJurisdictionField();
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
                // addJurisdictionField();
                addAgencyField();
                addPlaceholderField();
                adjustFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        addOrReplace( agentField );
    }

 /*   private void addJurisdictionField() {
        final List<String> choices = getAllJurisdictionNames();
        jurisdictionField = new AutoCompleteTextField<String>(
                "jurisdiction",
                new PropertyModel<String>( this, "jurisdictionName" )
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
        jurisdictionField.setOutputMarkupId( true );
        jurisdictionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement() ) );
            }
        } );
        addOrReplace( jurisdictionField );
    }
*/
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
        for ( Agent agent : getCommunityService().getParticipationManager().getAllKnownAgents( getCommunityService() ) ) {
            names.add( agent.getName() );
        }
        Collections.sort( names );
        return names;
    }

    private List<String> getAllJurisdictionNames() {
        List<String> names = new ArrayList<String>();
        for ( Place place : getCommunityService().getModelService().listActualEntities( Place.class, true ) ) {
            if ( !place.isUnknown() )
                names.add( place.getName() );
        }
        Collections.sort( names );
        return names;
    }


    @SuppressWarnings( "unchecked" )
    private List<String> getAllAgencyNames() {
        List<String> names = new ArrayList<String>();
        for ( Agency agency :
                getCommunityService().getParticipationManager().getAllKnownAgencies( getCommunityService() ) ) {
            if ( agency.getRegisteredOrganization().isFixedOrganization() || agency.isParticipatingAsAPlaceholder() ) {
                names.add( agency.getName() );
            }
        }
        Collections.sort( names );
        return names;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> getAllPlaceholderNames() {
        List<String> names = new ArrayList<String>();
        for ( Organization organization : getCommunityService().getModelService().listActualEntities( Organization.class ) ) {
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
        CommunityService communityService = getCommunityService();
        Requirement requirement = getRequirement();
        if ( name != null && !name.isEmpty() ) {
            Agent agent = communityService.getParticipationManager().findAgentNamed( name, communityService );
            if ( agent != null ) {
                MultiCommand multiCommand = new MultiCommand( getUsername(), "Set required agent" );
                multiCommand.makeUndoable( false );
                multiCommand.setChange( new Change( Change.Type.Updated, requirement ) ); // set the summary change else produces none and no journaling
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        requirement,
                        getAgentSpecPath( "actor" ),
                        agent.getActor() ) );
               multiCommand.addCommand( new UpdateModelObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "registeredOrgId" ),
                            agent.getAgency().getRegisteredOrganizationUid() ) );
                doCommand( multiCommand );
            }
        } else {
            doCommand( new UpdateModelObject(
                    getUsername(),
                    requirement,
                    getAgentSpecPath( "actor" ),
                    null ) );
        }
        requirement.initialize( communityService );
    }

    public String getAgencyName() {
        Agency agency = getAgentSpec().getAgency();
        return agency == null ? null : agency.getName();
    }

    public void setAgencyName( String name ) {
        CommunityService communityService = getCommunityService();
        Requirement requirement = getRequirement();
        if ( name != null && !name.isEmpty() ) {
            Agency agency = communityService.getParticipationManager().findAgencyNamed( name, communityService );
            if ( agency != null ) {
                if ( agency.getRegisteredOrganization().isFixedOrganization() ) {
                    doCommand( new UpdateModelObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "fixedOrgId" ),
                            agency.getRegisteredOrganization().getFixedOrganizationId()
                    ) );
                } else {
                    doCommand( new UpdateModelObject(
                            getUsername(),
                            requirement,
                            getAgentSpecPath( "registeredOrgId" ),
                            agency.getRegisteredOrganization().getUid()
                    ) );
                }
            }
        } else {
            MultiCommand multiCommand = new MultiCommand( getUsername(), "Unset required agency"  );
            multiCommand.setChange( new Change( Change.Type.Updated, requirement ) ); // set the summary change else produces none and no journaling
            multiCommand.makeUndoable( false );
            multiCommand.addCommand( new UpdateModelObject(
                    getUsername(),
                    requirement,
                    getAgentSpecPath( "fixedOrgId" ),
                    null
            ) );
            if ( getAgentName() == null ) {
                multiCommand.addCommand( new UpdateModelObject(
                        getUsername(),
                        requirement,
                        getAgentSpecPath( "registeredOrgId" ),
                        null
                ) );
            }
            doCommand( multiCommand );
        }
        requirement.initialize( communityService );
    }

    public String getPlaceholderName() {
        Organization placeholder = getAgentSpec().getPlaceholder();
        return placeholder == null ? null : placeholder.getName();
    }

    public void setPlaceholderName( String name ) {
        CommunityService communityService = getCommunityService();
        Requirement requirement = getRequirement();
        Organization placeholder = null;
        if ( name != null && !name.isEmpty() ) {
            placeholder = communityService.getModelService().findActualEntity( Organization.class, name );
            if ( placeholder != null )
                placeholder = placeholder.isPlaceHolder() ? placeholder : null;
        }
        doCommand( new UpdateModelObject(
                getUsername(),
                requirement,
                getAgentSpecPath( "placeholder" ),
                placeholder ) );
        requirement.initialize( communityService );
    }

/*
    public String getJurisdictionName() {
        Place jurisdiction = getAgentSpec().getJurisdiction();
        return jurisdiction == null ? null : jurisdiction.getName();
    }

    public void setJurisdictionName( String name ) {
        PlanCommunity planCommunity = getCommunityService();
        Requirement requirement = getRequirement();
        Place jurisdiction = null;
        if ( name != null && !name.isEmpty() ) {
            jurisdiction = planCommunity.getPlanService().findActualEntity( Place.class, name );
        }
        doCommand( new UpdatePlanObject(
                getUsername(),
                requirement,
                getAgentSpecPath( "jurisdiction" ),
                jurisdiction ) );
        requirement.initialize( planCommunity );
    }
*/


    public Requirement getRequirement() {
        Requirement req = (Requirement) getModel().getObject();
        req.initialize( getCommunityService() );
        return req;
    }
}
