// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Medium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * A list of media.
 */
public class MediaList extends Panel {

    private static final long serialVersionUID = -3161931084000631783L;

    private final Component radioGroup;

    private String label;

    public MediaList( String id, final IModel<Medium> mediumModel, IModel<List<Medium>> listModel, String label ) {
        super( id );
        setOutputMarkupId( true );
        this.label = label;

        radioGroup = new RadioGroup<Medium>( "radioGroup", mediumModel ).add(

            new Label( "label", new PropertyModel<String>( this, "label" ) ),
            new ListView<Medium>( "media", listModel ) {
                @Override
                protected void populateItem( ListItem<Medium> item ) {
                    final IModel<Medium> itemModel = item.getModel();

                    Medium medium = itemModel.getObject();
                    Component radio = new Radio<Medium>( "value", itemModel ).add(
                        //new AttributeModifier( "id", new Model<String>( mediumId ) ),
                        new AjaxEventBehavior( "onchange" ) {
                            @Override
                            protected void onEvent( AjaxRequestTarget target ) {
                                updateTo( itemModel.getObject(), target );
                            }
                        } ).setOutputMarkupId( true );

                    String mediumId = radio.getMarkupId(); //"medium" + item.getId();
                    item.add(
                        new WebMarkupContainer( "label" ).add(
                            new Label( "address", medium.toString() )
                                    .add( new AttributeModifier( "class", medium.getCssClass() ) )
                            
                            ).add( new AttributeModifier( "for", mediumId ) ),

                        radio
                    
                    )
                        .setRenderBodyOnly( true );
                }
            } );
        
        add(
            new WebMarkupContainer( "mediaDiv" ).add( radioGroup ).setOutputMarkupId( true ) );
    }

    public void updateTo( Medium medium, AjaxRequestTarget target ) {
    }
    
    public MediaList showList( boolean visible ) {
        radioGroup.setVisible( visible );
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }
}
