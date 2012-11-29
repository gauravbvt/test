package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.participation.PlanParticipation;
import com.mindalliance.channels.core.participation.PlanParticipationService;
import com.mindalliance.channels.core.participation.PlanParticipationValidation;
import com.mindalliance.channels.core.participation.PlanParticipationValidationService;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/20/12
 * Time: 4:34 PM
 */
public class UserParticipationPanel extends AbstractSocialListPanel {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( UserParticipationPanel.class );

    @SpringBean
    private ChannelsUser user;

    @SpringBean
    private PlanParticipationService planParticipationService;

    @SpringBean
    private PlanParticipationValidationService planParticipationValidationService;
    private WebMarkupContainer userParticipationContainer;

    private Organization selectedAvailableParticipationOrg;
    private WebMarkupContainer newActorContainer;


    public UserParticipationPanel( String id, SocialPanel socialPanel, boolean collapsible ) {
        super( id, collapsible );
        init();
    }

    protected void init() {
        super.init();
        resetAll();
    }

    private void resetAll() {
        addUserParticipationContainer();
        addUserRole();
        addOpenAndConfirmedParticipation();
        addToBeConfirmedParticipation();
        addParticipationToConfirm();
    }

    private void addUserRole() {
        userParticipationContainer.add( new Label( "userRole", getUserRole() ) );
    }

    private void addUserParticipationContainer() {
        userParticipationContainer = new WebMarkupContainer( "userParticipation" );
        userParticipationContainer.setOutputMarkupId( true );
        addOrReplace( userParticipationContainer );
    }


    private void addOpenAndConfirmedParticipation() {
        List<ParticipationWrapper> participationWrappers = openAndConfirmedParticipationWrappers();
        WebMarkupContainer openAndConfirmedParticipationContainer = new WebMarkupContainer( "openAndConfirmedParticipation" );
        // openAndConfirmedParticipationContainer.setVisible( !participationWrappers.isEmpty() );
        userParticipationContainer.add( openAndConfirmedParticipationContainer );
        Label noParticipationLabel = new Label( "noParticipation", "No one yet" );
        openAndConfirmedParticipationContainer.add( noParticipationLabel );
        noParticipationLabel.setVisible( participationWrappers.isEmpty() );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getActor( getQueryService() ) );
                AjaxCheckBox acceptedCheckBox = new AjaxCheckBox(
                        "accepted",
                        new PropertyModel<Boolean>( participationWrapper, "accepted" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAll();
                        getPlanManager().clearCache();
                        target.add( userParticipationContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
                    }
                };
                item.add( acceptedCheckBox );
                item.add( new Label( "participation", assignation ) );
                item.add( ( new Label(
                        "requester",
                        participationWrapper.isRequested()
                                ? ( " - Requested by " + participationWrapper.getRequesterFullName() )
                                : ""
                ) ) );
            }
        };
        openAndConfirmedParticipationContainer.add( participationList );
        // new participation
        WebMarkupContainer newParticipationContainer = new WebMarkupContainer( "newParticipation" );
        newParticipationContainer.setOutputMarkupId( true );
        openAndConfirmedParticipationContainer.add( newParticipationContainer );
        newParticipationContainer.add(
                new Label(
                        "newParticipationLabel",
                        participationWrappers.isEmpty()
                                ? "I participate in"
                                : "I also participate in" )
        );
        addPartipationOrgChoice( newParticipationContainer, participationWrappers );
        addParticipationAgentChoice( newParticipationContainer, participationWrappers );
    }


    private List<ParticipationWrapper> openAndConfirmedParticipationWrappers() {
        final QueryService queryService = getQueryService();
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        for ( PlanParticipation participation : unconstrainedUnacceptedParticipations() ) {
            wrappers.add(  new ParticipationWrapper(  participation ) );
        }
        final List<PlanParticipation> unsupervisedParticipations = unsupervisedParticipations();
        for ( PlanParticipation participation : unsupervisedParticipations ) {
            wrappers.add( new ParticipationWrapper( participation ) );
        }
        final List<PlanParticipation> confirmedSupervisedParticipations = confirmedSupervisedParticipations();
        for ( PlanParticipation participation : confirmedSupervisedParticipations ) {
            wrappers.add( new ParticipationWrapper( participation ) );
        }
/*
        for ( Actor actor : planParticipationService.findOpenActors( getUser(), getQueryService() ) ) {
            PlanParticipation openParticipation = new PlanParticipation(
                    getUsername(),
                    getPlan(),
                    getUser(),
                    actor );
            wrappers.add( new ParticipationWrapper( openParticipation ) );
        }
*/
        Collections.sort(
                wrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                        boolean p1Open = p1.isOpen( queryService );
                        boolean p2Open = p2.isOpen( queryService );
                        if ( !p1Open && p2Open ) return -1;
                        if ( p1Open && !p2Open ) return 1;
                        return p1.getActor( queryService ).getName()
                                .compareTo( p2.getActor( queryService ).getName() );
                    }
                } );
        return wrappers;
    }

    private List<PlanParticipation> unconstrainedUnacceptedParticipations() {
        List<PlanParticipation> participations = new ArrayList<PlanParticipation>(  );
        for ( Actor actor : planParticipationService.findOpenActors( getUser(), getQueryService() ) ) {
            if ( actor.isUnconstrainedParticipation() ) {
                participations.add( new PlanParticipation(  getUsername(),  getPlan(),  getUser(),  actor ) );
            }
        }
        return participations;
    }

    @SuppressWarnings( "unchecked" )
    private List<PlanParticipation> unsupervisedParticipations() {
        return (List<PlanParticipation>) CollectionUtils.select( planParticipationService.getUserParticipations(
                getPlan(),
                getUser().getUserInfo(),
                getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (PlanParticipation) object ).isSupervised( getQueryService() );
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<PlanParticipation> confirmedSupervisedParticipations() {
        final QueryService queryService = getQueryService();
        return (List<PlanParticipation>) CollectionUtils.select( planParticipationService.getUserParticipations(
                getPlan(),
                getUser().getUserInfo(),
                getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation participation = (PlanParticipation) object;
                        return ( participation.isSupervised( queryService )
                                && planParticipationService.isValidatedByAllSupervisors( participation, queryService ) );
                    }
                } );
    }


    private String getUserRole() {
        String userRole = user.isAdmin()
                ? "administrator"
                : user.isPlanner()
                ? "planner"
                : "participant";
        return ( ChannelsUtils.startsWithVowel( userRole ) ? " an " : " a " ) + userRole + ".";
    }


    private String getAssignation( Actor actor ) {
        StringBuilder sb = new StringBuilder();
        if ( actor != null ) {
            sb.append( actor.getName() );
            String channelsString = actor.getChannelsString();
            if ( !channelsString.isEmpty() ) {
                sb.append( ", reachable via " );
                sb.append( channelsString );
            }
            sb.append( '.' );
        }
        return sb.toString();
    }

    private void addToBeConfirmedParticipation() {
        List<ParticipationWrapper> participationWrappers = unconfirmedSupervisedParticipationWrappers();
        WebMarkupContainer unconfirmedSupervisedParticipationContainer =
                new WebMarkupContainer( "unconfirmedSupervisedParticipation" );
        unconfirmedSupervisedParticipationContainer.setOutputMarkupId( true );
        userParticipationContainer.add( unconfirmedSupervisedParticipationContainer );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getActor( getQueryService() ) );
                item.add( new Label( "participation", assignation ) );
                AjaxCheckBox acceptedCheckBox = new AjaxCheckBox(
                        "accepted",
                        new PropertyModel<Boolean>( participationWrapper, "accepted" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAllAndUpdate( target );
                    }
                };
                item.add( acceptedCheckBox );
                item.add( ( new Label(
                        "requester",
                        participationWrapper.isRequested()
                                ? ( " - Requested by " + participationWrapper.getRequesterFullName() )
                                : ""
                ) ) );
            }
        };
        unconfirmedSupervisedParticipationContainer.add( participationList );
        unconfirmedSupervisedParticipationContainer.setVisible( !participationWrappers.isEmpty() );
    }

    private void resetAllAndUpdate( AjaxRequestTarget target ) {
        resetAll();
        getPlanManager().clearCache();
        target.add( userParticipationContainer );
        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
    }

    private void addPartipationOrgChoice(
            final WebMarkupContainer container,
            final List<ParticipationWrapper> participationWrappers ) {
        List<Organization> orgsWithAvailableParticipation = organisationsWithAvailableParticipation( participationWrappers );
        DropDownChoice<Organization> orgChoice = new DropDownChoice<Organization>(
                "orgs",
                new PropertyModel<Organization>( this, "selectedAvailableParticipationOrg" ),
                orgsWithAvailableParticipation,
                new ChoiceRenderer<Organization>() {
                    @Override
                    public Object getDisplayValue( Organization org ) {
                        return org.getName();
                    }

                    @Override
                    public String getIdValue( Organization object, int index ) {
                        return Integer.toString( index );
                    }
                } );
        orgChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addParticipationAgentChoice( container, participationWrappers );
                target.add( container );
            }
        } );
        orgChoice.setOutputMarkupId( true );
        container.setVisible( !orgsWithAvailableParticipation.isEmpty() );
        container.addOrReplace( orgChoice );
    }

    private void addParticipationAgentChoice( WebMarkupContainer container,
                                              List<ParticipationWrapper> participationWrappers ) {
        newActorContainer = new WebMarkupContainer( "asAgent" );
        newActorContainer.setOutputMarkupId( true );
        makeVisible( newActorContainer, selectedAvailableParticipationOrg != null );
        container.addOrReplace( newActorContainer );
        List<Actor> availableParticipationActors = actorsWithAvailableParticipation( participationWrappers );
        DropDownChoice<Actor> actorChoice = new DropDownChoice<Actor>(
                "agents",
                new PropertyModel<Actor>( this, "participationAsAvailableActor" ),
                availableParticipationActors,
                new ChoiceRenderer<Actor>() {
                    @Override
                    public Object getDisplayValue( Actor actor ) {
                        return actor.getName();
                    }

                    @Override
                    public String getIdValue( Actor actor, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        actorChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                resetAllAndUpdate( target );
            }
        } );
        newActorContainer.add( actorChoice );
    }

    private List<Actor> actorsWithAvailableParticipation( List<ParticipationWrapper> participationWrappers ) {
        List<Actor> actors = new ArrayList<Actor>();
        if ( selectedAvailableParticipationOrg != null ) {
            for ( Job job : selectedAvailableParticipationOrg.getJobs() ) {
                final Actor actor = job.getActor();
                if ( isActorAvailableForParticipation( actor )
                        && !CollectionUtils.exists(
                        participationWrappers,
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return ( (ParticipationWrapper) object ).getParticipation().getActorId() == actor.getId();
                            }
                        }
                ) ) {
                    actors.add( actor );
                }
            }
        }
        Collections.sort( actors, new Comparator<Actor>() {
            @Override
            public int compare( Actor a1, Actor a2 ) {
                return a1.getName().compareTo( a2.getName() );
            }
        } );
        return actors;
    }

    @SuppressWarnings( "unchecked" )
    private List<Organization> organisationsWithAvailableParticipation(
            final List<ParticipationWrapper> participationWrappers ) {
        List<Organization> orgs = (List<Organization>) CollectionUtils.select(
                getQueryService().listActualEntities( Organization.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Organization org = (Organization) object;
                        return CollectionUtils.exists(
                                org.getJobs(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        final Actor actor = ( (Job) object ).getActor();
                                        return isActorAvailableForParticipation( actor )
                                                && !CollectionUtils.exists(
                                                participationWrappers,
                                                new Predicate() {
                                                    @Override
                                                    public boolean evaluate( Object object ) {
                                                        return ( (ParticipationWrapper) object ).getParticipation().getActorId()
                                                                == actor.getId();
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
        Collections.sort( orgs, new Comparator<Organization>() {
            @Override
            public int compare( Organization org1, Organization org2 ) {
                return org1.getName().compareTo( org2.getName() );

            }
        } );
        return orgs;
    }

    private List<ParticipationWrapper> unconfirmedSupervisedParticipationWrappers() {
        final QueryService queryService = getQueryService();
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        final List<PlanParticipation> participations = unconfirmedSupervisedParticipations();
        for ( PlanParticipation participation : participations ) {
            wrappers.add( new ParticipationWrapper( participation ) );
        }
        Collections.sort(
                wrappers,
                new Comparator<ParticipationWrapper>() {
                    @Override
                    public int compare( ParticipationWrapper p1, ParticipationWrapper p2 ) {
                        return p1.getActor( queryService ).getName()
                                .compareTo( p2.getActor( queryService ).getName() );
                    }
                } );
        return wrappers;
    }

    @SuppressWarnings( "unchecked" )
    private List<PlanParticipation> unconfirmedSupervisedParticipations() {
        final QueryService queryService = getQueryService();
        return (List<PlanParticipation>) CollectionUtils.select( planParticipationService.getUserParticipations(
                getPlan(),
                getUser().getUserInfo(),
                getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipation participation = (PlanParticipation) object;
                        return ( participation ).isSupervised( queryService )
                                && !planParticipationService.isValidatedByAllSupervisors( participation, queryService );
                    }
                } );
    }

    private void addParticipationToConfirm() {
        WebMarkupContainer validationsContainer = new WebMarkupContainer( "supervisorParticipationValidations" );
        userParticipationContainer.add( validationsContainer );
        List<ParticipationValidationWrapper> validationWrappers = participationValidationWrappers();
        ListView<ParticipationValidationWrapper> validationList = new ListView<ParticipationValidationWrapper>(
                "participationValidations",
                validationWrappers
        ) {
            @Override
            protected void populateItem( ListItem<ParticipationValidationWrapper> item ) {
                ParticipationValidationWrapper validationWrapper = item.getModelObject();
                AjaxCheckBox validatedCheckBox = new AjaxCheckBox(
                        "validated",
                        new PropertyModel<Boolean>( validationWrapper, "validated" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAllAndUpdate( target );
                    }
                };
                item.add( validatedCheckBox );
                ChannelsUserInfo participatingUser = validationWrapper.getParticipatingUser();
                item.add( new Label( "user", participatingUser.getFullName() ) );
                item.add( new Label( "email", participatingUser.getEmail() ) );
                Actor participationActor = validationWrapper.getActor( getQueryService() );
                item.add( new Label( "agent", participationActor == null ? "?" : participationActor.getName() ) );
            }
        };
        validationsContainer.add( validationList );
        validationsContainer.setVisible( !validationWrappers.isEmpty() );
    }

    @SuppressWarnings( "unchecked" )
    private List<ParticipationValidationWrapper> participationValidationWrappers() {
        final QueryService queryService = getQueryService();
        List<ParticipationValidationWrapper> wrappers = new ArrayList<ParticipationValidationWrapper>();
        final List<PlanParticipationValidation> allValidations =
                planParticipationValidationService.getParticipationValidations( getPlan() );
        final List<Actor> userActors = planParticipationService.listActorsUserParticipatesAs(
                getPlan(),
                getUser(),
                queryService );
        // Find all plan participation validation made by a supervisor user participates as (= confirmed)
        List<PlanParticipationValidation> userValidations = (List<PlanParticipationValidation>) CollectionUtils.select(
                allValidations,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        PlanParticipationValidation validation = (PlanParticipationValidation) object;
                        Actor supervisor = validation.getSupervisor( queryService );
                        return supervisor != null && userActors.contains( supervisor );
                    }
                }
        );
        for ( PlanParticipationValidation userValidation : userValidations ) {
            wrappers.add( new ParticipationValidationWrapper( userValidation, true ) );
        }
        // Find all plan participation validations user needs to confirm as supervisor
        List<PlanParticipation> planParticipationAwaitingUserValidation =
                (List<PlanParticipation>) CollectionUtils.select(
                        planParticipationService
                                .getParticipationsSupervisedByUser( getUser(), getPlan(), queryService ),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                final PlanParticipation supervisedParticipation = (PlanParticipation) object;
                                return !CollectionUtils.exists(
                                        allValidations,
                                        new Predicate() {
                                            @Override
                                            public boolean evaluate( Object object ) {
                                                PlanParticipationValidation validation = (PlanParticipationValidation) object;
                                                Actor supervisor = validation.getSupervisor( queryService );
                                                return validation.getPlanParticipation()
                                                        .equals( supervisedParticipation )
                                                        && supervisor != null
                                                        && userActors.contains( supervisor );
                                            }
                                        }
                                );
                            }
                        } );
        for ( PlanParticipation participationToBeValidated : planParticipationAwaitingUserValidation ) {
            PlanParticipationValidation validationToBe = new PlanParticipationValidation(
                    participationToBeValidated,
                    null,
                    getUsername() );
            wrappers.add( new ParticipationValidationWrapper( validationToBe, false ) );
        }
        return wrappers;
    }


    public Organization getSelectedAvailableParticipationOrg() {
        return selectedAvailableParticipationOrg;
    }

    public void setSelectedAvailableParticipationOrg( Organization selectedAvailableParticipationOrg ) {
        this.selectedAvailableParticipationOrg = selectedAvailableParticipationOrg;
    }

    public Actor getParticipationAsAvailableActor() {
        return null;
    }

    public void setParticipationAsAvailableActor( Actor actor ) {
        if ( isActorAvailableForParticipation( actor ) ) {
            planParticipationService.addAcceptedParticipation(
                    getUsername(),
                    getPlan(),
                    getUser(),
                    actor );
        }
        selectedAvailableParticipationOrg = null;
    }

    private boolean isActorAvailableForParticipation( Actor actor ) {
        return planParticipationService.isParticipationOpenAndAvailable( actor, getUser(), getQueryService() );
    }

    public class ParticipationWrapper implements Serializable {

        private PlanParticipation participation;

        public ParticipationWrapper( PlanParticipation participation ) {
            this.participation = participation;
        }

        public PlanParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( PlanParticipation participation ) {
            this.participation = participation;
        }

        public boolean isAccepted() {
            return participation.isAccepted();
        }

        public void setAccepted( boolean accepted ) {
            QueryService queryService = getQueryService();
            if ( accepted ) {
                planParticipationService.accept( participation );
            } else {
                if ( isRequested() ) {
                    planParticipationService.refuse( participation );
                } else {
                    planParticipationService.deleteParticipation(
                            getPlan(),
                            participation.getParticipant(),
                            participation.getActor( queryService ),
                            queryService );
                }
            }
            selectedAvailableParticipationOrg = null;
            getPlanManager().clearCache(); // Must manually clear the cache
        }

        public boolean isConfirmedByAllSupervisors( QueryService queryService ) {
            return planParticipationService.isValidatedByAllSupervisors( participation, queryService );
        }

        public Actor getActor( QueryService queryService ) {
            return participation.getActor( queryService );
        }

        public boolean isOpen( QueryService queryService ) {
            return getActor( queryService ).isOpenParticipation();
        }

        public boolean isRequested() {
            return !participation.getUsername().equals( getUser().getUsername() );
        }

        public String getRequesterFullName() {
            ChannelsUser requestingUser = getQueryService().getUserDao().getUserNamed( participation.getUsername() );
            return requestingUser == null
                    ? "?"
                    : requestingUser.getFullName();
        }
    }

    public class ParticipationValidationWrapper implements Serializable {
        private PlanParticipationValidation participationValidation;
        private boolean validated;

        public ParticipationValidationWrapper(
                PlanParticipationValidation participationValidation,
                boolean validated ) {
            this.validated = validated;
            this.participationValidation = participationValidation;
        }

        public boolean isValidated() {
            return validated;
        }

        public void setValidated( boolean validated ) {
            this.validated = validated;
            for ( Actor supervisor : planParticipationService.listSupervisorsUserParticipatesAs(
                    participationValidation.getPlanParticipation(),
                    getPlan(),
                    getUser(),
                    getQueryService()
            ) ) {
                if ( validated ) {
                    planParticipationValidationService.addParticipationValidation(
                            participationValidation.getPlanParticipation(),
                            supervisor,
                            getUser() );
                } else {
                    planParticipationValidationService.removeParticipationValidation(
                            participationValidation.getPlanParticipation(),
                            supervisor );
                }
            }
        }

        public ChannelsUserInfo getParticipatingUser() {
            return participationValidation.getPlanParticipation().getParticipant();
        }

        public Actor getActor( QueryService queryService ) {
            return participationValidation.getPlanParticipation().getActor( queryService );
        }
    }


}
