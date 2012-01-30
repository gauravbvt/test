package com.mindalliance.playbook.pages.panels;

import com.mindalliance.playbook.dao.ContactDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Collaboration;
import com.mindalliance.playbook.model.Contact;
import com.mindalliance.playbook.model.Medium;
import com.mindalliance.playbook.model.Step;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Details a send step.
 */
public class SendPanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger( SendPanel.class );

    private static final long serialVersionUID = 841767260474526472L;

    @SpringBean
    private ContactDao contactDao;
    
    @SpringBean
    private StepDao stepDao;

    public SendPanel( String id, final IModel<Step> model ) {
        super( id, model );
        setRenderBodyOnly( true );

        final Component radioGroup = new RadioGroup<Medium>( "radioGroup", 
                                                             new PropertyModel<Medium>( model, "using" ) ).add(
            
            new ListView<Medium>( "media", new PropertyModel<List<Medium>>( model, "with.media" ) ) {
                @Override
                protected void populateItem( ListItem<Medium> item ) {
                    IModel<Medium> itemModel = item.getModel();
                    String mediumId = "medium" + item.getId();

                    item.add(
                        new WebMarkupContainer( "label" ).add(
                            new Label( "type", new PropertyModel<String>( itemModel, "type" ) ), 
                            new Label( "address", new PropertyModel<String>( itemModel, "address" ) ) )
                            .add( new AttributeModifier( "for", new Model<String>( mediumId ) ) ),

                        new Radio<Medium>( "value", itemModel ).add(
                                new AttributeModifier( "id", new Model<String>( mediumId ) ) ) 
                            )
                            .setRenderBodyOnly( true );
                }
            } ).setVisible( ( (Collaboration) model.getObject() ).getWith() != null );

        final Component mediaDiv = new WebMarkupContainer( "mediaDiv" ).add( radioGroup ).setOutputMarkupId( true );

        add(
            new ContactField( "contact", new PropertyModel<Contact>( model, "with" ) ).add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        boolean visible = ( (Collaboration) model.getObject() ).getWith() != null;
                        LOG.debug( "onChange: {}", visible );
                        radioGroup.setVisible( visible );
                        target.add( mediaDiv );
                        if ( visible )
                            target.appendJavaScript( "$('#stepForm').trigger('create');" );
//                        target.appendJavaScript( "$('#" + mediaDiv.getMarkupId() + "').trigger('create');" );
//                            target.appendJavaScript( "$('#" + mediaDiv.getMarkupId() + "').listview('refresh');" );
                    }
                } ),

            mediaDiv

        );
    }
}
