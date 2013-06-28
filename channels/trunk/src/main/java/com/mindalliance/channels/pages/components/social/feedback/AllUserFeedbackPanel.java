package com.mindalliance.channels.pages.components.social.feedback;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.services.messages.FeedbackService;
import com.mindalliance.channels.db.services.users.UserRecordService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/22/12
 * Time: 3:29 PM
 */
public class AllUserFeedbackPanel extends AbstractUpdatablePanel implements Filterable {

    /**
     * Simple date format.
     */
    private static String DATE_FORMAT_STRING =  "M/d/yyyy HH:mm";

    private static int MAX_TEXT_LENGTH = 40;

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_FEEDBACK_ROWS = 10;

    private static final String ANY = "Any";

    static private final String[] TOPIC_CHOICES = {
            ANY,
            Feedback.CHANNELS,
            Feedback.FEEDBACK,
            Feedback.GUIDELINES,
            Feedback.INFO_NEEDS,
            Feedback.ISSUES,
            Feedback.PARTICIPATING,
            Feedback.PLANNING,
            Feedback.SURVEYS,
            Feedback.REQUIREMENTS
    };

    @SpringBean
    FeedbackService feedbackService;

    @SpringBean
    UserRecordService userInfoService;

    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();

    private FeedbackTable feedbacksTable;
    private String selectedFeedbackId;
    private WebMarkupContainer selectedFeedbackContainer;
    boolean urgentOnly;
    boolean unresolvedOnly;
    String topic = "";
    boolean notRepliedToOnly;
    String containing = "";
    private boolean showProfile;
    private boolean personalOnly;
    private boolean canResolve;
    private Label resolvedLabel;

    public AllUserFeedbackPanel( String id, Model<Plan> planModel, boolean showProfile ) {
        this( id, planModel, showProfile, false, false );
    }

    public AllUserFeedbackPanel(
            String id,
            IModel<Plan> planModel,
            boolean showProfile,
            boolean personalOnly,
            boolean canResolve ) {
        super( id, planModel, null );
        this.showProfile = showProfile;
        this.personalOnly = personalOnly;
        this.canResolve = canResolve;
        init();
    }

    @Override
    // Use the domain community
    public CommunityService getCommunityService() {
        return getCommunityService( getDomainPlanCommunity() );
    }

    private void init() {
        addHeading();
        addFilters();
        addFeedbackTable();
        addSelectedFeedback();
    }

    private void addHeading() {
        add( new Label(
                "heading",
                personalOnly ? "Feedback I sent" : "Feedback from all users" ) );
    }

    public void select( Feedback feedback ) {
        setSelectedFeedbackId( feedback.isUnknown() ? null : feedback.getUid() );
    }


    private void addFilters() {
        add( new AjaxCheckBox( "urgent", new PropertyModel<Boolean>( this, "urgentOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        add( new AjaxCheckBox( "unresolved", new PropertyModel<Boolean>( this, "unresolvedOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        add( new AjaxCheckBox( "notRepliedTo", new PropertyModel<Boolean>( this, "notRepliedToOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        DropDownChoice<String> topicsChoice = new DropDownChoice<String>(
                "topics",
                new PropertyModel<String>( this, "topic" ),
                Arrays.asList( TOPIC_CHOICES )
        );
        topicsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        add( topicsChoice );
        TextField<String> containingField = new TextField<String>(
                "containing",
                new PropertyModel<String>( this, "containing" )
        );
        containingField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        add( containingField );
    }

    private void updateTableAndSelected( AjaxRequestTarget target ) {
        selectedFeedbackId = null;
        addSelectedFeedback();
        target.add( selectedFeedbackContainer );
        addFeedbackTable();
        target.add( feedbacksTable );
    }

    private void addFeedbackTable() {
        feedbacksTable = new FeedbackTable(
                "feedbackTable",
                new PropertyModel<List<FeedbackWrapper>>( this, "filteredFeedbacks" ) );
        feedbacksTable.setOutputMarkupId( true );
        addOrReplace( feedbacksTable );
    }

    public List<FeedbackWrapper> getFilteredFeedbacks() {
        List<FeedbackWrapper> wrappers = new ArrayList<FeedbackWrapper>();
        List<Feedback> feedbacks = feedbackService.selectInitialFeedbacks(
                getCommunityService(),
                urgentOnly,
                unresolvedOnly,
                notRepliedToOnly,
                getTopic().equals( ANY ) ? null : getTopic(),
                containing,
                personalOnly ? getUsername() : null
        );
        for ( Feedback f : feedbacks ) {
            if ( !isFilteredOut( f ) )
                wrappers.add( new FeedbackWrapper( f ) );
        }
        return wrappers;
    }

    private boolean isFilteredOut( Feedback feedback ) {
        ModelObject about = filters.get( "about" );
        return about != null && !about.equals( feedback.getAbout( getCommunityService() ) );
    }


    private void addFeedbackTitle() {
        Label feedbackTitle = new Label( "feedbackLabel", getFeedbackTitle() );
        feedbackTitle.setOutputMarkupId( true );
        addOrReplace( feedbackTitle );
    }

    private String getFeedbackTitle() {
        StringBuilder sb = new StringBuilder();
        Feedback feedback = getSelectedFeedback();
        if ( feedback != null ) {
            if ( feedback.isUrgent() ) sb.append( "Urgent" );
            if ( !feedback.isResolved() ) {
                if ( sb.length() != 0 ) sb.append( " " );
                sb.append( "unresolved" );
            }
            if ( sb.length() != 0 ) sb.append( " " );
            sb.append( feedback.getTypeLabel() );
            sb.append( " from " );
            ChannelsUser user = userInfoService.getUserWithIdentity( feedback.getUsername() );
            sb.append( user == null ? feedback.getUsername() : user.getFullName() );
            sb.append( " about " );
            sb.append( feedback.getTopic().toLowerCase() );
            String moRefString = feedback.getMoRef();
            if ( moRefString != null ) {
                ModelObject mo = ModelObjectRef.resolveFromString( moRefString, getCommunityService() );
                if ( mo != null ) {
                    sb.append( " (" );
                    sb.append( mo.getLabel() );
                    sb.append( ") " );
                }
            }
            sb.append( ", received on " );
            sb.append( new SimpleDateFormat(DATE_FORMAT_STRING).format( feedback.getCreated() ) );
            sb.append( "." );
        }
        return StringUtils.capitalize( sb.toString() );
    }

    private Feedback getSelectedFeedback() {
        return selectedFeedbackId != null
                ? feedbackService.load( selectedFeedbackId )
                : null;
    }

    private void addSelectedFeedback() {
        addFeedbackTitle();
        selectedFeedbackContainer = new WebMarkupContainer( "feedbackContainer" );
        selectedFeedbackContainer.setOutputMarkupId( true );
        Feedback feedback = getSelectedFeedback();
        makeVisible( selectedFeedbackContainer, feedback != null );
        addResolutionStatus();
        selectedFeedbackContainer.add( feedback == null
                ? new Label( "discussion", "" )
                : new FeedbackDiscussionPanel(
                "discussion",
                new Model<Feedback>( feedback ),
                showProfile,
                canResolve,
                personalOnly
        ) );
        addOrReplace( selectedFeedbackContainer );
    }

    private void addResolutionStatus() {
        Feedback feedback = getSelectedFeedback();
        resolvedLabel = new Label(
                "resolved",
                feedback == null
                        ? ""
                        : feedback.isResolved()
                        ? "Resolved"
                        : "Not resolved" );
        resolvedLabel.setOutputMarkupId( true );
        resolvedLabel.add( new AttributeModifier(
                "class",
                feedback == null || !feedback.isResolved()
                        ? "not-resolved"
                        : "resolved" ) );
        selectedFeedbackContainer.addOrReplace( resolvedLabel );
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addFeedbackTable();
        target.add( feedbacksTable );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    public String getContaining() {
        return containing;
    }

    public void setContaining( String containing ) {
        this.containing = containing;
    }

    public boolean isNotRepliedToOnly() {
        return notRepliedToOnly;
    }

    public void setNotRepliedToOnly( boolean notRepliedToOnly ) {
        this.notRepliedToOnly = notRepliedToOnly;
    }

    public String getTopic() {
        return topic == null || topic.isEmpty() ? ANY : topic;
    }

    public void setTopic( String topic ) {
        if ( topic == null || topic.isEmpty() || topic.equals( ANY ) )
            this.topic = null;
        else
            this.topic = topic;
    }

    public boolean isUnresolvedOnly() {
        return unresolvedOnly;
    }

    public void setUnresolvedOnly( boolean unresolvedOnly ) {
        this.unresolvedOnly = unresolvedOnly;
    }

    public boolean isUrgentOnly() {
        return urgentOnly;
    }

    public void setUrgentOnly( boolean urgentOnly ) {
        this.urgentOnly = urgentOnly;
    }

    public String getSelectedFeedbackId() {
        return selectedFeedbackId;
    }

    public void setSelectedFeedbackId( String selectedFeedbackId ) {
        this.selectedFeedbackId = selectedFeedbackId;
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        addSelectedFeedback();
        target.add( selectedFeedbackContainer );
        addFeedbackTable();
        target.add( feedbacksTable );
    }

    public void updateContent( AjaxRequestTarget target ) {
        addFeedbackTable();
        target.add( feedbacksTable );
    }



    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( FeedbackWrapper.class ) && change.isExpanded() ) {
            FeedbackWrapper fw = (FeedbackWrapper) change.getSubject( getCommunityService() );
            if ( selectedFeedbackId != null && fw.getFeedback().getUid().equals( selectedFeedbackId ) ) {
                setSelectedFeedbackId( null );
            } else {
                setSelectedFeedbackId( fw.getFeedback().getUid() );
            }
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( FeedbackWrapper.class ) && change.isExpanded() ) {
            addSelectedFeedback();
            target.add( selectedFeedbackContainer );
        } else if ( change.isForInstanceOf( Feedback.class ) && change.isUpdated() ) {
            addFeedbackTable();
            target.add( feedbacksTable );
           addResolutionStatus();
           target.add( resolvedLabel );
        } else {
            super.updateWith( target, change, updated );
        }
    }

    public class FeedbackWrapper implements Identifiable {

        private Feedback feedback;

        public FeedbackWrapper( Feedback feedback ) {
            this.feedback = feedback;
        }

        public Feedback getFeedback() {
            return feedback;
        }

        @Override
        public long getId() {
            return feedback.getId();
        }

        @Override
        public String getDescription() {
            return feedback.getDescription();
        }

        @Override
        public String getTypeName() {
            return feedback.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return feedback.isModifiableInProduction();
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getName() {
            return feedback.getName();
        }

        public Date getCreated() {
            return feedback.getCreated();
        }

        public String getTopic() {
            return feedback.getTopic();
        }

        public String getMoRef() {
            return feedback.getMoRef();
        }

        public ModelObject getAbout() {
            String moRefString = feedback.getMoRef();
            if ( moRefString == null ) {
                return null;
            } else {
                ModelObjectRef modelObjectRef = ModelObjectRef.fromString( moRefString );
                return (ModelObject) modelObjectRef.resolve( getCommunityService() );
            }
        }

        public String getMoRefLabel() {
            return feedback.getMoLabel();
        }

        public String getText() {
            return feedback.getText();
        }

        public String getTypeLabel() {
            return feedback.getTypeLabel();
        }

        public String getResolvedLabel() {
            return feedback.isResolved() ? "yes" : "no";
        }

        public String getUrgentLabel() {
            return feedback.isUrgent() ? "yes" : "no";
        }

        public String getFormattedCreated() {
            return new SimpleDateFormat(DATE_FORMAT_STRING).format( feedback.getCreated() );
        }

        public String getFormattedLastReplied() {
            return feedback.getLastReplied() != null
                    ? new SimpleDateFormat(DATE_FORMAT_STRING).format( feedback.getLastReplied() )
                    : null;
        }

        public String getAbbreviatedText() {
            return StringUtils.abbreviate( feedback.getText(), MAX_TEXT_LENGTH );
        }

        public String getFullName() {
            return feedback.getUserFullName( userInfoService );
        }

        public String getExpandLabel() {
            String selected = getSelectedFeedbackId();
            return selected != null && selected.equals( feedback.getUid() ) ? "Close" : "Open";
        }



    }


    /**
     * Feedbacks table.
     */
    public class FeedbackTable extends AbstractTablePanel<FeedbackWrapper> {

        private IModel<List<FeedbackWrapper>> feedbacksModel;

        public FeedbackTable( String id, IModel<List<FeedbackWrapper>> feedbacksModel ) {
            super( id );
            this.feedbacksModel = feedbacksModel;
            initialize();
        }

        public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
            if ( change.isExpanded() ) {
                target.add( this );
            }
            super.updateWith( target, change, updatables );
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Urgent", "urgentLabel", EMPTY ) );
            columns.add( makeColumn( "Type", "typeLabel", EMPTY ) );
            columns.add( makeUserColumn( "From", "fullName", EMPTY ) );
            columns.add( makeColumn( "Topic", "topic", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "About", "about", "moRefLabel", EMPTY, AllUserFeedbackPanel.this, false ) );
            columns.add( makeColumn( "Content", "abbreviatedText", null, EMPTY, "text" ) );
            columns.add( makeColumn( "Received", "formattedCreated", null, EMPTY, null, "created" ) );
            columns.add( makeColumn( "Last replied", "formattedLastReplied", EMPTY ) );
            columns.add( makeColumn( "Resolved", "resolvedLabel", EMPTY ) );
            columns.add( makeExpandLinkColumn( "", "", "@expandLabel" ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "feedbacks",
                    columns,
                    new SortableBeanProvider<FeedbackWrapper>( feedbacksModel.getObject(),
                            "created" ),
                    MAX_FEEDBACK_ROWS ) );
        }


    }

}
