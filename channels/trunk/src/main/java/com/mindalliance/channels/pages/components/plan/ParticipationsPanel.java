package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.dao.user.PlanParticipation;
import com.mindalliance.channels.core.dao.user.PlanParticipationService;
import com.mindalliance.channels.core.dao.user.UserContactInfoService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
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
import java.util.List;

/**
 * User participations panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 22, 2010
 * Time: 12:57:54 PM
 */
public class ParticipationsPanel extends AbstractCommandablePanel implements NameRangeable {

    @SpringBean
    private PlanParticipationService planParticipationService;

    @SpringBean
    private UserContactInfoService userContactInfoService;

    @SpringBean
    private ChannelsUserDao userDao;

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
    private boolean onlyWithActors;
    private boolean onlyWithoutActors;
    private CheckBox withoutActorCheckBox;
    private CheckBox withActorCheckBox;
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
        addWithActorCheckBox();
        addWithoutActorCheckBox();
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

    private void addWithActorCheckBox() {
        withActorCheckBox = new CheckBox(
                "withActorsOnly",
                new PropertyModel<Boolean>( this, "onlyWithActors" )
        );
        withActorCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addWithoutActorCheckBox();
                addRangesPanel();
                addParticipationsTable();
                target.add( withoutActorCheckBox );
                target.add( nameRangePanel );
                target.add( participationsTable );
            }
        } );
        withActorCheckBox.setOutputMarkupId( true );
        addOrReplace( withActorCheckBox );
    }

    private void addWithoutActorCheckBox() {
        withoutActorCheckBox = new CheckBox(
                "withoutActorsOnly",
                new PropertyModel<Boolean>( this, "onlyWithoutActors" )
        );
        withoutActorCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addWithActorCheckBox();
                nameRange = new NameRange();
                addRangesPanel();
                addParticipationsTable();
                target.add( withActorCheckBox );
                target.add( nameRangePanel );
                target.add( participationsTable );
            }
        } );
        withoutActorCheckBox.setOutputMarkupId( true );
        addOrReplace( withoutActorCheckBox );
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
                        return ( !onlyWithActors || wrapper.hasActor() )
                                && ( !onlyWithoutActors || !wrapper.hasActor() )
                                && isInNameRange( wrapper );
                    }
                }
        );
    }

    private List<ParticipationWrapper> getAllParticipationWrappers() {
        List<ParticipationWrapper> participationWrappers = new ArrayList<ParticipationWrapper>();
        participationWrappers = new ArrayList<ParticipationWrapper>();
        QueryService queryService = getQueryService();
        for ( ChannelsUser channelsUser : userDao.getUsers( getPlan().getUri() ) ) {
            List<PlanParticipation> participations = planParticipationService.getParticipations(
                    getPlan(), channelsUser.getUserInfo(), queryService );
            for ( PlanParticipation participation : participations ) {
                planParticipationService.refresh( participation );
                ParticipationWrapper wrapper = new ParticipationWrapper(
                        channelsUser.getUsername(),
                        participation );
                participationWrappers.add( wrapper );
            }
            if ( isLockedByUser( getPlan() ) && participations.isEmpty() ) {
                participationWrappers.add( new ParticipationWrapper( channelsUser.getUsername() ) );
                if ( addedParticipationWrapper != null ) {
                    participationWrappers.add( addedParticipationWrapper );
                }
            }
        }
        addedParticipationWrapper = null;
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

    public boolean isOnlyWithActors() {
        return onlyWithActors;
    }

    public void setOnlyWithActors( boolean val ) {
        if ( val ) onlyWithoutActors = false;
        onlyWithActors = val;
    }

    public boolean isOnlyWithoutActors() {
        return onlyWithoutActors;
    }

    public void setOnlyWithoutActors( boolean val ) {
        if ( val ) onlyWithActors = false;
        onlyWithoutActors = val;
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
        addParticipationActorChannels();
        addUserChannels();
    }

    private boolean isAssignedParticipationSelected() {
        return selectedParticipation != null
                && selectedParticipation.getActor() != null;
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
        Actor actor = ( pw == null ) ? null : pw.getActor();
        WebMarkupContainer asActorSpan = new WebMarkupContainer( "asActor" );
        asActorSpan.setOutputMarkupId( true );
        makeVisible( asActorSpan, actor != null );
        participationDiv.addOrReplace( asActorSpan );
        if ( actor != null ) {
            ModelObjectLink actorLink = new ModelObjectLink( "actorLink", new Model<Actor>( actor ), new Model<String>( actor.getName() ) );
            asActorSpan.add( actorLink );
        } else {
            asActorSpan.add( new Label( "actorLink", "" ) );
        }
    }

    private void addParticipationActorChannels() {
        ParticipationWrapper pw = getParticipation();
        String channelsString = pw != null && pw.getActor() != null
                ? pw.getActor().getChannelsString()
                : "None";
        Label label = new Label( "actorChannels", channelsString );
        label.setOutputMarkupId( true );
        participationDiv.addOrReplace( label );
    }

    private void addUserChannels() {
        ParticipationWrapper pw = getParticipation();
        String channelsString = pw != null
                ? Channel.toString( userContactInfoService.findChannels(
                pw.getParticipatingUserInfo(),
                getQueryService() ) )
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
                if ( action.equals( "entity named" ) ) {
                    resetParticipations();
                    addParticipationsTable();
                    target.add( participationsTable );
                    selectedParticipation = null;
                    addParticipation();
                    target.add( participationDiv );
                    update(
                            target,
                            new Change(
                                    Change.Type.Updated,
                                    getPlan() ) );
                } else if ( action.equals( "participation" ) ) {
                    ChannelsUser participatingUser = userDao.getUserNamed( wrapper.getUsername() );
                    if ( participatingUser != null ) {
                        selectedParticipation = null;
                        addedParticipationWrapper = new ParticipationWrapper(
                                participatingUser.getUsername(),
                                new PlanParticipation( getUsername(), getPlan(), participatingUser ) );
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
        private PlanParticipation participation;

        public ParticipationWrapper( String username ) {
            this.username = username;
        }

        public ParticipationWrapper( String username, PlanParticipation participation ) {
            this.username = username;
            this.participation = participation;
        }

        public String getUsername() {
            return username;
        }

        public PlanParticipation getParticipation() {
            return participation;
        }

        public void setParticipation( PlanParticipation participation ) {
            this.participation = participation;
        }

        public boolean hasActor() {
            return participation != null && participation.getActor( getQueryService() ) != null;
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

        public Actor getActor() {
            return participation == null ? null : participation.getActor( getQueryService() );
        }

        public void setActor( Actor actor ) {
            if ( participation != null ) {
                planParticipationService.removeParticipation( getUser().getUsername(), getPlan(), participation );
            }
            if ( actor != null ) {
                participation = planParticipationService.addParticipation(
                        getUser().getUsername(),
                        getPlan(),
                        userDao.getUserNamed( getUsername() ),
                        actor );
            } else {
                participation = null;
            }
            getPlanManager().clearCache();
        }

        public List<Actor> getDomain() {
            List<Actor> domain = new ArrayList<Actor>();
            List<Actor> actors = getQueryService().listActualEntities( Actor.class );
            for ( Actor actor : actors ) {
                if ( planParticipationService.canBeDesignated( getPlan(), actor ) )
                    domain.add( actor );
            }
            Collections.sort(
                    domain,
                    new Comparator<Actor>() {
                        @Override
                        public int compare( Actor a1, Actor a2 ) {
                            return a1.getName().compareTo( a2.getName() );
                        }
                    } );
            return domain;
        }

        public ChannelsUserInfo getParticipatingUserInfo() {
            return participation.getParticipant();
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
                    null,
                    EMPTY,
                    null,
                    "userNormalizedFullName"
            ) );
            if ( isLockedByUser( getPlan() ) ) {
                columns.add( makeEntityReferenceColumn(
                        "Is agent",
                        "actor",
                        "domain",
                        Actor.class,
                        true,
                        "Name an agent representing the user",
                        ParticipationsPanel.this
                ) );
            } else {
                columns.add( this.makeLinkColumn( "Is agent", "actor", "actor.normalizedName", EMPTY ) );
            }
            columns.add( makeActionLinkColumn(
                    "",
                    "contact",
                    "select",
                    "actor",
                    "more",
                    ParticipationsPanel.this
            ) );
            if ( isLockedByUser( getPlan() ) ) {
                columns.add( makeActionLinkColumn(
                        "",
                        "add",
                        "participation",
                        "actor",
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

