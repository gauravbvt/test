package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User participations panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2010
 * Time: 12:57:54 PM
 */
public class ParticipationsPanel extends AbstractCommandablePanel implements NameRangeable {  // todo - COMMUNITY - remove

    @SpringBean
    private ParticipationManager participationManager;

    @SpringBean
    private UserRecordService userInfoService;

    private static final int MAX_ROWS = 10;
    private static String USERNAMES = "Users";
    private static String FULL_NAMES = "Names";
    private static String EMAILS = "Email addresses";
    /**
     * Indexing choices.
     */
    private static final String[] indexingChoices = {USERNAMES, FULL_NAMES, EMAILS};
    /**
     * What "column" to index names on.
     */
    private String indexedOn;
    private ParticipationsTable participationsTable;
    private NameRangePanel nameRangePanel;
    private NameRange nameRange = new NameRange();
    private boolean onlyWithAgents;
    private boolean onlyWithoutAgents;
    private CheckBox withoutAgentCheckBox;
    private CheckBox withAgentCheckBox;
    private ParticipationWrapper selectedParticipation;
    private WebMarkupContainer participationDiv;
    private ParticipationWrapper addedParticipationWrapper;

    public ParticipationsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        indexedOn = indexingChoices[0];
        resetParticipations();
        addIndexedOnChoices();
        addRangesPanel();
        addParticipationsTable();
        addWithAgentCheckBox();
        addWithoutAgentCheckBox();
        addParticipation();
    }

    private void resetParticipations() {
        addedParticipationWrapper = null;
    }

    private void addIndexedOnChoices() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                Arrays.asList( indexingChoices ) );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addRangesPanel();
                addParticipationsTable();
                target.add( nameRangePanel );
                target.add( participationsTable );
            }
        } );
        add( indexedOnChoices );
    }

    private void addRangesPanel() {
        nameRangePanel = new NameRangePanel(
                "ranges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All" );
        nameRangePanel.setOutputMarkupId( true );
        addOrReplace( nameRangePanel );
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<ParticipationWrapper> participations = getParticipations();
        if ( indexedOn.equals( USERNAMES ) ) {
            return (List<String>) CollectionUtils.collect(
                    participations,
                    new Transformer() {
                        public Object transform( Object object ) {
                            return ( (ParticipationWrapper) object ).getUsername();
                        }
                    }
            );
        } else if ( indexedOn.equals( FULL_NAMES ) ) {
            return (List<String>) CollectionUtils.collect(
                    participations,
                    new Transformer() {
                        public Object transform( Object object ) {
                            return ( (ParticipationWrapper) object ).getUserNormalizedFullName();
                        }
                    }
            );
        } else if ( indexedOn.equals( EMAILS ) ) {
            return (List<String>) CollectionUtils.collect(
                    participations,
                    new Transformer() {
                        public Object transform( Object object ) {
                            return ( (ParticipationWrapper) object ).getUserEmail();
                        }
                    }
            );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    private void addParticipationsTable() {
        participationsTable = new ParticipationsTable(
                "participationsTable",
                new PropertyModel<List<ParticipationWrapper>>( this, "participations" )
        );
        participationsTable.setOutputMarkupId( true );
        addOrReplace( participationsTable );
    }

    private void addWithAgentCheckBox() {
        withAgentCheckBox = new CheckBox(
                "withAgentsOnly",
                new PropertyModel<Boolean>( this, "onlyWithAgents" )
        );
        withAgentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addWithoutAgentCheckBox();
                addRangesPanel();
                addParticipationsTable();
                target.add( withoutAgentCheckBox );
                target.add( nameRangePanel );
                target.add( participationsTable );
            }
        } );
        withAgentCheckBox.setOutputMarkupId( true );
        addOrReplace( withAgentCheckBox );
    }

    private void addWithoutAgentCheckBox() {
        withoutAgentCheckBox = new CheckBox(
                "withoutAgentsOnly",
                new PropertyModel<Boolean>( this, "onlyWithoutAgents" )
        );
        withoutAgentCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addWithAgentCheckBox();
                nameRange = new NameRange();
                addRangesPanel();
                addParticipationsTable();
                target.add( withAgentCheckBox );
                target.add( nameRangePanel );
                target.add( participationsTable );
            }
        } );
        withoutAgentCheckBox.setOutputMarkupId( true );
        addOrReplace( withoutAgentCheckBox );
    }

    /**
     * Get list of all participations in range.
     *
     * @return a list of participations
     */
    @SuppressWarnings( "unchecked" )
    public List<ParticipationWrapper> getParticipations() {
        return (List<ParticipationWrapper>) CollectionUtils.select(
                getAllParticipationWrappers(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        ParticipationWrapper wrapper = (ParticipationWrapper) object;
                        return ( !onlyWithAgents || wrapper.hasAgent() )
                                && ( !onlyWithoutAgents || !wrapper.hasAgent() )
                                && isInNameRange( wrapper );
                    }
                }
        );
    }

    private List<ParticipationWrapper> getAllParticipationWrappers() {
        List<ParticipationWrapper> participationWrappers = new ArrayList<ParticipationWrapper>();
        CommunityService communityService = getCommunityService();
        UserRecordService userDao = communityService.getUserRecordService();
        UserParticipationService userParticipationService = communityService.getUserParticipationService();
        for ( ChannelsUser channelsUser : userDao.getUsers( getCollaborationModel().getUri() ) ) {
            List<UserParticipation> participations = participationManager.getUserParticipations(
                     channelsUser, communityService );
            for ( UserParticipation participation : participations ) {
                userParticipationService.refresh( participation );
                ParticipationWrapper wrapper = new ParticipationWrapper(
                        channelsUser.getUsername(),
                        participation );
                participationWrappers.add( wrapper );
            }
            if ( isLockedByUser( Channels.PLAN_PARTICIPATION ) && participations.isEmpty() ) {
                participationWrappers.add( new ParticipationWrapper( channelsUser.getUsername() ) );
                if ( addedParticipationWrapper != null ) {
                    participationWrappers.add( addedParticipationWrapper );
                    addedParticipationWrapper = null;
                }
            }
        }
        return participationWrappers;
    }

    private boolean isInNameRange( ParticipationWrapper participationWrapper ) {
        if ( indexedOn.equals( USERNAMES ) ) {
            return nameRange.contains( participationWrapper.getUsername() );
        } else if ( indexedOn.equals( FULL_NAMES ) ) {
            return nameRange.contains( participationWrapper.getUserNormalizedFullName() );
        } else if ( indexedOn.equals( EMAILS ) ) {
            return nameRange.contains( participationWrapper.getUserEmail() );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    public boolean isOnlyWithAgents() {
        return onlyWithAgents;
    }

    public void setOnlyWithAgents( boolean val ) {
        if ( val ) onlyWithoutAgents = false;
        onlyWithAgents = val;
    }

    public boolean isOnlyWithoutAgents() {
        return onlyWithoutAgents;
    }

    public void setOnlyWithoutAgents( boolean val ) {
        if ( val ) onlyWithAgents = false;
        onlyWithoutAgents = val;
    }

    /**
     * Change the selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addParticipationsTable();
        target.add( participationsTable );
    }

    private void setSelectedParticipation( ParticipationWrapper pw ) {
        if ( selectedParticipation != null && selectedParticipation.getParticipation() != null ) {
            releaseAnyLockOn( selectedParticipation.getParticipation() );
        }
        selectedParticipation = pw;
        if ( selectedParticipation.getParticipation() != null ) {
            requestLockOn( selectedParticipation.getParticipation() );
        }
    }

    private void addParticipation() {
        participationDiv = new WebMarkupContainer( "participationDiv" );
        participationDiv.setOutputMarkupId( true );
        makeVisible( participationDiv, isAssignedParticipationSelected() );
        addOrReplace( participationDiv );
        addParticipationLabel();
        addParticipationLink();
        addParticipationAgentChannels();
        addUserChannels();
    }

    private boolean isAssignedParticipationSelected() {
        return selectedParticipation != null
                && selectedParticipation.getAgent() != null;
    }

    private void addParticipationLabel() {
        ParticipationWrapper pw = getParticipation();
        String label = ( pw == null )
                ? ""
                : MessageFormat.format( "Contact info for {0} ({1})",
                pw.getUserFullName(),
                pw.getUsername()
        );
        Label participationLabel = new Label( "participation", label );
        participationLabel.setOutputMarkupId( true );
        participationDiv.addOrReplace( participationLabel );
    }

    private void addParticipationLink() {
        ParticipationWrapper pw = getParticipation();
        Agent agent = ( pw == null ) ? null : pw.getAgent();
        WebMarkupContainer asAgentSpan = new WebMarkupContainer( "asAgent" );
        asAgentSpan.setOutputMarkupId( true );
        makeVisible( asAgentSpan, agent != null );
        participationDiv.addOrReplace( asAgentSpan );
        if ( agent != null ) {
            ModelObjectLink actorLink = new ModelObjectLink(
                    "actorLink",
                    new Model<Actor>( agent.getActor() ),
                    new Model<String>( agent.getName() ) );
            asAgentSpan.add( actorLink );
        } else {
            asAgentSpan.add( new Label( "actorLink", "" ) );
        }
    }

    private void addParticipationAgentChannels() {
        ParticipationWrapper pw = getParticipation();
        String channelsString = ( pw == null || pw.getAgent() == null )
                ? "None"
                : pw.getAgent().getActor().getChannelsString();
        Label label = new Label( "agentChannels", channelsString );
        label.setOutputMarkupId( true );
        participationDiv.addOrReplace( label );
    }

    private void addUserChannels() {
        ParticipationWrapper pw = getParticipation();
        String channelsString = pw != null
                ? Channel.toString( userInfoService.findChannels(
                pw.getParticipatingUserInfo(),
                getCommunityService() ) )
                : "None";
        Label label = new Label( "userChannels", channelsString );
        label.setOutputMarkupId( true );
        participationDiv.addOrReplace( label );
    }

    /**
     * Get selected participation wrapper.
     *
     * @return a participation wrapper
     */
    public ParticipationWrapper getParticipation() {
        return selectedParticipation;
    }


    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof ParticipationWrapper ) {
            ParticipationWrapper wrapper = (ParticipationWrapper) object;

            if ( action.equals( "select" ) ) {
                setSelectedParticipation( wrapper );
                addParticipation();
                target.add( participationDiv );
            } else {
                if ( action.equals( "agent set" ) ) {
                    resetParticipations();
                    addParticipationsTable();
                    target.add( participationsTable );
                    selectedParticipation = null;
                    addParticipation();
                    target.add( participationDiv );
/*                    update(
                            target,
                            new Change(
                                    Change.Type.Updated,
                                    getPlan() ) );*/
                } else if ( action.equals( "participation" ) ) {
                    ChannelsUser participatingUser
                            = getCommunityService().getUserRecordService().getUserWithIdentity( wrapper.getUsername() );
                    if ( participatingUser != null ) {
                        selectedParticipation = null;
                        addedParticipationWrapper = new ParticipationWrapper(
                                participatingUser.getUsername(),
                                new UserParticipation( getUsername(), participatingUser, getPlanCommunity() ) );
                        addParticipation();
                        target.add( participationDiv );
                        addParticipationsTable();
                        target.add( participationsTable );
                    }
                }
            }
        }
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !( change.isUpdated() && change.isForInstanceOf( Channelable.class ) ) ) {
            super.updateWith( target, change, updated );
        }
    }

    /**
     * Participation wrapper.
     */
    public class ParticipationWrapper implements Serializable {

        private String username;
        private UserParticipation participation;

        public ParticipationWrapper( String username ) {
            this.username = username;
        }

        public ParticipationWrapper( String username, UserParticipation participation ) {
            this.username = username;
            this.participation = participation;
        }

        public String getUsername() {
            return username;
        }

        public String getActive() {
            if ( participation == null ) {
                return "";
            } else {
                return getCommunityService().getParticipationManager().isActive(
                        participation,
                        getCommunityService()
                ) ? "Yes" : "Not yet";
            }
        }

        public String getAccepted() {
            if ( participation == null || !hasAgent() ) {
                return "";
            } else {
                return participation.isAccepted() ? "Yes" : "No";
            }
        }

        public UserParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( UserParticipation participation ) {
            this.participation = participation;
        }

        public boolean hasAgent() {
            return participation != null && participation.getAgent( getCommunityService() ) != null;
        }

        public String getUserFullName() {
            return getQueryService().findUserFullName( username );
        }

        public String getUserNormalizedFullName() {
            return getQueryService().findUserNormalizedFullName( username );
        }

        public String getUserEmail() {
            return getQueryService().findUserEmail( username );
        }

        public String getUserRole() {
            return getQueryService().findUserRole( username );
        }

        public Agent getAgent() {
            return participation == null ? null : participation.getAgent( getCommunityService() );
        }

        public void setAgent( Agent agent ) {
            CommunityService communityService = getCommunityService();
            UserRecordService userDao = communityService.getUserRecordService();
            UserParticipationService userParticipationService = communityService.getUserParticipationService();
            if ( participation != null ) {
                userParticipationService.removeParticipation( getUser().getUsername(), participation, communityService );
            }
            if ( agent != null ) {
                if ( getUser().getUsername().equals( username ) ) {
                    participation = userParticipationService.addAcceptedParticipation(
                            getUser().getUsername(),
                            userDao.getUserWithIdentity( username ),
                            agent,
                            communityService );
                } else {
                    participation = userParticipationService.addParticipation(
                            getUser().getUsername(),
                            userDao.getUserWithIdentity( username ),
                            agent,
                            communityService );
                }
            } else {
                participation = null;
            }
            // getPlanManager().clearCache();
        }

        public List<Agent> getDomain() {
            Set<Agent> domain = new HashSet<Agent>();
            CommunityService communityService = getCommunityService();
            List<Agent> agents = participationManager.getAllKnownAgents( communityService );
            for ( Agent agent : agents ) {
                ChannelsUser participatingUser = getParticipatingUser();
                if ( participatingUser != null
                        && participationManager.isParticipationAvailable(
                        agent,
                        getParticipatingUser(),
                        communityService ) )
                    domain.add( agent );
            }
            List<Agent> orderedDomain = new ArrayList<Agent>( domain );
            Collections.sort(
                    orderedDomain,
                    new Comparator<Agent>() {
                        @Override
                        public int compare( Agent a1, Agent a2 ) {
                            return a1.getName().compareTo( a2.getName() );
                        }
                    } );
            return orderedDomain;
        }

        public UserRecord getParticipatingUserInfo() {
            ChannelsUser participatingUser = getParticipatingUser();
            return participatingUser == null ? null : participatingUser.getUserRecord();
        }

        private ChannelsUser getParticipatingUser() {
            return getCommunityService().getUserRecordService().getUserWithIdentity( username );
        }
    }

    public class ParticipationsTable extends AbstractTablePanel {

        private IModel<List<ParticipationWrapper>> participationsModel;

        public ParticipationsTable(
                String id,
                IModel<List<ParticipationWrapper>> participationsModel ) {
            super( id );
            this.participationsModel = participationsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn(
                    "User",
                    "username",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Privileges",
                    "userRole",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Name",
                    "userNormalizedFullName",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Email",
                    "userEmail",
                    EMPTY
            ) );
            if ( isLockedByUser( Channels.PLAN_PARTICIPATION ) ) {
                columns.add( makeNameableReferenceColumn(
                        "Is agent",
                        "agent",
                        "domain",
                        Agent.class,
                        "agent set",
                        "Name an agent representing the user",
                        ParticipationsPanel.this
                ) );
            } else {
                columns.add( this.makeColumn( "Is agent", "agent.name", EMPTY ) );
            }
            columns.add( makeColumn(
                    "Accepted?",
                    "accepted",
                    EMPTY
            ) );
            columns.add( makeColumn(
                    "Active?",
                    "active",
                    EMPTY
            ) );
            columns.add( makeActionLinkColumn(
                    "",
                    "contact",
                    "select",
                    "agent",
                    "more",
                    ParticipationsPanel.this
            ) );
            if ( isLockedByUser( Channels.PLAN_PARTICIPATION ) ) {
                columns.add( makeActionLinkColumn(
                        "",
                        "add",
                        "participation",
                        "agent",
                        "more",
                        ParticipationsPanel.this ) );
            }
            // provider and table
            addOrReplace( new AjaxFallbackDefaultDataTable(
                    "participations",
                    columns,
                    new SortableBeanProvider<ParticipationWrapper>(
                            participationsModel.getObject(),
                            "username" ),
                    MAX_ROWS ) );
        }
    }
}

