// Copyright (c) 2012. All Rights Reserved.
// CONFIDENTIAL

package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.model.Medium;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * A list of media.
 */
public class MediaList extends Panel {

    private static final long serialVersionUID = -3161931084000631783L;

    private final Component radioGroup;

    public MediaList( String id, IModel<Medium> mediumModel, IModel<List<Medium>> listModel ) {
        super( id );
        setOutputMarkupId( true );

        radioGroup = new RadioGroup<Medium>( "radioGroup", mediumModel ).add(

            new ListView<Medium>( "media", listModel ) {
                @Override
                protected void populateItem( ListItem<Medium> item ) {
                    IModel<Medium> itemModel = item.getModel();
                    String mediumId = "medium" + item.getId();

                    item.add(
                        new WebMarkupContainer( "label" ).add(
                            new Label( "type", new PropertyModel<String>( itemModel, "type" ) ),
                            new Label(
                                "address",
                                new PropertyModel<String>( itemModel, "address" ) ) ).add(
                            new AttributeModifier(
                                "for",
                                new Model<String>( mediumId ) ) ),

                        new Radio<Medium>( "value", itemModel ).add(
                            new AttributeModifier( "id", new Model<String>( mediumId ) ) ) ).setRenderBodyOnly( true );
                }
            } );

        add( new WebMarkupContainer( "mediaDiv" )
                .add( radioGroup )
                .setOutputMarkupId( true ) );
    }
    
    public MediaList showList( boolean visible ) {
        radioGroup.setVisible( visible );
        return this;
    }
}
