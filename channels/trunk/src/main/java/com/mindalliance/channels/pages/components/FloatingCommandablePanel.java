package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.Identifiable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
import java.util.Random;
import java.util.Set;

/**
 * Floating commandable panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Sep 30, 2009
 * Time: 9:26:10 PM
 */
abstract public class FloatingCommandablePanel extends AbstractCommandablePanel {


    /**
     * Background color.
     */
    private static final String BG_COLOR = "#e8e8e8";
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
    private static ResourceReference JAVASCRIPT = new JavascriptResourceReference(
            FloatingCommandablePanel.class, "res/FloatingCommandablePanel.js" );
    /**
     * Title bar.
     */
    private WebMarkupContainer moveBar;

    private WebMarkupContainer content;
    /**
     * Title label.
     */
    private Label titleLabel;

    private Random random = new Random();
    private WebMarkupContainer resizer;
    private boolean minimized = false;
    private AjaxFallbackLink minimizeLink;
    private static final int MINIMIZED_TITLE_SIZE = 27;
    public FloatingCommandablePanel( String id ) {
        this( id, null, null );
    }

    public FloatingCommandablePanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        setOutputMarkupId( true );
        add( JavascriptPackageResource.getHeaderContribution( JAVASCRIPT ) );
        // move
        moveBar = new WebMarkupContainer( "moveBar" );
        String moveScript = MessageFormat.format(
                "Floater.beginMove(this.parentNode.parentNode,event,{0,number,####},{1,number,####},{2,number,####},{3,number,####});",
                getPadTop(),
                getPadLeft(),
                getPadBottom(),
                getPadRight()
        );
        moveBar.add( new AttributeModifier( "onMouseDown", true, new Model<String>( moveScript ) ) );
        add( moveBar );
        addTitle();
        // minimize
        addMinimize();
        // close -- blur any entry field to make sure any change is taken
        AjaxFallbackLink<?> closeLink = new AjaxFallbackLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        closeLink.add( new AttributeModifier( "onMouseOver", true, new Model<String>( "this.focus();" ) ) );
        moveBar.add( closeLink );
        // Content
        addContent();
        // resizer
        addResizer();
        // styling
        setLayout();
    }

    private void addMinimize() {
        minimizeLink = new AjaxFallbackLink( "minimize" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                minimizeNormalize( target );
            }
        };
        minimizeLink.setOutputMarkupId( true );
        WebMarkupContainer icon = new WebMarkupContainer( "minimizeIcon" );
        icon.add( new AttributeModifier(
                "src",
                true,
                new Model<String>( minimized
                        ? "images/float-bar-maximize.png"
                        : "images/float-bar-minimize.png" ) ) );
        minimizeLink.add( icon );
        moveBar.addOrReplace( minimizeLink );
    }

    private void minimizeNormalize( AjaxRequestTarget target ) {
        minimized = !minimized;
        makeVisible( content, !minimized );
        makeVisible( resizer, !minimized );
        addMinimize();
        addTitle();
        target.addComponent( this );
        String minimizeNormalizeScript = "Floater.minimizeNormalize('"
                + minimizeLink.getMarkupId() + "', "
                + getPadBottom() + ", "
                + minimized + ");";
        target.appendJavascript( minimizeNormalizeScript );

    }

    private void addContent() {
        content = new WebMarkupContainer( "content" );
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
        resizer.add( new AttributeModifier( "onMouseDown", true, new Model<String>( resizeScript ) ) );
        add( resizer );
    }

    protected WebMarkupContainer getContentContainer() {
        return content;
    }

    /**
     * Add title to floating panel.
     */
    protected void addTitle() {
        titleLabel = new Label( "title", new Model<String>( getAdjustedTitle() ) );
        titleLabel.setOutputMarkupId( true );
        moveBar.addOrReplace( titleLabel );
    }

    private String getAdjustedTitle() {
        String title = getTitle();
        return minimized
                ? StringUtils.abbreviate( title, MINIMIZED_TITLE_SIZE )
                : title;
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
        addTitle();
        target.addComponent( titleLabel );
    }


    /**
     * Get top padding in px.
     *
     * @return an int
     */
    abstract protected int getPadTop();

    /**
     * Get left padding in px.
     *
     * @return an int
     */
    abstract protected int getPadLeft();

    /**
     * Get bottom padding in px.
     *
     * @return an int
     */
    abstract protected int getPadBottom();

    /**
     * Get right padding in px.
     *
     * @return an int
     */
    abstract protected int getPadRight();


    /**
     * Get min width  in px on resize.
     *
     * @return an int
     */
    abstract protected int getMinWidth();

    /**
     * Get min width  in px on resize.
     *
     * @return an int
     */
    abstract protected int getMinHeight();

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
        add( new AttributeModifier( "style", true, new Model<String>( style ) ) );
        add( new HeaderContributor( new IHeaderContributor() {
            public void renderHead( IHeaderResponse response ) {
                String script = "Floater.onOpen('" + getMarkupId() + "');";
                response.renderOnDomReadyJavascript( script );
            }
        } ) );
    }

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

}
