package com.mindalliance.channels.pages.components.help;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
import com.mindalliance.channels.pages.components.guide.IGuidePanel;
import info.bliki.wiki.model.WikiModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/11/13
 * Time: 2:26 PM
 */
public class HelpPanel extends AbstractUpdatablePanel implements IGuidePanel {

    @SpringBean
    private GuideReader guideReader;

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

    public HelpPanel( String id, String guideName, String defaultUserRoleId, Map<String, Object> context ) {
        super( id );
        this.guideName = guideName;
        this.defaultUserRoleId = defaultUserRoleId;
        guide = guideReader.getGuide( guideName );
        guide.setContext( context );
        init();
    }

    private void init() {
        addTitle();
        addGlossary();
        addBack();
        content = new WebMarkupContainer( "content" );
        content.setOutputMarkupId( true );
        add( content );
        initContent();
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    private void initContent() {
        Topic topic = getTopic();
        addTopicName( topic );
        addTopicItems( topic );
        addDefinitions( getSection(), topic );
        addDoNext( getSection(), topic );
        addDocumentLink( topic );
    }

    private void addTitle() {
        AjaxLink<String> titleLink = new AjaxLink<String>( "title" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, Channels.GUIDE_ID );
                update( target, change );
            }
        };
        titleLink.setOutputMarkupId( true );
        addOrReplace( titleLink );
    }

    private void addGlossary() {
        glossaryLink = new AjaxLink<String>( "glossary" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                selectTopicInSection( getUserRoleId(), "concepts", "glossary", target );
            }
        };
        glossaryLink.setOutputMarkupId( true );
        makeVisible( glossaryLink, getUserRole().getId().equals( "planner" ) );
        addOrReplace( glossaryLink );
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

    private void addDefinitions( final Section section, Topic topic ) {
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
                        openOn( defSection, defTopic, target );
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
                        openOn( nextSection, nextTopic, target );
                    }
                };
                String sectionPrefix = ( nextSection == null || nextSection.equals( getSection() )
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

    private void openOn( Section section, Topic topic, AjaxRequestTarget target ) {
        rememberState();
        addBack();
        target.add( backLink );
        selectTopicInSection( getUserRoleId(), section.getId(), topic.getId(), target );
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
                guide.getContext(),
                scriptChange.getSubjectPath(),
                null );
    }

    private Label getDescriptionLabel( TopicItem topicItem ) {
        Label label = new Label( "description", wikimediaToHtml( topicItem.getDescription() ) );
        label.setEscapeModelStrings( false );
        return label;
    }

    private String wikimediaToHtml( String content ) {
        WikiModel wikiModel = new WikiModel( "", "" );
        String html = wikiModel.render( content );
        html = html.replaceAll( "<a ", "<a target='_blank' " );
        return html;

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
        addGlossary();
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
        return userRole != null
                ? userRole.findSection(
                sectionId == null
                        ? userRole.getSections().get( 0 ).getId()
                        : sectionId )
                : null;
    }

    private Topic getTopic() {
        Section section = getSection();
        return section != null ?
                section.findTopic(
                        topicId == null
                                ? section.getTopics().get( 0 ).getId()
                                : topicId )
                : null;
    }
}
