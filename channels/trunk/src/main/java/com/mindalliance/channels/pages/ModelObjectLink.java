package com.mindalliance.channels.pages;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Role;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * A link to an aggregate object.
 */
public class ModelObjectLink extends ExternalLink {

    public ModelObjectLink( String id, IModel<? extends ModelObject> mo ) {
        this( id, mo, null );
    }

    public ModelObjectLink(
            String id, final IModel<? extends ModelObject> mo, IModel<String> s ) {

        super(
            id,
            new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    final ModelObject obj = mo.getObject();
                    if ( obj instanceof Role )
                        return linkFor( (Role) obj );

                    if ( obj != null )
                        LoggerFactory.getLogger( ModelObjectLink.class ).warn(
                                MessageFormat.format( "Links to {0} are not implemented",
                                                      obj.getClass() ) );
                    return "#";
                }
            },
            s );
    }

    private static String linkFor( Role role ) {
        return MessageFormat.format( "role.html?id={0}", role.getId() );                  // NON-NLS
    }
}
