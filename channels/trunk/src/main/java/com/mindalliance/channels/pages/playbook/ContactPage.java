package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.procedures.VCardPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * List of contacts for a given flow.
 */
public class ContactPage extends AbstractChannelsWebPage {

    /** HTTP return code for unknown object. */
    private static final int NOT_FOUND = 404;

    public ContactPage( PageParameters parameters ) {
        super( parameters );

        Flow flow = getParm( "0", Flow.class );
        if ( flow == null )
            throw new AbortWithHttpErrorCodeException( NOT_FOUND, "Not found" );

        init( flow, getQueryService().findAllContacts( contactsSpec( flow ), true ) );
    }

    private static ResourceSpec contactsSpec( Flow flow ) {
        Node node = flow.isAskedFor() ? flow.getSource() : flow.getTarget();
        Part contactedPart = node.isPart() ? (Part) node : null;
        return contactedPart.resourceSpec();
    }

    private void init( Flow flow, List<ResourceSpec> contacts ) {
        String label = flow.getLabel();

        add( new Label( "title", label ),
             new Label( "header", label ),
             new Label( "description", flow.getDescription() ),

             new ListView<ResourceSpec>( "contact", contacts ) {
                @Override
                protected void populateItem( ListItem<ResourceSpec> item ) {
                    ResourceSpec spec = item.getModelObject();

                    item.add(
                            new VCardPanel( "channel", spec, "../../" )
                    );
                }
            } );

    }

    private <T extends ModelObject> T getParm( String parm, Class<T> parmClass ) {
        T result = null;

        PageParameters parms = getPageParameters();
        if ( parms.getNamedKeys().contains( parm ) )
            try {
                result = getQueryService().find( parmClass, Long.valueOf( parms.get( parm ).toString() ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( ClassCastException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }

}
