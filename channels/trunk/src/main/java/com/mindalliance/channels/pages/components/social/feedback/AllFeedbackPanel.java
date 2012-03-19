package com.mindalliance.channels.pages.components.social.feedback;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.ModelObjectRef;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.services.FeedbackService;
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
 * Date: 2/22/12
 * Time: 12:01 PM
 */
public class AllFeedbackPanel extends FloatingCommandablePanel implements Filterable {

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    /**
     * Simple date format.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat( "M/d/yyyy HH:mm" );

    private static int MAX_TEXT_LENGTH = 40;

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_FEEDBACK_ROWS = 10;

    private static final String ANY = "Any";

    static private final String[] TOPIC_CHOICES = {
            ANY,
            Feedback.GUIDELINES,
            Feedback.INFO_NEEDS,
            Feedback.ISSUES,
            Feedback.PARTICIPATING,
            Feedback.PLANNING,
            Feedback.RFI
    };

    @SpringBean
    FeedbackService feedbackService;

    @SpringBean
    ChannelsUserDao userDao;

    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();

    private FeedbackTable feedbacksTable;
    private Feedback selectedFeedback;
    private WebMarkupContainer selectedFeedbackContainer;
    boolean urgentOnly;
    boolean unresolvedOnly;
    String topic = "";
    boolean notRepliedToOnly;
    String containing = "";
    private boolean showProfile;

    public AllFeedbackPanel( String id, Model<Plan> planModel, boolean showProfile ) {
        super( id, planModel, null );
        this.showProfile = showProfile;
        init();
    }

    private void init() {
        addFilters();
        addFeedbackTable();
        addSelectedFeedback();
    }

    public void select( Feedback feedback ) {
        setSelectedFeedback( feedback.isUnknown() ? null : feedback );
    }


    private void addFilters() {
        getContentContainer().add( new AjaxCheckBox( "urgent", new PropertyModel<Boolean>( this, "urgentOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        getContentContainer().add( new AjaxCheckBox( "unresolved", new PropertyModel<Boolean>( this, "unresolvedOnly" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateTableAndSelected( target );
            }
        } );
        getContentContainer().add( new AjaxCheckBox( "notRepliedTo", new PropertyModel<Boolean>( this, "notRepliedToOnly" ) ) {
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
        getContentContainer().add( topicsChoice );
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
        getContentContainer().add( containingField );
    }

    private void updateTableAndSelected( AjaxRequestTarget target ) {
        selectedFeedback = null;
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
        getContentContainer().addOrReplace( feedbacksTable );
    }

    public List<FeedbackWrapper> getFilteredFeedbacks() {
        List<FeedbackWrapper> wrappers = new ArrayList<FeedbackWrapper>();
        List<Feedback> feedbacks = feedbackService.selectInitialFeedbacks(
                urgentOnly,
                unresolvedOnly,
                notRepliedToOnly,
                getTopic().equals( ANY ) ? null : getTopic(),
                containing
        );
        for ( Feedback f : feedbacks ) {
            if ( !isFilteredOut( f ) )
                wrappers.add( new FeedbackWrapper( f ) );
        }
        return wrappers;
    }

    private boolean isFilteredOut( Feedback feedback ) {
        ModelObject about = filters.get( "about" );
        return about != null && !about.equals( feedback.getAbout( getQueryService() ) );
    }


    private void addFeedbackTitle() {
        Label feedbackTitle = new Label( "feedbackLabel", getFeedbackTitle() );
        feedbackTitle.setOutputMarkupId( true );
        getContentContainer().addOrReplace( feedbackTitle );
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
            ChannelsUser user = userDao.getUserNamed( feedback.getUsername() );
            sb.append( user == null ? feedback.getUsername() : user.getFullName() );
            sb.append( " about " );
            sb.append( feedback.getTopic().toLowerCase() );
            String moRefString = feedback.getMoRef();
            if ( moRefString != null ) {
                ModelObject mo = ModelObjectRef.resolveFromString( moRefString, getQueryService() );
                if ( mo != null ) {
                    sb.append( " (" );
                    sb.append( mo.getLabel() );
                    sb.append( ") " );
                }
            }
            sb.append( ", received on " );
            sb.append( dateFormat.format( feedback.getCreated() ) );
            sb.append( "." );
        }
        return StringUtils.capitalize( sb.toString() );
    }

    private void addSelectedFeedback() {
        addFeedbackTitle();
        selectedFeedbackContainer = new WebMarkupContainer( "feedbackContainer" );
        selectedFeedbackContainer.setOutputMarkupId( true );
        Feedback feedback = getSelectedFeedback();
        makeVisible( selectedFeedbackContainer, feedback != null );
        Label resolvedLabel = new Label(
                "resolved",
                feedback == null
                        ? ""
                        : feedback.isResolved()
                        ? "Resolved"
                        : "Not resolved" );
        resolvedLabel.add( new AttributeModifier(
                "class",
                feedback == null || !feedback.isResolved()
                        ? "not-resolved"
                        : "resolved" ) );
        selectedFeedbackContainer.add( resolvedLabel );
        selectedFeedbackContainer.add( feedback == null
                ? new Label( "discussion", "" )
                : new FeedbackDiscussionPanel(
                "discussion",
                new Model<Feedback>( feedback ),
                showProfile ) );
        getContentContainer().addOrReplace( selectedFeedbackContainer );
    }

    @Override
    protected String getTitle() {
        return "Feedback";
    }

    @Override
    protected int getPadTop() {
        return PAD_TOP;
    }

    @Override
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    @Override
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    @Override
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    @Override
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    @Override
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Feedback.UNKNOWN );
        update( target, change );
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

    public Feedback getSelectedFeedback() {
        if ( selectedFeedback != null ) {
            feedbackService.refresh( selectedFeedback );
        }
        return selectedFeedback;
    }

    public void setSelectedFeedback( Feedback selectedFeedback ) {
        this.selectedFeedback = selectedFeedback;
        feedbackService.refresh( selectedFeedback );
    }

    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        addSelectedFeedback();
        target.add( selectedFeedbackContainer );
        addFeedbackTable();
        target.add( feedbacksTable );
    }


    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( FeedbackWrapper.class ) && change.isExpanded() ) {
            FeedbackWrapper fw = (FeedbackWrapper) change.getSubject( getQueryService() );
            setSelectedFeedback( fw.getFeedback() );
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( FeedbackWrapper.class ) && change.isExpanded() ) {
            addSelectedFeedback();
            target.add( selectedFeedbackContainer );
        } else
            super.updateWith( target, change, updated );
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
                return (ModelObject) modelObjectRef.resolve( getQueryService() );
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
            return dateFormat.format( feedback.getCreated() );
        }

        public String getFormattedLastReplied() {
            return feedback.getLastReplied() != null
                    ? dateFormat.format( feedback.getLastReplied() )
                    : null;
        }

        public String getAbbreviatedText() {
            return StringUtils.abbreviate( feedback.getText(), MAX_TEXT_LENGTH );
        }

        public String getFullName() {
            return feedback.getUserFullName( userDao );
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

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Urgent", "urgentLabel", EMPTY ) );
            columns.add( makeColumn( "Type", "typeLabel", EMPTY ) );
            columns.add( makeUserColumn( "From", "fullName", EMPTY ) );
            columns.add( makeColumn( "Topic", "topic", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "About", "about", "moRefLabel", EMPTY, AllFeedbackPanel.this, true ) );
            columns.add( makeColumn( "Content", "abbreviatedText", null, EMPTY, "text" ) );
            columns.add( makeColumn( "Received", "formattedCreated", null, EMPTY, null, "created" ) );
            columns.add( makeColumn( "Last replied", "formattedLastReplied", EMPTY ) );
            columns.add( makeColumn( "Resolved", "resolvedLabel", EMPTY ) );
            columns.add( makeExpandLinkColumn( "", "", "more..." ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "feedbacks",
                    columns,
                    new SortableBeanProvider<FeedbackWrapper>( feedbacksModel.getObject(),
                            "created" ),
                    MAX_FEEDBACK_ROWS ) );
        }


    }
}
