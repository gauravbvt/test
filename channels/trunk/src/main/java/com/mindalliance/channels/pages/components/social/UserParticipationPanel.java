package com.mindalliance.channels.pages.components.social;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.PlanParticipationValidation;
import com.mindalliance.channels.core.dao.user.PlanParticipationValidationService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
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

    private Organization selectedSupervisedParticipationOrg;


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
        addOpenAndClosedParticipation();
        addSupervisedParticipation();
        addParticipationValidations();
    }

    private void addUserRole() {
        userParticipationContainer.add( new Label( "userRole", getUserRole() ) );
    }

    private void addUserParticipationContainer() {
        userParticipationContainer = new WebMarkupContainer( "userParticipation" );
        userParticipationContainer.setOutputMarkupId( true );
        addOrReplace( userParticipationContainer );
    }


    private void addOpenAndClosedParticipation() {
        List<ParticipationWrapper> participationWrappers = openAndClosedParticipationWrappers();
        WebMarkupContainer openAndClosedParticipationContainer = new WebMarkupContainer( "openAndClosedParticipation" );
        openAndClosedParticipationContainer.setVisible( !openAndClosedParticipationWrappers().isEmpty() );
        userParticipationContainer.add( openAndClosedParticipationContainer );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getActor( getQueryService() ) );
                AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                        "confirmed",
                        new PropertyModel<Boolean>( participationWrapper, "confirmed" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAll();
                        getPlanManager().clearCache();
                        target.add( userParticipationContainer );
                        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
                    }
                };
                boolean open = participationWrapper.isOpen( getQueryService() );
                confirmedCheckBox.setEnabled( open );
                confirmedCheckBox.add( new AttributeModifier(
                        "title",
                        open ? "Open participation" : "Planned participation"
                ) );
                item.add( confirmedCheckBox );
                item.add( new Label( "participation", assignation ) );
            }
        };
        openAndClosedParticipationContainer.add( participationList );
    }

    private List<ParticipationWrapper> openAndClosedParticipationWrappers() {
        final QueryService queryService = getQueryService();
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        final List<PlanParticipation> currentParticipations = unsupervisedParticipations();
        for ( PlanParticipation participation : currentParticipations ) {
            wrappers.add( new ParticipationWrapper( participation, true ) );
        }
        for ( Actor actor : planParticipationService.findOpenActors( getUser(), getQueryService() ) ) {
            PlanParticipation openParticipation = new PlanParticipation(
                    getUsername(),
                    getPlan(),
                    getUser(),
                    actor );
            wrappers.add( new ParticipationWrapper( openParticipation, false ) );
        }
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

    private void addSupervisedParticipation() {
        List<ParticipationWrapper> participationWrappers = supervisedParticipationWrappers();
        WebMarkupContainer supervisedParticipationContainer = new WebMarkupContainer( "supervisedParticipation" );
        supervisedParticipationContainer.setOutputMarkupId( true );
        userParticipationContainer.add( supervisedParticipationContainer );
        ListView<ParticipationWrapper> participationList = new ListView<ParticipationWrapper>(
                "participations",
                participationWrappers ) {
            @Override
            protected void populateItem( ListItem<ParticipationWrapper> item ) {
                ParticipationWrapper participationWrapper = item.getModelObject();
                String assignation = getAssignation( participationWrapper.getActor( getQueryService() ) );
                item.add( new Label( "participation", assignation ) );
                AjaxCheckBox confirmedCheckBox = new AjaxCheckBox(
                        "confirmed",
                        new PropertyModel<Boolean>( participationWrapper, "confirmed" )
                ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        resetAllAndUpdate( target );
                    }
                };
                item.add( confirmedCheckBox );
                Label validatedLabel = new Label(
                        "validated",
                        participationWrapper.isConfirmedByAllSupervisors( getQueryService() )
                                ? "Confirmed"
                                : "Not yet confirmed"
                );
                item.add( validatedLabel );
            }
        };
        supervisedParticipationContainer.add( participationList );
        WebMarkupContainer newParticipationContainer = new WebMarkupContainer( "newParticipation" );
        newParticipationContainer.setOutputMarkupId( true );
        supervisedParticipationContainer.add( newParticipationContainer );
        addSupervisedPartipationOrgChoice( newParticipationContainer, participationWrappers );
        addSupervisedParticipationAgentChoice( newParticipationContainer, participationWrappers );
        supervisedParticipationContainer.setVisible( !( participationWrappers.isEmpty()
                && organisationsWithSupervisedParticipation( participationWrappers ).isEmpty() ) );
    }

    private void resetAllAndUpdate( AjaxRequestTarget target ) {
        resetAll();
        getPlanManager().clearCache();
        target.add( userParticipationContainer );
        update( target, new Change( Change.Type.Updated, getPlan(), "participation" ) );
    }

    private void addSupervisedPartipationOrgChoice(
            final WebMarkupContainer container,
            final List<ParticipationWrapper> participationWrappers ) {
        List<Organization> orgsWithSupervisedParticipation = organisationsWithSupervisedParticipation( participationWrappers );
        DropDownChoice<Organization> orgChoice = new DropDownChoice<Organization>(
                "orgs",
                new PropertyModel<Organization>( this, "selectedSupervisedParticipationOrg" ),
                orgsWithSupervisedParticipation,
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
                addSupervisedParticipationAgentChoice( container, participationWrappers );
                target.add( container );
            }
        } );
        orgChoice.setOutputMarkupId( true );
        container.setVisible( !orgsWithSupervisedParticipation.isEmpty() );
        container.addOrReplace( orgChoice );
    }

    private void addSupervisedParticipationAgentChoice( WebMarkupContainer container,
                                                        List<ParticipationWrapper> participationWrappers ) {
        List<Actor> supervisedParticipationActors = actorsWithSupervisedParticipation( participationWrappers );
        DropDownChoice<Actor> actorChoice = new DropDownChoice<Actor>(
                "agents",
                new PropertyModel<Actor>( this, "participationAsSupervisedActor" ),
                supervisedParticipationActors,
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
        actorChoice.setOutputMarkupId( true );
        container.addOrReplace( actorChoice );
    }

    private List<Actor> actorsWithSupervisedParticipation( List<ParticipationWrapper> participationWrappers ) {
        List<Actor> actors = new ArrayList<Actor>();
        if ( selectedSupervisedParticipationOrg != null ) {
            for ( Job job : selectedSupervisedParticipationOrg.getJobs() ) {
                final Actor actor = job.getActor();
                if ( actor.isSupervisedParticipation() && !CollectionUtils.exists(
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
    private List<Organization> organisationsWithSupervisedParticipation(
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
                                        return actor.isSupervisedParticipation()
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

    private List<ParticipationWrapper> supervisedParticipationWrappers() {
        final QueryService queryService = getQueryService();
        List<ParticipationWrapper> wrappers = new ArrayList<ParticipationWrapper>();
        final List<PlanParticipation> participations = supervisedParticipations();
        for ( PlanParticipation participation : participations ) {
            wrappers.add( new ParticipationWrapper( participation, true ) );
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
    private List<PlanParticipation> supervisedParticipations() {
        return (List<PlanParticipation>) CollectionUtils.select( planParticipationService.getUserParticipations(
                getPlan(),
                getUser().getUserInfo(),
                getQueryService() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (PlanParticipation) object ).isSupervised( getQueryService() );
                    }
                } );
    }

    private void addParticipationValidations() {
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


    public Organization getSelectedSupervisedParticipationOrg() {
        return selectedSupervisedParticipationOrg;
    }

    public void setSelectedSupervisedParticipationOrg( Organization selectedSupervisedParticipationOrg ) {
        this.selectedSupervisedParticipationOrg = selectedSupervisedParticipationOrg;
    }

    public Actor getParticipationAsSupervisedActor() {
        return null;
    }

    public void setParticipationAsSupervisedActor( Actor actor ) {
        if ( actor.isSupervisedParticipation() ) {
            PlanParticipation participation = new PlanParticipation(
                    getUsername(),
                    getPlan(),
                    getUser(),
                    actor );
            planParticipationService.save( participation );
        }
        selectedSupervisedParticipationOrg = null;
    }

    public class ParticipationWrapper implements Serializable {

        private PlanParticipation participation;
        private boolean confirmed;

        public ParticipationWrapper( PlanParticipation participation, boolean confirmed ) {
            this.participation = participation;
            this.confirmed = confirmed;
        }

        public PlanParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( PlanParticipation participation ) {
            this.participation = participation;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public void setConfirmed( boolean confirmed ) {
            QueryService queryService = getQueryService();
            this.confirmed = confirmed;
            if ( confirmed ) {
                if ( getQueryService().getPlanParticipationService().isParticipationOpenAndAvailable(
                        participation.getActor( getQueryService() ),
                        getUser(),
                        queryService ) ) {
                    planParticipationService.save( participation );
                }
            } else {
                planParticipationService.deleteParticipation(
                        getPlan(),
                        participation.getParticipant(),
                        participation.getActor( queryService ),
                        queryService );
            }
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
