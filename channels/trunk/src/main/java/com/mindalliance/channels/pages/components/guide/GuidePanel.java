package com.mindalliance.channels.pages.components.guide;

import com.google.code.jqwicket.ui.accordion.AccordionOptions;
import com.google.code.jqwicket.ui.accordion.AccordionWebMarkupContainer;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.guide.ChangeQualifier;
import com.mindalliance.channels.guide.Guide;
import com.mindalliance.channels.guide.GuideReader;
import com.mindalliance.channels.guide.IGuidePanel;
import com.mindalliance.channels.guide.ScriptChange;
import com.mindalliance.channels.guide.Section;
import com.mindalliance.channels.guide.Topic;
import com.mindalliance.channels.guide.TopicDocument;
import com.mindalliance.channels.guide.TopicItem;
import com.mindalliance.channels.guide.TopicItemScript;
import com.mindalliance.channels.guide.TopicRef;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Guide panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/24/12
 * Time: 3:26 PM
 */
public class GuidePanel extends AbstractUpdatablePanel implements IGuidePanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( GuidePanel.class );

    @SpringBean
    private GuideReader guideReader;

    private String name;
    private Guide guide;
    private Section selectedSection;
    private Topic selectedTopic;
    private Topic openedTopic;
    private AccordionWebMarkupContainer accordion;
    private Map<Topic, WebMarkupContainer> topicDocs = new HashMap<Topic, WebMarkupContainer>();
    private Map<Topic, Component> sectionDivs = new HashMap<Topic, Component>();
    private AjaxFallbackLink hideGuideLink;

    public GuidePanel( String id, String name ) {
        super( id );
        this.name = name;
        init();
    }

    private void init() {
        guide = guideReader.getGuide( name );
        addGuideName();
        addHideImage();
        addGuideAccordion();
  //       addGuideDoc();
    }

    private void addGuideName() {
        add( new Label( "guideName", guide.getName() ) );
    }


    private void addHideImage() {
        hideGuideLink = new AjaxFallbackLink( "hideGuide" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Channels.GUIDE_ID );
                change.setMessage( "To re-open, select Guide in the top Show menu." );
                update( target, change );
            }
        };
//        addTipTitle( hideGuideLink, "Hide this panel", true );   // Does not go away because its container is closed before it can fade out
        add( hideGuideLink );
    }

    private void addGuideAccordion() {
        sectionDivs = new HashMap<Topic, Component>();
        AccordionOptions options = new AccordionOptions();
        options.active( false );
        // options.addCssResourceReferences( new CssResourceReference( getClass(), "res/guide.css" ) );
        accordion = new AccordionWebMarkupContainer( "accordion", options );
        accordion.setOutputMarkupId( true );
        accordion.add( new ListView<Section>(
                "sections",
                guide.getSections() ) {
            protected void populateItem( final ListItem<Section> sectionItem ) {
                final Section section = sectionItem.getModelObject();
                Label sectionLabel = new Label( "title", section.getName() );
                sectionItem.add( sectionLabel );
                sectionLabel.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        if ( selectedSection != null && selectedSection.equals( section ) ) {
                            selectedSection = null;
                            selectedTopic = null;
                            accordion.activate( target, false );
                            target.add( accordion );
                        } else {
                            selectedSection = section;
                            selectTopic( selectedSection, null, target );
                        }
                        String js = "setTimeout('" + accordion.resize().toString( true ) + "',500);";
                        target.appendJavaScript( js );
                    }
                } );
                ListView<Topic> topicList = new ListView<Topic>(
                        "topics",
                        section.getTopics()
                ) {
                    @Override
                    protected void populateItem( ListItem<Topic> topicListItem ) {
                        final Topic topic = topicListItem.getModelObject();
                        sectionDivs.put( topic, sectionItem );
                        WebMarkupContainer doc = getDoc( section, topic );
                        makeVisible( doc, false );
                        topicListItem.add( doc );
                        AjaxLink<String> topicLink = new AjaxLink<String>( "topicLink" ) {
                            @Override
                            public void onClick( AjaxRequestTarget target ) {
                                selectTopic( section, topic, target );
                            }
                        };
                        Label topicNameLabel = new Label( "topicName", topic.getName() );
                        topicLink.add( topicNameLabel );
                        topicListItem.add( topicLink );
                    }
                };
                sectionItem.setOutputMarkupId( true );
                sectionItem.add( topicList );
            }
        } );
        addOrReplace( accordion );
    }

    private WebMarkupContainer getDoc( Section section, Topic topic ) {
        WebMarkupContainer docContainer = topicDocs.get( topic );
        if ( docContainer == null ) {
            docContainer = new WebMarkupContainer( "topic" );
            docContainer.setOutputMarkupId( true );
            docContainer.add( getTopicItemList( topic ) );
            docContainer.add( getDoNextContainer( section, topic ) );
            docContainer.add( getDocumentContainer( topic ) );
            topicDocs.put( topic, docContainer );
        }
        return docContainer;
    }

    private void addGuideDoc() {
        add( getGuideDoc() );
    }

    private Label getGuideDoc() {
        Label label = new Label( "guideDoc", wikimediaToHtml(
                selectedTopic == null
                        ? guide.getDescription()
                        : "" ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private ListView<TopicItem> getTopicItemList( Topic topic ) {
        ListView<TopicItem> topicItemListView = new ListView<TopicItem>(
                "topicItems",
                new PropertyModel<List<TopicItem>>( topic, "topicItems" )
        ) {
            @Override
            protected void populateItem( ListItem<TopicItem> item ) {
                final TopicItem topicItem = item.getModelObject();
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
                item.add( actionLink );
                actionLink.setVisible( canRunScript( topicScript ) );
                item.add( getDescriptionLabel( topicItem )
                        .setVisible( !topicItem.getDescription().isEmpty() ) );
            }
        };
        return topicItemListView;
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
        return (Identifiable) ChannelsUtils.getProperty(
                getGuide(),
                scriptChange.getSubjectPath(),
                null );
    }

    private Label getDescriptionLabel( TopicItem topicItem ) {
        Label label = new Label( "description", wikimediaToHtml( topicItem.getDescription() ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private String wikimediaToHtml( String wikimedia ) {
        // First substitute template variable
        GuideTemplateContext guideTemplateContext = new GuideTemplateContext();
        String convertedWikimedia = ChannelsUtils.convertTemplate( wikimedia.trim(), guideTemplateContext );
        String helpUrl = guideTemplateContext.getHelpUrl();
        WikiModel wikiModel = new WikiModel( helpUrl + "/${image}", helpUrl + "/${title}" );
        String html = wikiModel.render( convertedWikimedia );
        html = html.replaceAll( "<a ", "<a target='_blank' " );
        return html;
    }


    private WebMarkupContainer getDoNextContainer( final Section section, Topic topic ) {
        List<TopicRef> topicRefs = topic.getNextTopics();
        WebMarkupContainer doNextContainer = new WebMarkupContainer( "doNextContainer" );
        doNextContainer.setVisible( !topicRefs.isEmpty() );
        ListView<TopicRef> doNextListView = new ListView<TopicRef>(
                "doNexts",
                topicRefs
        ) {
            @Override
            protected void populateItem( ListItem<TopicRef> item ) {
                TopicRef topicRef = item.getModelObject();
                final Section nextSection = guide.derefSection( topicRef.getSectionId() );
                final Topic nextTopic = nextSection == null
                        ? null
                        : nextSection.findTopic( topicRef.getTopicId() );
                AjaxLink<String> nextLink = new AjaxLink<String>( "doNextLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        openOn( section, nextTopic, target );
                    }
                };
                String sectionPrefix = ( nextSection == null || nextSection.equals( selectedSection )
                        ? ""
                        : "(" + nextSection.getName() + ") " );
                String labelString = sectionPrefix
                        + ( nextTopic == null ? "???" : nextTopic.getName() );
                Label doNextLabel = new Label( "doNextText", labelString );
                nextLink.add( doNextLabel );
                item.add( nextLink );
            }
        };
        doNextContainer.add( doNextListView );
        return doNextContainer;
    }

    private void openOn( Section section, Topic topic, AjaxRequestTarget target ) {
        accordion.activate(target, guide.findSectionIndex( section ) );
        selectTopic( section, topic, target );
        String js = "$('#guide').scrollTop(0);";
        // String js = "setTimeout('" + accordion.resize().toString( true ) + "',500);";
        target.appendJavaScript( js );
    }

    private WebMarkupContainer getDocumentContainer( Topic topic ) {
        TopicDocument topicDocument = topic.getDocument();
        WebMarkupContainer documentContainer = new WebMarkupContainer( "document" );
        WebMarkupContainer docLink = new WebMarkupContainer( "documentLink" );
        documentContainer.add( docLink );
        if ( topicDocument != null ) {
            String serverUrl = guideReader.getServerUrl();
            String url = serverUrl
                    + ( serverUrl.endsWith( File.separator ) ? "" : File.separator )
                    + "doc/" + name + "/" + topicDocument.getUrl();
            docLink.add( new AttributeModifier( "href", url ) );
            docLink.add(  new AttributeModifier( "target", "_blank" ) );
            addTipTitle( docLink, topicDocument.getTitle() );
        }
        documentContainer.setVisible( topicDocument != null );
        return documentContainer;
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        if ( selectedTopic != null ) {
            target.add( getDoc( selectedSection, selectedTopic ) );
        } else if ( selectedSection == null ) {
            target.add( this );
        }
    }

    @Override
    public void setContext( Map<String, Object> context ) {
        guide.setContext( context );
    }

    @Override
    public void selectTopicInSection( String sectionId, String topicId, AjaxRequestTarget target ) {
        Section section = guide.findSection( sectionId );
        if ( section != null ) {
            Topic topic = section.findTopic( topicId );
            if ( topic != null ) {
                openOn( section, topic, target );
                return;
            }
        }
        LOG.warn( "Topic " + topicId + " not found in section " + sectionId + " of guide " + guide.getName() );
    }

    private void selectTopic( Section section, Topic topic, AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Selected, Channels.GUIDE_ID );
        change.setProperty( "topic" );
        change.addQualifier( "topic", topic );
        change.addQualifier( "section", section );
        update( target, change );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            Topic topic = (Topic) change.getQualifier( "topic" );
            Section section = (Section) change.getQualifier( "section" );
            openedTopic = selectedTopic;
            if ( topic == null || selectedTopic != null && topic.equals( selectedTopic ) ) {
                selectedTopic = null;
            } else {
                selectedSection = section;
                selectedTopic = topic;
            }
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.getId() == Channels.GUIDE_ID ) {
            if ( openedTopic != null ) {
                WebMarkupContainer doc = getDoc( selectedSection, openedTopic );
                makeVisible( doc, false );
                target.add( doc );
            }
            if ( selectedTopic != null ) {
                WebMarkupContainer doc = getDoc( selectedSection, selectedTopic );
                makeVisible( doc, true );
                target.add( doc );
            }
            accordion.resize( target );
        } else {
            super.updateWith( target, change, updated );
        }
    }


    // Script support: the bean against which all subject paths are evaluated

    public GuideInfo getGuide() {
        return new GuideInfo();
    }

    public class GuideTemplateContext implements Serializable {

        public GuideTemplateContext() {
        }

        public String getHelpUrl() {
            String serverUrl = guideReader.getServerUrl();
            return serverUrl
                    + ( serverUrl.endsWith( "/" ) ? "" : "/" )
                    + "doc/channels_user_guide";
        }

    }

    public class GuideInfo implements Serializable {

        public GuideInfo() {
        }

        public GuideInfo getGuide() {
            return this;
        }

        public Plan getPlan() {
            return GuidePanel.this.getPlan();
        }

        public PlanPage getPlanPage() {
            try {
                return GuidePanel.this.planPage();
            } catch( Exception e ) {
                return null;
            }
        }

        public Flow getAnyFlow() {
            List<Flow> flows = getPlanPage().getPart().getAllFlows();
            return flows.isEmpty()
                    ? null
                    : flows.get( new Random( 13 ).nextInt( flows.size() ) );
        }

        public Flow getAnySharingFlow() {
            List<Flow> flows = new ArrayList<Flow>();
            flows.addAll( getPlanPage().getPart().getAllSharingSends() );
            flows.addAll( getPlanPage().getPart().getAllSharingReceives() );
            return flows.isEmpty()
                    ? null
                    : flows.get( new Random( 13 ).nextInt( flows.size() ) );
        }


        public Actor getAnyActualAgent() {
            List<Actor> actualActors = getQueryService().listActualEntities( Actor.class );
            return actualActors.isEmpty()
                    ? null
                    : actualActors.get( new Random( 13 ).nextInt( actualActors.size() ) );
        }

        public Organization getAnyActualOrganization() {
            List<Organization> actualOrgs = getQueryService().listActualEntities( Organization.class );
            return actualOrgs.isEmpty()
                    ? null
                    : actualOrgs.get( new Random( 13 ).nextInt( actualOrgs.size() ) );
        }

        public Event getAnyEvent() {
            List<Event> events = getQueryService().listKnownEntities( Event.class );
            return events.isEmpty()
                    ? null
                    : events.get( new Random( 13 ).nextInt( events.size() ) );
        }

        public Phase getAnyPhase() {
            List<Phase> phases = getQueryService().listKnownEntities( Phase.class );
            return phases.isEmpty()
                    ? null
                    : phases.get( new Random( 13 ).nextInt( phases.size() ) );
        }

        public Place getAnyActualPlace() {
            List<Place> actualPlaces = getQueryService().listActualEntities( Place.class );
            return actualPlaces.isEmpty()
                    ? null
                    : actualPlaces.get( new Random( 13 ).nextInt( actualPlaces.size() ) );
        }

        public Role getAnyRole() {
            List<Role> roles = getQueryService().listKnownEntities( Role.class );
            return roles.isEmpty()
                    ? null
                    : roles.get( new Random( 13 ).nextInt( roles.size() ) );
        }

        public TransmissionMedium getAnyMedium() {
            List<TransmissionMedium> media = getQueryService().listKnownEntities( TransmissionMedium.class );
            return media.isEmpty()
                    ? null
                    : media.get( new Random( 13 ).nextInt( media.size() ) );
        }

        public InfoProduct getAnyInfoProduct() {
            List<InfoProduct> infoProducts = getQueryService().listKnownEntities( InfoProduct.class );
            return infoProducts.isEmpty()
                    ? null
                    : infoProducts.get( new Random( 13 ).nextInt( infoProducts.size() ) );
        }

        public InfoFormat getAnyInfoFormat() {
            List<InfoFormat> formats = getQueryService().listKnownEntities( InfoFormat.class );
            return formats.isEmpty()
                    ? null
                    : formats.get( new Random( 13 ).nextInt( formats.size() ) );
        }

        public RFISurvey getUnknownRFISurvey() {
            return RFISurvey.UNKNOWN;
        }

        public Feedback getUnknownFeedback() {
            return Feedback.UNKNOWN;
        }

        public Requirement getUnknownRequirement() {
            return Requirement.UNKNOWN;
        }

    }


}
