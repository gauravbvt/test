package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "M/d/yyyy HH:mm" );

    @SpringBean
    private RFISurveyService rfiSurveyService;

    @SpringBean
    private ChannelsUserDao userDao;

    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();

    private List<String> allUsernames;
    private List<String> allParticipantUsernames;

    private String recipientDomain = ALL;
    private SurveyParticipationTable surveyParticipationTable;

    public RFIsPanel( String id, IModel<RFISurvey> model ) {
        super( id, model );
        init();
    }

    private void init() {
        addRecipientsChoice();
        addSurveyParticipationTable();
        addActionLabels();
        addActionButtons();
    }

    private void addSurveyParticipationTable() {
        surveyParticipationTable = new SurveyParticipationTable(
                "participation",
                new PropertyModel<List<SurveyParticipation>>( this, "surveyParticipations" ) );
        surveyParticipationTable.setOutputMarkupId( true );
        addOrReplace( surveyParticipationTable );
    }

    @SuppressWarnings( "unchecked" )
    public List<SurveyParticipation> getSurveyParticipations() {
        List<SurveyParticipation> surveyParticipations = new ArrayList<SurveyParticipation>();
        RFISurvey rfiSurvey = getRFISurvey();
        List<String> usernames = isAllUsernames()
                ? getAllUsernames()
                : isParticipating()
                ? getAllParticipantUsernames()
                : (List<String>) CollectionUtils.subtract( getAllUsernames(), getAllParticipantUsernames() );
        for ( String username : usernames ) {
            RFI rfi = null;
            if ( getAllParticipantUsernames().contains( username ) )
                rfi = rfiSurveyService.findRFI( username, rfiSurvey );
            SurveyParticipation surveyParticipation = new SurveyParticipation(
                    userDao.getUserNamed( username ).getUserInfo(),
                    rfi,
                    rfiSurvey );
            if ( !isFilteredOut( surveyParticipation ) ) {
                surveyParticipations.add( surveyParticipation );
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

    private void addActionLabels() {
        // todo
    }

    private void addActionButtons() {
        // todo
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
                //Todo
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

    private String getRecipientDomain() {
        return recipientDomain == null ? ALL : recipientDomain;
    }

    public void update( AjaxRequestTarget target, Object object, String action ) {
        if ( object instanceof SurveyParticipation ) {
            if ( action.equals( "selected" ) ) {
                addSurveyParticipationTable();
                target.add( surveyParticipationTable );
            }
        }
    }

    private List<String> getAllUsernames() {
        if ( allUsernames == null ) {
            allUsernames = userDao.getUsernames();
        }
        return allUsernames;
    }

    private List<String> getAllParticipantUsernames() {
        if ( allParticipantUsernames == null ) {
            allParticipantUsernames = rfiSurveyService.findParticipants( getRFISurvey() );
        }
        return allParticipantUsernames;
    }

    private RFISurvey getRFISurvey() {
        return (RFISurvey) getModel().getObject();
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
        public String getName() {
            return userInfo.getName();
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
                    result = dateFormat.format( deadline );
                }
            }
            return result;
        }

        public String getNagged() {
            String result = null;
            if ( rfi != null ) {
                Date nagged = rfi.getNagged();
                if ( nagged != null ) {
                    result = dateFormat.format( nagged );
                }
            }
            return result;
        }



        private <T extends ModelObject> T find( Class<T> clazz, Long id ) {
            try {
                return getQueryService().find( clazz, id );
            } catch ( NotFoundException e ) {
                return null;
            }
        }

        // todo
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
                    "selected",
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
            columns.add( makeColumn( "Deadline", "deadline", EMPTY ) );
            columns.add( makeColumn( "Nagged", "nagged", EMPTY ) );
           // todo
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "rfiSurveys",
                    columns,
                    new SortableBeanProvider<SurveyParticipation>( surveyParticipationsModel.getObject(),
                            "created" ),
                    MAX_ROWS ) );

        }
    }

}
