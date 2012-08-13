package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIForwardService;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panels for managing the RFIs in a survey.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/12
 * Time: 2:36 PM
 */
public class RFIsPanel extends AbstractUpdatablePanel implements Filterable {

    private static final String ALL = "every one";
    private static final String PARTICIPANTS_ONLY = "participants only";
    private static final String NON_PARTICIPANTS_ONLY = "non-participants only";
    private static final int MAX_ROWS = 10;
    private String deadline = null;

    @SpringBean
    private RFISurveyService rfiSurveyService;

    @SpringBean
    private RFIService rfiService;

    @SpringBean
    private SurveysDAO surveysDAO;

    @SpringBean
    private RFIForwardService rfiForwardService;

    @SpringBean
    private ChannelsUserDao userDao;

    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();

    private List<String> allUsernames;
    private List<String> allParticipantUsernames;
    private List<SurveyParticipation> surveyParticipations;

    private boolean hasDeadline = false;
    private String recipientDomain = ALL;
    private SurveyParticipationTable surveyParticipationTable;
    private WebMarkupContainer actionsContainer;
    private WebMarkupContainer deadlineContainer;

    public RFIsPanel( String id, IModel<RFISurvey> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addRecipientsChoice();
        addSurveyParticipationTable();
        addActions();
    }

    private void reset() {
        allUsernames = null;
        allParticipantUsernames = null;
        surveyParticipations = null;
    }

    private void addSurveyParticipationTable() {
        surveyParticipations = null;
        surveyParticipationTable = new SurveyParticipationTable(
                "participation",
                new PropertyModel<List<SurveyParticipation>>( this, "surveyParticipations" ) );
        surveyParticipationTable.setOutputMarkupId( true );
        addOrReplace( surveyParticipationTable );
    }

    @SuppressWarnings( "unchecked" )
    public List<SurveyParticipation> getSurveyParticipations() {
        if ( surveyParticipations == null ) {
            surveyParticipations = new ArrayList<SurveyParticipation>();
            RFISurvey rfiSurvey = getRFISurvey();
            List<String> usernames = isAllUsernames()
                    ? getAllUsernames()
                    : isParticipating()
                    ? getAllParticipantUsernames()
                    : (List<String>) CollectionUtils.subtract( getAllUsernames(), getAllParticipantUsernames() );
            for ( String username : usernames ) {
                RFI rfi = null;
                if ( getAllParticipantUsernames().contains( username ) )
                    rfi = rfiService.find( getPlan(), rfiSurvey, username );
                SurveyParticipation surveyParticipation = new SurveyParticipation(
                        userDao.getUserNamed( username ).getUserInfo(),
                        rfi,
                        rfiSurvey );
                if ( !isFilteredOut( surveyParticipation ) ) {
                    surveyParticipations.add( surveyParticipation );
                }
            }
        }
        return surveyParticipations;
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addSurveyParticipationTable();
        target.add( surveyParticipationTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    private boolean isFilteredOut( SurveyParticipation surveyParticipation ) {
        for ( String property : filters.keySet() ) {
            if ( !ModelObject.areEqualOrNull(
                    filters.get( property ),
                    (ModelObject) ChannelsUtils.getProperty( surveyParticipation, property, null ) ) ) {
                return true;
            }
        }
        return false;
    }


    private boolean isParticipating() {
        return getRecipientDomain().equals( PARTICIPANTS_ONLY );
    }

    private boolean isNotParticipating() {
        return getRecipientDomain().equals( NON_PARTICIPANTS_ONLY );
    }

    private boolean isAllUsernames() {
        return getRecipientDomain().equals( ALL );
    }

    private void addRecipientsChoice() {
        DropDownChoice<String> recipientChoice = new DropDownChoice<String>(
                "recipients",
                new PropertyModel<String>( this, "recipientDomain" ),
                getRecipientDomains()
        );
        recipientChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                reset();
                addSurveyParticipationTable();
                target.add( surveyParticipationTable );
                addActions();
                target.add( actionsContainer );
            }
        } );
        add( recipientChoice );
    }

    private List<String> getRecipientDomains() {
        List<String> choices = new ArrayList<String>();
        choices.add( ALL );
        choices.add( PARTICIPANTS_ONLY );
        choices.add( NON_PARTICIPANTS_ONLY );
        return choices;
    }

    public String getRecipientDomain() {
        return recipientDomain == null ? ALL : recipientDomain;
    }

    public void setRecipientDomaine( String val ) {
        recipientDomain = val.equals( ALL ) ? null : val;
    }

    private List<String> getAllUsernames() {
        if ( allUsernames == null ) {
            allUsernames = userDao.getUsernames();
        }
        return allUsernames;
    }

    private List<String> getAllParticipantUsernames() {
        if ( allParticipantUsernames == null ) {
            allParticipantUsernames = rfiService.findParticipants( getPlan(), getRFISurvey() );
        }
        return allParticipantUsernames;
    }

    private void addActions() {
        actionsContainer = new WebMarkupContainer( "actions" );
        actionsContainer.setOutputMarkupId( true );
        addOrReplace( actionsContainer );
        addActionLabels();
        addDeadline();
        addActionButtons();
    }

    private void addActionLabels() {
        int selectedCount = getSelectedSurveyParticipations().size();
        actionsContainer.add( new Label( "selectionCount", Integer.toString( selectedCount ) ) );
        actionsContainer.add( new Label( "userOrUsers", selectedCount > 1 ? "users" : "user" ) );
        actionsContainer.add( new Label( "userCount", Integer.toString( getUserCount() ) ) );
    }

    private int getUserCount() {
        return userDao.getUsernames( getPlan().getUri() ).size();
    }

    @SuppressWarnings( "unchecked" )
    private List<SurveyParticipation> getSelectedSurveyParticipations() {
        return (List<SurveyParticipation>) CollectionUtils.select(
                getSurveyParticipations(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (SurveyParticipation) object ).isSelected();
                    }
                }
        );
    }

    @SuppressWarnings( "unchecked" )
    private void addActionButtons() {
        // send or update RFIs
        Date deadlineDate = getDeadlineDate();
        ConfirmedAjaxFallbackLink<String> createRFIsLink = new ConfirmedAjaxFallbackLink<String>(
                "sendRFI",
                "Send or update survey participation survey request to "
                        + getSelectedSurveyParticipations().size()
                        + ( getSelectedSurveyParticipations().size() > 1 ? " persons" : " person" )
                        + ( deadlineDate == null
                        ? " with no deadline "
                        : ( " with deadline on " + getDateFormat().format( deadlineDate ) ) )
                        + "?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                for ( SurveyParticipation participation : getSelectedSurveyParticipations() ) {
                    rfiService.makeOrUpdateRFI(
                            getPlan(),
                            getUsername(),
                            participation.getRfiSurvey(),
                            participation.getUserInfo(),
                            participation.getOrganization(),
                            participation.getTitle(),
                            participation.getRole(),
                            getDeadlineDate()
                    );
                }
                surveyParticipations = null;
                update( target, new Change( Change.Type.Updated, getRFISurvey(), "updated" ) );
            }
        };
        createRFIsLink.setEnabled( getSelectedSurveyParticipations().size() > 0 );
        actionsContainer.add( createRFIsLink );
        // nag
        final List<SurveyParticipation> naggableParticipations = (List<SurveyParticipation>) CollectionUtils.select(
                getSelectedSurveyParticipations(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (SurveyParticipation) object ).isNaggable();
                    }
                }
        );
        ConfirmedAjaxFallbackLink<String> nagLink = new ConfirmedAjaxFallbackLink<String>(
                "nag",
                "Nag "
                        + naggableParticipations.size()
                        + (naggableParticipations.size() > 1 ? " persons" : " person" )
                        + "?" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                for ( SurveyParticipation participation : naggableParticipations ) {
                    rfiService.nag(
                            getPlan(),
                            getUsername(),
                            participation.getRfiSurvey(),
                            participation.getUserInfo()
                    );
                }
                surveyParticipations = null;
                update( target, new Change( Change.Type.Updated, getRFISurvey(), "updated" ) );
            }
        };
        nagLink.setEnabled( naggableParticipations.size() > 0 );
        actionsContainer.add( nagLink );
    }

    private void addDeadline() {
        deadlineContainer = new WebMarkupContainer( "deadlineContainer" );
        deadlineContainer.setOutputMarkupId( true );
        makeVisible( deadlineContainer, isHasDeadline() );
        actionsContainer.add( deadlineContainer );
        AjaxCheckBox deadlineCheckBox = new AjaxCheckBox(
                "hasDeadline",
                new PropertyModel<Boolean>( this, "hasDeadline" )
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( deadlineContainer, isHasDeadline() );
                target.add( deadlineContainer );
                addActions();
                target.add( actionsContainer );
            }
        };
        deadlineCheckBox.setOutputMarkupId( true );
        deadlineCheckBox.setEnabled( getSelectedSurveyParticipations().size() > 0 );
        actionsContainer.add( deadlineCheckBox );
        addDeadlineField();
    }

    private void addDeadlineField() {
        TextField<String> deadlineField = new TextField<String>(
                "deadline",
                new PropertyModel<String>( this, "deadline" ) );
        deadlineField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                target.add( deadlineContainer );
                addActions();
                target.add( actionsContainer );
            }
        } );
        deadlineContainer.add( deadlineField );
    }

    public boolean isHasDeadline() {
        return hasDeadline;
    }

    public void setHasDeadline( boolean hasDeadline ) {
        this.hasDeadline = hasDeadline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline( String val ) {
        if ( val == null || val.trim().isEmpty() )
            deadline = null;
        else
            try {
                deadline = Integer.toString( Integer.parseInt( val ) );
            } catch ( NumberFormatException e ) {
                deadline = null;
            }
    }

    private Date getDeadlineDate() {
        String d = getDeadline();
        if ( d == null ) {
            return null;
        } else {
            int days = Integer.parseInt( d );
            Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.DAY_OF_MONTH, days );
            return calendar.getTime();
        }
    }

    private RFISurvey getRFISurvey() {
        return (RFISurvey) getModel().getObject();
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof SurveyParticipation ) {
            if ( action.equals( "selected" ) ) {
                addActions();
                target.add( actionsContainer );
            }
        }
    }

    public void changed( Change change ) {
        reset();
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        addSurveyParticipationTable();
        target.add( surveyParticipationTable );
        super.updateWith( target, change, updated );
    }

    public class SurveyParticipation implements Identifiable {

        private ChannelsUserInfo userInfo;
        private RFISurvey rfiSurvey;
        private RFI rfi; // can be null if no participation
        private boolean selected = false;

        public SurveyParticipation( ChannelsUserInfo userInfo, RFI rfi, RFISurvey rfiSurvey ) {
            this.userInfo = userInfo;
            this.rfi = rfi;
            this.rfiSurvey = rfiSurvey;
        }

        public RFI getRfi() {
            return rfi;
        }

        public RFISurvey getRfiSurvey() {
            return rfiSurvey;
        }

        public ChannelsUserInfo getUserInfo() {
            return userInfo;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
        }

        public String getFullUserName() {
            return userInfo.getFullName();
        }

        public Organization getOrganization() {
            return rfi == null
                    ? null
                    : find( Organization.class, rfi.getOrganizationId() );
        }

        public Role getRole() {
            return rfi == null
                    ? null
                    : find( Role.class, rfi.getRoleId() );
        }

        public String getTitle() {
            return rfi == null
                    ? null
                    : rfi.getTitle();
        }

        public String getDeadline() {
            String result = null;
            if ( rfi != null ) {
                Date deadline = rfi.getDeadline();
                if ( deadline != null ) {
                    result = getDateFormat().format( deadline );
                }
            }
            return result;
        }

        public String getNagged() {
            String result = null;
            if ( rfi != null ) {
                Date nagged = rfi.getNagged();
                if ( nagged != null ) {
                    result = getDateFormat().format( nagged );
                }
            }
            return result;
        }

        public String getStatus() {
            if ( rfi == null ) return null;
            return rfi.isDeclined()
                    ? "Declined"
                    : surveysDAO.isCompleted( rfi )
                    ? "Completed"
                    : "Incomplete";
        }

        @SuppressWarnings( "unchecked" )
        public String getForwardedBy() {
            List<String> fullNames = (List<String>) CollectionUtils.collect(
                    rfiForwardService.findForwarderUsernames( rfi ),
                    new Transformer() {
                        @Override
                        public Object transform( Object input ) {
                            String username = (String) input;
                            ChannelsUser user = userDao.getUserNamed( username );
                            return user == null ? username : user.getFullName();
                        }
                    } );
            return fullNames.isEmpty()
                    ? null
                    : StringUtils.join( fullNames, ", " );
        }

        public String getInvitedOn() {
            return rfi == null ? null : getDateFormat().format( rfi.getCreated() );
        }

        public Date getCreated() {
            return rfi == null ? null : rfi.getCreated();
        }

        private <T extends ModelObject> T find( Class<T> clazz, Long id ) {
            if ( id == null )
                return null;
            else
                try {
                    return getQueryService().find( clazz, id );
                } catch ( NotFoundException e ) {
                    return null;
                }
        }

        // Identifiable

        @Override
        public long getId() {
            return userInfo.getId();
        }

        @Override
        public String getDescription() {
            return userInfo.getDescription();
        }

        @Override
        public String getTypeName() {
            return userInfo.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return userInfo.isModifiableInProduction();
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getName() {
            return userInfo.getName();
        }


        public boolean isNaggable() {
            return rfi != null
                    && surveysDAO.isOverdue( rfi, getQueryService(), getAnalyst() );

        }
    }

    public class SurveyParticipationTable extends AbstractTablePanel<SurveyParticipation> {

        private IModel<List<SurveyParticipation>> surveyParticipationsModel;

        public SurveyParticipationTable( String s, IModel<List<SurveyParticipation>> surveyParticipationsModel ) {
            super( s );
            this.surveyParticipationsModel = surveyParticipationsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeCheckBoxColumn(
                    "",
                    "selected",
                    true,
                    RFIsPanel.this ) );
            columns.add( makeColumn( "Name", "fullUserName", EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "organization",
                    "organization.name",
                    EMPTY,
                    RFIsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "role",
                    "role.name",
                    EMPTY,
                    RFIsPanel.this ) );
            columns.add( makeColumn( "Title", "title", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeColumn( "Invited on", "invitedOn", EMPTY ) );
            columns.add( makeColumn( "Forwarded by", "forwardedBy", EMPTY ) );
            columns.add( makeColumn( "Deadline", "deadline", EMPTY ) );
            columns.add( makeColumn( "Nagged", "nagged", EMPTY ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "rfiSurveys",
                    columns,
                    new SortableBeanProvider<SurveyParticipation>( surveyParticipationsModel.getObject(),
                            "created" ),
                    MAX_ROWS ) );

        }

    }

}
