package com.mindalliance.channels.pages.components.help;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.surveys.RFISurvey;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.guide.ChangeQualifier;
import com.mindalliance.channels.guide.Guide;
import com.mindalliance.channels.guide.GuideReader;
import com.mindalliance.channels.guide.ScriptChange;
import com.mindalliance.channels.guide.Section;
import com.mindalliance.channels.guide.Topic;
import com.mindalliance.channels.guide.TopicDocument;
import com.mindalliance.channels.guide.TopicItem;
import com.mindalliance.channels.guide.TopicItemScript;
import com.mindalliance.channels.guide.TopicRef;
import com.mindalliance.channels.guide.UserRole;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.guide.HelpScriptable;
import com.mindalliance.channels.pages.components.guide.IGuidePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/13
 * Time: 2:26 PM
 */
public class HelpPanel extends AbstractUpdatablePanel implements IGuidePanel, HelpScriptable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( HelpPanel.class );


    @SpringBean
    private GuideReader guideReader;

    @SpringBean
    private ImagingService imagingService;


    private Guide guide;

    private String guideName;
    private String defaultUserRoleId;
    private String userRoleId;
    private String sectionId;
    private String topicId;
    private Stack<Section> sectionStack = new Stack<Section>();
    private Stack<Topic> topicStack = new Stack<Topic>();
    private AjaxLink<String> backLink;
    private WebMarkupContainer content;
    private AjaxLink<String> glossaryLink;
    private WebMarkupContainer lightBox;
    private WebMarkupContainer docImage;
    private Label docCaption;

    public HelpPanel( String id, String guideName, String defaultUserRoleId, Map<String, Object> context ) {
        super( id );
        this.guideName = guideName;
        this.defaultUserRoleId = defaultUserRoleId;
        guide = guideReader.getGuide( guideName );
        guide.setContext( context );
        init();
    }

    private void init() {
        addLightbox( "images/delete.png", "" );
        addTitle();
        addGlossaryLink();
        addBack();
        content = new WebMarkupContainer( "content" );
        content.setOutputMarkupId( true );
        add( content );
        initContent();
    }

    private void addLightbox( String imageSrc, String caption ) {
        lightBox = new WebMarkupContainer( "lightbox" );
        lightBox.setOutputMarkupId( true );
        lightBox.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                makeVisible( lightBox, false );
                target.add( lightBox );
            }
        } );
        docImage = new WebMarkupContainer( "image" );
        docImage.add( new AttributeModifier( "src", imageSrc ) );
        docImage.setOutputMarkupId( true );
        lightBox.add( docImage );
        docCaption = new Label( "caption", caption );
        docCaption.setOutputMarkupId( true );
        lightBox.add( docCaption );
        addTipTitle( lightBox, "Click to close" );
        makeVisible( lightBox, false );
        addOrReplace( lightBox );
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    private void initContent() {
        Topic topic = getTopic();
        addTopicName( topic );
        addTopicItems( topic );
        addDefinitions( topic );
        addDoNext( getSection(), topic );
        addDocumentLink( topic );
    }

    @Override
    protected void onRender() {
        super.onRender();
        // addTopicItems( getTopic() );
    }

    private void addTitle() {
        AjaxLink<String> titleLink = new AjaxLink<String>( "title" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                makeVisible( lightBox, false );
                Change change = new Change( Change.Type.Collapsed, Channels.GUIDE_ID );
                update( target, change );
            }
        };
        titleLink.setOutputMarkupId( true );
        addOrReplace( titleLink );
    }

    private void addGlossaryLink() {
        glossaryLink = new AjaxLink<String>( "glossary" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                UserRole userRole = getUserRole();
                rememberState();
                addBack();
                target.add( backLink );
                selectTopicInSection( getUserRoleId(), userRole.getGlossarySection(), userRole.getGlossaryTopic(), target );
            }
        };
        glossaryLink.setOutputMarkupId( true );
        makeVisible( glossaryLink, getUserRole().hasGlossary() );
        addOrReplace( glossaryLink );
    }

    private Map<String, String[]> getGlossary() {
        UserRole userRole = getUserRole();
        return guide.getGlossary( userRole );
    }

    private String getGlossarySectionId() {
        UserRole userRole = getUserRole();
        return userRole.getGlossarySection();
    }


    private void addBack() {
        backLink = new AjaxLink<String>( "back" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                goBack( target );
            }
        };
        backLink.setOutputMarkupId( true );
        makeVisible( backLink, canGoBack() );
        addOrReplace( backLink );
    }

    private void goBack( AjaxRequestTarget target ) {
        if ( canGoBack() ) {
            Section priorSection = sectionStack.pop();
            Topic priorTopic = topicStack.pop();
            addBack();
            target.add( backLink );
            selectTopicInSection( getUserRoleId(), priorSection.getId(), priorTopic.getId(), target );
        }
    }

    private boolean canGoBack() {
        return !sectionStack.isEmpty() && !topicStack.isEmpty();
    }


    private void addTopicName( Topic topic ) {
        Label topicNameLabel = new Label( "topicName", topic.getName() );
        topicNameLabel.setOutputMarkupId( true );
        content.addOrReplace( topicNameLabel );
    }

    private void addTopicItems( Topic topic ) {
        ListView<TopicItem> topicItemListView = new ListView<TopicItem>(
                "topicItems",
                new PropertyModel<List<TopicItem>>( topic, "topicItems" )
        ) {
            @Override
            protected void populateItem( ListItem<TopicItem> topicListItem ) {
                final TopicItem topicItem = topicListItem.getModelObject();
                final TopicItemScript topicScript = topicItem.getItemScript();
                AjaxLink<String> actionLink = new AjaxLink<String>( "actionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( topicScript != null )
                            runTopicScript( topicScript, target );
                    }
                };
                actionLink.add( new Label(
                        "action",
                        topicScript == null
                                ? ""
                                : topicScript.getAction() ) );
                topicListItem.add( actionLink );
                actionLink.setVisible( canRunScript( topicScript ) );
                topicListItem.add( getDescriptionLabel( topicItem )
                        .setVisible( !topicItem.getDescription().isEmpty() ) );
            }
        };
        topicItemListView.setOutputMarkupId( true );
        content.addOrReplace( topicItemListView );
    }

    private Label getDescriptionLabel(  TopicItem topicItem ) {
        final StringBuilder htmlBuilder = new StringBuilder();
        Label label = new Label( "description", "" ) {
            @Override
            protected void onRender() {
                super.onRender();
                getResponse().write( htmlBuilder.toString() );
            }
        };
        label.setEscapeModelStrings( false );
        label.setOutputMarkupId( true );
        label.add( new HelpAjaxBehavior(
                htmlBuilder,
                guide,
                getUserRole(),
                topicItem,
                getGlossary(),
                imagingService ) {
            @Override
            protected void respond( AjaxRequestTarget target ) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                if ( params.getParameterNames().contains( IMAGE_PARAM ) ) {
                    String imageSrc = params.getParameterValue( IMAGE_PARAM ).toString();
                    String caption = params.getParameterValue( CAPTION_PARAM ).toString( "" );
                    if ( imageSrc != null ) {
                        addLightbox( imageSrc, caption.replaceAll( "_", " " ) );
                        makeVisible( lightBox, true );
                        target.add( lightBox );
                    }
                } else if ( params.getParameterNames().contains( SECTION_PARAM ) ) {
                    String sectionId = params.getParameterValue( SECTION_PARAM ).toString( "" );
                    String topicId = params.getParameterValue( TOPIC_PARAM ).toString( "" );
                    openOn( sectionId, topicId, target );
                }
            }
        } );

        return label;
    }


    private void addDefinitions( Topic topic ) {
        List<TopicRef> topicRefs = topic.getSortedDefinitions( getUserRole() );
        WebMarkupContainer doDefinitionsContainer = new WebMarkupContainer( "definitionsContainer" );
        doDefinitionsContainer.setOutputMarkupId( true );
        doDefinitionsContainer.setVisible( !topicRefs.isEmpty() );
        ListView<TopicRef> doDefinitionListView = new ListView<TopicRef>(
                "definitions",
                topicRefs
        ) {
            @Override
            protected void populateItem( ListItem<TopicRef> item ) {
                TopicRef topicRef = item.getModelObject();
                UserRole userRole = getUserRole();
                final Section defSection = userRole != null
                        ? userRole.findSection( topicRef.getSectionId() )
                        : null;
                final Topic defTopic = defSection == null
                        ? null
                        : defSection.findTopic( topicRef.getTopicId() );
                AjaxLink<String> defLink = new AjaxLink<String>( "definitionLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        if ( defSection != null && defTopic != null )
                            openOn( defSection.getId(), defTopic.getId(), target );
                    }
                };
                String labelString = defTopic == null ? "???" : defTopic.getName();
                Label defLabel = new Label( "definitionText", labelString );
                defLink.add( defLabel );
                item.add( defLink );
            }
        };
        doDefinitionsContainer.add( doDefinitionListView );
        content.addOrReplace( doDefinitionsContainer );
    }


    private void addDoNext( final Section section, Topic topic ) {
        List<TopicRef> topicRefs = topic.getNextTopics();
        WebMarkupContainer doNextContainer = new WebMarkupContainer( "doNextContainer" );
        doNextContainer.setOutputMarkupId( true );
        doNextContainer.setVisible( !topicRefs.isEmpty() );
        ListView<TopicRef> doNextListView = new ListView<TopicRef>(
                "doNexts",
                topicRefs
        ) {
            @Override
            protected void populateItem( ListItem<TopicRef> item ) {
                TopicRef topicRef = item.getModelObject();
                UserRole userRole = getUserRole();
                final Section nextSection = userRole != null
                        ? userRole.findSection( topicRef.getSectionId() )
                        : null;
                final Topic nextTopic = nextSection == null
                        ? null
                        : nextSection.findTopic( topicRef.getTopicId() );
                AjaxLink<String> nextLink = new AjaxLink<String>( "doNextLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        openOn( nextSection.getId(), nextTopic.getId(), target );
                    }
                };
                String labelString = nextTopic == null ? "???" : nextTopic.getName();
                Label doNextLabel = new Label( "doNextText", labelString );
                nextLink.add( doNextLabel );
                item.add( nextLink );
            }
        };
        doNextContainer.add( doNextListView );
        content.addOrReplace( doNextContainer );
    }

    private void rememberState() {
        UserRole userRole = getUserRole();
        Section currentSection = userRole.findSection( sectionId );
        Topic currentTopic = null;
        if ( currentSection != null )
            currentTopic = currentSection.findTopic( topicId );
        if ( currentSection != null && currentTopic != null ) {
            sectionStack.push( currentSection );
            topicStack.push( currentTopic );
        }
    }

    private void openOn( String sectionId, String topicId, AjaxRequestTarget target ) {
        rememberState();
        addBack();
        target.add( backLink );
        selectTopicInSection( getUserRoleId(), sectionId, topicId, target );
    }


    private boolean canRunScript( TopicItemScript topicScript ) {
        return topicScript != null && !CollectionUtils.exists(
                topicScript.getScriptChanges(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return makeChange( (ScriptChange) object ) == null;
                    }
                }
        );
    }

    private void runTopicScript( TopicItemScript topicScript, AjaxRequestTarget target ) {
        if ( topicScript != null ) {
            for ( ScriptChange scriptChange : topicScript.getScriptChanges() ) {
                Change change = makeChange( scriptChange );
                if ( change != null ) {
                    Updatable updatable = getUpdatable( scriptChange );
                    if ( updatable != null ) {
                        updatable.changed( change );
                        updatable.updateWith( target, change, new ArrayList<Updatable>() );
                    } else {
                        break;
                    }
                } else {
                    break; // stop running the script
                }
            }
        }
    }

    private Updatable getUpdatable( ScriptChange scriptChange ) {
        String updatableTargetPath = scriptChange.getUpdateTargetPath();
        if ( updatableTargetPath == null ) {
            return planPage();
        } else {
            if ( updatableTargetPath.equals( "planPage" ) ) {
                return planPage();
            } else {
                return (Updatable) ChannelsUtils.getProperty( planPage(), updatableTargetPath, null );
            }
        }
    }


    private Change makeChange( ScriptChange scriptChange ) {
        if ( scriptChange == null ) return null;
        Change change;
        Change.Type type = Change.Type.valueOf( scriptChange.getChangeType() );
        if ( scriptChange.getSubjectId() != null ) {
            change = new Change( type, scriptChange.getSubjectId() );
        } else {
            Identifiable identifiable = getSubject( scriptChange );
            if ( identifiable == null )
                return null;
            else
                change = new Change( type, identifiable );
        }
        change.setProperty( scriptChange.getProperty() );
        for ( ChangeQualifier qualifier : scriptChange.getQualifiers() ) {
            change.addQualifier( qualifier.getName(), qualifier.getValue() );
        }
        return change;
    }

    private Identifiable getSubject( ScriptChange scriptChange ) {
        String subjectPath = scriptChange.getSubjectPath();
        Object bean;
        if ( subjectPath.startsWith( HelpScriptable.GUIDE + "." ) ) {
            subjectPath = subjectPath.substring( HelpScriptable.GUIDE.length() + 1 );
            bean = (HelpScriptable) this;
        } else {
            bean = guide.getContext();
        }
        try {
            return (Identifiable) ChannelsUtils.getProperty(
                    bean,
                    subjectPath,
                    null );
        } catch ( Exception e ) {
            LOG.warn( "Help subject not found: " + scriptChange.getSubjectPath() );
            return null;
        }
    }


    private void addDocumentLink( Topic topic ) {
        TopicDocument topicDocument = topic.getDocument();
        WebMarkupContainer docLink = new WebMarkupContainer( "documentLink" );
        docLink.setOutputMarkupId( true );
        if ( topicDocument != null ) {
            String serverUrl = guideReader.getServerUrl();
            String url = serverUrl
                    + ( serverUrl.endsWith( File.separator ) ? "" : File.separator )
                    + "doc/" + guideName + "/" + topicDocument.getUrl();
            docLink.add( new AttributeModifier( "href", url ) );
            docLink.add( new AttributeModifier( "target", "_blank" ) );
            addTipTitle( docLink, topicDocument.getTitle() );
        }
        docLink.setVisible( topicDocument != null );
        content.addOrReplace( docLink );
    }

    @Override
    public void setContext( Map<String, Object> context ) {
        guide.setContext( context );
    }

    @Override
    public void selectTopicInSection( String sectionId, String topicId, AjaxRequestTarget target ) {
        selectTopicInSection( null, sectionId, topicId, target );
    }

    @Override
    public void selectTopicInSection( String userRoleId, String sectionId, String topicId, AjaxRequestTarget target ) {
        this.userRoleId = userRoleId;
        this.sectionId = sectionId;
        this.topicId = topicId;
        addGlossaryLink();
        target.add( glossaryLink );
        initContent();
        target.add( content );
    }

    private UserRole getUserRole() {
        return guide.findUserRole(
                userRoleId == null
                        ? defaultUserRoleId
                        : userRoleId );
    }

    private Section getSection() {
        UserRole userRole = getUserRole();
        Section section = userRole != null
                ? userRole.findSection(
                sectionId == null
                        ? userRole.getSections().get( 0 ).getId()
                        : sectionId )
                : null;
        sectionId = section == null ? null : section.getId();
        return section;
    }

    private Topic getTopic() {
        Section section = getSection();
        Topic topic = section != null ?
                section.findTopic(
                        topicId == null
                                ? section.getTopics().get( 0 ).getId()
                                : topicId )
                : null;
        topicId = topic == null ? null : topic.getId();
        return topic;
    }

    // HelpScriptable - Script support

    private <T extends ModelObject> T chooseOne( List<T> choices ) {
        if ( choices.size() > 0 )
            return choices.get( new Random().nextInt( choices.size() ) );
        else
            return null;
    }

    public Flow getAnyFlow() {
        return chooseOne( getQueryService().list( Flow.class ) );
    }

    @Override
    public Actor getAnyActualAgent() {
        return chooseOne( getQueryService().listActualEntities( Actor.class ) );
    }

    @Override
    public Event getAnyEvent() {
        return chooseOne( getQueryService().list( Event.class ) );
    }

    @Override
    public Phase getAnyPhase() {
        return chooseOne( getQueryService().list( Phase.class ) );
    }

    @Override
    public Organization getAnyActualOrganization() {
        return chooseOne( getQueryService().listActualEntities( Organization.class ) );
    }

    @Override
    public Role getAnyRole() {
        return chooseOne( getQueryService().list( Role.class ) );
    }

    @Override
    public Place getAnyActualPlace() {
        return chooseOne( getQueryService().listActualEntities( Place.class ) );
    }

    @Override
    public TransmissionMedium getAnyMedium() {
        return chooseOne( getQueryService().list( TransmissionMedium.class ) );
    }

    @Override
    public InfoProduct getAnyInfoProduct() {
        return chooseOne( getQueryService().list( InfoProduct.class ) );
    }

    @Override
    public InfoFormat getAnyInfoFormat() {
        return chooseOne( getQueryService().list( InfoFormat.class ) );
    }

    @Override
    public Function getAnyFunction() {
        return chooseOne( getQueryService().list( Function.class ) );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Flow getAnySharingFlow() {
        return chooseOne( (List<Flow>) CollectionUtils.select(
                getQueryService().list( Flow.class ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Flow) object ).isSharing();
                    }
                }
        ) );
    }

    @Override
    public RFISurvey getUnknownRFISurvey() {
        return RFISurvey.UNKNOWN;
    }

    @Override
    public Feedback getUnknownFeedback() {
        return Feedback.UNKNOWN;
    }
}
