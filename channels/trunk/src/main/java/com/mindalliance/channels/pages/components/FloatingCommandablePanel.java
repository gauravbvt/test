package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.model.Identifiable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.text.MessageFormat;
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
    private static final String BG_COLOR = "#dde3eb";
    /**
     * Border width.
     */
    private static final int BORDER_WIDTH = 1;
    /**
     * Border color.
     */
    private static final String BORDER_COLOR = "#464f5a";
    /**
     * JavaScript.
     */
    private static ResourceReference JAVASCRIPT = new JavascriptResourceReference(
            FloatingCommandablePanel.class, "res/FloatingCommandablePanel.js" );

    public FloatingCommandablePanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        setOutputMarkupId( true );
        add( JavascriptPackageResource.getHeaderContribution( JAVASCRIPT ) );
        AjaxFallbackLink<?> closeLink = new AjaxFallbackLink( "close" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        add( closeLink );
        setLayout();
    }

    /**
     * Close panel.
     *
     * @param target an ajax request target.
     */
    abstract protected void close( AjaxRequestTarget target );

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
                String script = "Floater.restoreStyle('" + getMarkupId() + "');";
                response.renderOnDomReadyJavascript( script );
            }
        } ) );
    }

    protected int getTop() {
        return 120;
    }

    protected int getLeft() {
        return 35;
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
