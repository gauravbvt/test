package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.Identifiable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Filtereable label used in tables.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/12/12
 * Time: 9:39 PM
 */
public class FilterableLabel  extends AbstractUpdatablePanel {
    /**
     * A filterable.
     */
    private Filterable filterable;
    /**
     * Model of filterable, linked model object.
     */
    private IModel<? extends Identifiable> identifiableModel;
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
    private AjaxLink filterLink;
    /**
     * Filter property.
     */
    private String filterProperty;

    public FilterableLabel(
            String id,
            IModel<? extends Identifiable> identifiableModel,
            IModel<String> textModel,
            String hint,
            Filterable filterable ) {
        this( id, identifiableModel, textModel, hint, null, filterable );
    }

    public FilterableLabel(
            String id,
            IModel<? extends Identifiable> identifiableModel,
            IModel<String> textModel,
            String hint,
            String filterProperty,
            Filterable filterable ) {
        super( id, identifiableModel );
        this.filterable = filterable;
        this.identifiableModel = identifiableModel;
        this.textModel = textModel;
        this.hint = hint;
        this.filterProperty = filterProperty;
        init();
    }

    private void init() {
        addLabel();
        addFilterLink();
        addImage();
    }

    private void addLabel() {
        Identifiable identifiable = getIdentifiableObject();
        Label label = new Label( "identifiable", new Model<String>( identifiable.getName() ) );
        addTipTitle( label, new Model<String>( hint ) );
        add( label );
    }

    private void addFilterLink() {
        filterLink = new AjaxLink( "filterLink" ) {
            public void onClick( AjaxRequestTarget target ) {
                filterable.toggleFilter( getIdentifiableObject(), filterProperty, target );
                addImage();
                target.add( image );
            }
        };
        add( filterLink );
    }

    private Identifiable getIdentifiableObject() {
        return identifiableModel.getObject();
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
