package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.pages.reports.VCardPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * List of contacts for a given flow.
 */
public class ContactPage extends WebPage {

    /** HTTP return code for unknown object. */
    private static final int NOT_FOUND = 404;

    /** The query service. */
    @SpringBean
    private QueryService queryService;

    public ContactPage( PageParameters parameters ) {
        super( parameters );

        Flow flow = getParm( "0", Flow.class );
        if ( flow == null )
            throw new AbortWithHttpStatusException( NOT_FOUND, false );

        init( flow, queryService.findAllContacts( contactsSpec( flow ), true ) );
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
        if ( parms.containsKey( parm ) )
            try {
                result = queryService.find( parmClass, Long.valueOf( parms.getString( parm ) ) );

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
