package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.entities.EntityLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Filterable model object link.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 1, 2009
 * Time: 8:11:05 PM
 */
public class FilterableModelObjectLink extends AbstractUpdatablePanel {
    /**
     * A filterable.
     */
    private Filterable filterable;
    /**
     * Model of filterable, linked model object.
     */
    private IModel<? extends ModelObject> moModel;
    /**
     * Model of link text.
     */
    private IModel<String> textModel;
    /**
     * Hint to put as link title.
     */
    private String hint;
    /**
     * Filter/unfilter image.
     */
    private WebMarkupContainer image;
    /**
     * Link to (de)activate filtering.
     */
    private AjaxFallbackLink filterLink;
    /**
     * Filter property.
     */
    private String filterProperty;

    public FilterableModelObjectLink(
            String id,
            IModel<? extends ModelObject> moModel,
            IModel<String> textModel,
            String hint,
            Filterable filterable ) {
        this( id, moModel, textModel, hint, null, filterable );
    }

    public FilterableModelObjectLink(
            String id,
            IModel<? extends ModelObject> moModel,
            IModel<String> textModel,
            String hint,
            String filterProperty,
            Filterable filterable ) {
        super( id, moModel );
        this.filterable = filterable;
        this.moModel = moModel;
        this.textModel = textModel;
        this.hint = hint;
        this.filterProperty = filterProperty;
        init();
    }

    private void init() {
        addModelObjectLink();
        addFilterLink();
        addImage();
    }

    private void addModelObjectLink() {
        ModelObject mo = getLinkedObject();
        if ( mo.isEntity() ) {
            if ( mo.isUnknown() ) {
                add( new Label( "moLink", new Model<String>( mo.getName() )) );
            } else {
                add( new EntityLink(
                        "moLink",
                        new Model<ModelEntity>( (ModelEntity)mo ),
                        textModel.getObject() ) );
            }
        } else {
            add( new ModelObjectLink( "moLink", moModel, textModel, hint ) );
        }

    }

    private void addFilterLink() {
        filterLink = new AjaxFallbackLink( "filterLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                filterable.toggleFilter( getLinkedObject(), filterProperty, target );
                addImage();
                target.add( image );
            }
        };
        add( filterLink );
    }

    private ModelObject getLinkedObject() {
        return moModel.getObject();
    }

    private void addImage() {
        image = new WebMarkupContainer( "lockImage" );
        image.add( new AttributeModifier( "src", new PropertyModel<String>( this, "imageSource" ) ) );
        addTipTitle( image, new PropertyModel<String>( this, "imageTitle" ) );
        image.setOutputMarkupId( true );
        filterLink.addOrReplace( image );
    }

    /**
     * Get image source attribute value.
     *
     * @return a String
     */
    public String getImageSource() {
        return ( filterable.isFiltered( getModel().getObject(), filterProperty ) )
                ? "images/lock.png"
                : "images/lock_open.png";
    }

    /**
     * Get image title attribute value.
     *
     * @return a String
     */
    public String getImageTitle() {
        return ( filterable.isFiltered( getModel().getObject(), filterProperty ) )
                ? "release filter"
                : "filter on this";
    }
}
