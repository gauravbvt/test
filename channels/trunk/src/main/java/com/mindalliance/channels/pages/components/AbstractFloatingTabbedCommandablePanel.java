package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/26/12
 * Time: 11:20 AM
 */
public abstract class AbstractFloatingTabbedCommandablePanel extends AbstractCommandablePanel implements Guidable {

    /**
     * Pad top on move.
     */
    static protected final int PAD_TOP = 68;
    /**
     * Pad left on move.
     */
    static protected final int PAD_LEFT = 7;
    /**
     * Pad bottom on move and resize.
     */
    static protected final int PAD_BOTTOM = 7;
    /**
     * Pad right on move and resize.
     */
    static protected final int PAD_RIGHT = 7;

    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 300;

    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;

    /**
     * Background color.
     */
    private static final String BG_COLOR = "#f6f6f6";
    /**
     * Border width.
     */
    private static final int BORDER_WIDTH = 2;
    /**
     * Border color.
     */
    private static final String BORDER_COLOR = "#999999";
    /**
     * Bounds on random delta of opening position.
     */
    private static final int NON_OVERLAP_DELTA = 20;
    /**
     * JavaScript.
     */
    private static final JavaScriptResourceReference JAVASCRIPT = new JavaScriptResourceReference(
            AbstractFloatingCommandablePanel.class, "res/FloatingCommandablePanel.js" );
    /**
     * Title bar.
     */
    private WebMarkupContainer header;

    private Component actionsMenu;

    private List<Tab> tabs;

    private String selectedTabName;

    private WebMarkupContainer content;
    /**
     * Explanation label.
     */
    private Label explanationLabel;

    private Random random = new Random();
    private WebMarkupContainer resizer;
    private boolean minimized = false;
    private AjaxLink minimizeLink;
    private static final int MINIMIZED_TITLE_SIZE = 27;
    private static final int MINIMIZED_HEIGHT = 38;
    private WebMarkupContainer moveBar;

    public AbstractFloatingTabbedCommandablePanel( String id ) {
        this( id, null, null );
    }

    public AbstractFloatingTabbedCommandablePanel( String id, IModel<? extends Identifiable> iModel ) {
        this( id, iModel, null );
    }


    public AbstractFloatingTabbedCommandablePanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    protected abstract Component makeActionMenuOrLabel( String id );

    protected abstract List<Tab> makeTabs();

    protected abstract List<PathIcon> getPathIcons( String id );


    private void init() {
        addMoveBar();
        addHeader();
        addContent();
        addResizer();
        setLayout();
    }

    private void addMoveBar() {
        moveBar = new WebMarkupContainer( "moveBar" );
        String moveScript = MessageFormat.format(
                "Floater.beginMove(this.parentNode.parentNode,event,{0,number,####},{1,number,####},{2,number,####},{3,number,####});",
                getPadTop(),
                getPadLeft(),
                getPadBottom(),
                getPadRight()
        );
        moveBar.add( new AttributeModifier( "onMouseDown", new Model<String>( moveScript ) ) );
        add( moveBar );
        addTitle();
        addHelp();
        addMinimize();
        addClose();
     }

    /**
     * Add title to floating panel.
     */
    protected void addTitle() {
        Label titleLabel = new Label( "title", new Model<String>( getAdjustedTitle() ) );
        titleLabel.setOutputMarkupId( true );
        moveBar.addOrReplace( titleLabel );
    }

    private String getAdjustedTitle() {
        String explanation = getTitle();
        return minimized
                ? StringUtils.abbreviate( explanation, MINIMIZED_TITLE_SIZE )
                : explanation;
    }

    private void addClose() {
        AjaxLink<?> closeLink = new AjaxLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        closeLink.add( new AttributeModifier( "onMouseOver", new Model<String>( "this.focus();" ) ) );
        moveBar.add( closeLink );
    }

    private void addHelp() {
        moveBar.add( makeHelpIcon( "help", this, "images/float-bar-help.png" ) );
    }

    private void addMinimize() {
        minimizeLink = new AjaxLink( "minimize" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                minimizeNormalize( target );
            }
        };
        minimizeLink.setOutputMarkupId( true );
        WebMarkupContainer icon = new WebMarkupContainer( "minimizeIcon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( minimized
                        ? "images/float-bar-maximize.png"
                        : "images/float-bar-minimize.png" ) ) );
        minimizeLink.add( icon );
        moveBar.addOrReplace( minimizeLink );
    }

    protected void minimizeNormalize( AjaxRequestTarget target ) {
        minimized = !minimized;
        makeVisible( header, !minimized );
        makeVisible( content, !minimized );
        makeVisible( resizer, !minimized );
        addMinimize();
        addTitle();
        target.add( this );
        String minimizeNormalizeScript = "Floater.minimizeNormalize('"
                + minimizeLink.getMarkupId() + "', "
                + getPadBottom() + ", "
                + MINIMIZED_HEIGHT + ", "
                + minimized + ");";
        target.appendJavaScript( minimizeNormalizeScript );
    }

    private void addHeader() {
        // move
        header = new WebMarkupContainer( "header" );
        header.setOutputMarkupId( true );
        addOrReplace( header );
        addExplanation();
        addActionsMenu();
        addTabs();
        addPathIcons();
    }

    private void addHelpIcon() {
        Component helpIcon = makeHelpIcon( "help", (Guidable)this );
        header.add( helpIcon );
    }

    protected void addActionsMenu() {
        actionsMenu = makeActionMenuOrLabel( "actionMenu" );
        if ( actionsMenu == null ) {
            actionsMenu = new Label("actionMenu", "");
            actionsMenu.setVisible( false );
        }
        actionsMenu.setOutputMarkupId( true );
        header.addOrReplace( actionsMenu );
    }

    public void renderHead( IHeaderResponse response ) {
        super.renderHead( response );
        response.renderJavaScriptReference( JAVASCRIPT );
        String script = "Floater.onOpen('" + getMarkupId() + "');";
        response.renderOnDomReadyJavaScript( script );
    }

    public void setLayout() {
        String style = MessageFormat.format(
                "display:none;position:absolute;background-color:{0};border:{1}px solid {2};top:{3,number,#####}px;left:{4,number,#####}px;bottom:{5,number,#####}px;width:{6,number,#####}px;z-index:{7,number,#####};",
                BG_COLOR,
                BORDER_WIDTH,
                BORDER_COLOR,
                getTop(),
                getLeft(),
                getBottom(),
                getWidth(),
                getZIndex()
        );
        add( new AttributeModifier( "style", new Model<String>( style ) ) );
    }


     private void addTabs() {
        ListView<Tab> tabListView = new ListView<Tab>(
                "tabs",
                getTabs()
        ) {
            @Override
            protected void populateItem( final ListItem<Tab> item ) {
                final Tab tab = item.getModelObject();
                final AjaxLink<String> tabLink = new AjaxLink<String>( "tabLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        addHeader();
                        target.add( header );
                        update( target, tab.getChange() );
                    }
                };
                tabLink.add( new Label( "tabName", tab.getName() ) );
                tabLink.add( new AttributeModifier( "class", tab.getName().equals( getSelectedTabName() )? "active" : "" ) );
                item.add( tabLink );
            }
        };
        header.add( tabListView );
    }

    private List<Tab> getTabs() {
        if ( tabs == null ) {
            tabs = makeTabs();
        }
        return tabs;
    }


    private void addPathIcons() {
        ListView<PathIcon> pathIconListView = new ListView<PathIcon>(
                "pathIcons",
                getPathIcons( "pathIconLink" )
        ) {
            @Override
            protected void populateItem( final ListItem<PathIcon> item ) {
                final PathIcon pathIcon = item.getModelObject();
                final AbstractLink pathIconLink = pathIcon.getLink( );
                pathIconLink.setOutputMarkupId( true );
                Label pathIconNameLabel = new Label( "pathIconName", pathIcon.getName() );
                pathIconNameLabel.setOutputMarkupId( true );
                pathIconLink.addOrReplace( pathIconNameLabel );
                WebMarkupContainer img = new WebMarkupContainer( "pathIconImage" );
                img.setOutputMarkupId( true );
                img.add( new AttributeModifier( "src", pathIcon.getSrc() ) );
                img.add( new AttributeModifier( "alt", pathIcon.getAlt() ) );
                pathIconLink.addOrReplace( img );
                pathIconLink.setVisible( pathIcon.isVisible() );
                item.add( pathIconLink );
            }
        };
        header.add( pathIconListView );
    }


    private void addContent() {
        content = new WebMarkupContainer( "fp-content" );
        content.setOutputMarkupId( true );
        addOrReplace( content );
    }

    private void addResizer() {
        // resize
        resizer = new WebMarkupContainer( "resizer" );
        resizer.setOutputMarkupId( true );
        String resizeScript = MessageFormat.format(
                "Floater.beginResize(this.parentNode.parentNode,event,{0,number,####},{1,number,####},{2,number,####},{3,number,####});",
                getMinWidth(),
                getMinHeight(),
                getPadBottom(),
                getPadRight()
        );
        resizer.add( new AttributeModifier( "onMouseDown", new Model<String>( resizeScript ) ) );
        add( resizer );
    }

    protected WebMarkupContainer getContentContainer() {
        return content;
    }

    /**
     * Add title to floating panel.
     */
    protected void addExplanation() {
        explanationLabel = new Label( "explanation", new Model<String>( getAdjustedExplanation() ) );
        explanationLabel.setOutputMarkupId( true );
        header.addOrReplace( explanationLabel );
    }

    private String getAdjustedExplanation() {
        String explanation = getExplanation();
        return minimized
                ? StringUtils.abbreviate( explanation, MINIMIZED_TITLE_SIZE )
                : explanation;
    }

    // DEFAULT
    protected String getExplanation() {
        return getTitle();
    }

    /**
     * Get title of floating panel.
     *
     * @return a string
     */
    protected abstract String getTitle();

    /**
     * Refresh title.
     *
     * @param target an ajax request target
     */
    protected void refreshTitle( AjaxRequestTarget target ) {
        addExplanation();
        target.add( explanationLabel );
    }

    // DEFAULT

    protected int getPadTop() {
        return PAD_TOP;
    }

    protected int getPadLeft() {
        return PAD_LEFT;
    }

    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    protected int getPadRight() {
        return PAD_RIGHT;
    }

    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    protected int getMinHeight() {
        return MIN_HEIGHT;
    }


    /**
     * Close panel.
     *
     * @param target an ajax request target.
     */
    protected void close( AjaxRequestTarget target ) {
        minimized = false;
        doClose( target );
    }

    abstract protected void doClose( AjaxRequestTarget target );

    private int randomDelta() {
        return ( NON_OVERLAP_DELTA / 2 ) - random.nextInt( NON_OVERLAP_DELTA );
    }

    protected int getTop() {
        return 120 + randomDelta();
    }

    protected int getLeft() {
        return 35 + randomDelta();
    }

    protected int getBottom() {
        return 80;
    }

    protected int getWidth() {
        return 800;
    }

    protected int getZIndex() {
        return 10000;
    }

    /**
     * Refresh given change.
     *
     * @param target an ajax request target
     * @param change the nature of the change
     * @param aspect aspect shown
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        super.refresh( target, change, aspect );
        addHeader();
        target.add( header );
    }

    /**
     * Refresh menus.
     *
     * @param target ajax request target
     */
    public void refreshMenus( AjaxRequestTarget target ) {
        addActionsMenu();
        target.add( actionsMenu );
    }

    public String getSelectedTabName() {
        return selectedTabName;
    }

    public void setSelectedTabName( String selectedTabName ) {
        this.selectedTabName = selectedTabName;
    }

    public boolean isMinimized() {
        return minimized;
    }



    public class Tab implements Serializable {
        private String name;
        private String title;
        private Change change;

        public Tab( String name, Change change ) {
            this.change = change;
            this.name = name;
        }

        public Change getChange() {
            return change;
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle( String title ) {
            this.title = title;
        }
    }

    public class PathIcon implements Serializable {
        private String src;
        private String alt;
        private String name;
        private AbstractLink link;
        private boolean visible = true;

        public PathIcon( String name, String src, AbstractLink link ) {
            this.name = name;
            this.link = link;
            this.src = src;
        }

        public String getName() {
            return name;
        }

        public AbstractLink getLink() {
            return link;
        }

        public String getSrc() {
            return src;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt( String alt ) {
            this.alt = alt;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible( boolean visible ) {
            this.visible = visible;
        }
    }
}
