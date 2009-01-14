package com.mindalliance.channels.pages;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
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
                    final String result;
                    if ( obj instanceof Role )
                        result = linkFor( (Role) obj );
                    else if ( obj instanceof Part )
                        result = linkFor( (Part) obj );
                    else if ( obj instanceof Actor )
                        result = linkFor( (Actor) obj );
                    else if ( obj instanceof Organization )
                        result = linkFor( (Organization) obj );
                    else {
                        result = "#";
                        if ( obj != null )
                            LoggerFactory.getLogger( ModelObjectLink.class ).warn(
                                    MessageFormat.format( "Links to {0} are not implemented",
                                                          obj.getClass() ) );
                    }
                    return result;
                }
            },
            s );
    }

    private static String linkFor( Role role ) {
        return MessageFormat.format( "role.html?id={0}", role.getId() );                  // NON-NLS
    }

    private static String linkFor( Organization organization ) {
        return MessageFormat.format( "organization.html?id={0}", organization.getId() );                  // NON-NLS
    }

    private static String linkFor( Actor actor ) {
        return MessageFormat.format( "actor.html?id={0}", actor.getId() );                  // NON-NLS
    }

    private static String linkFor( Part part ) {
        return MessageFormat.format( "node.html?scenario={0}&amp;node={1}",               // NON-NLS
                                     part.getScenario().getId(),
                                     part.getId()
                                     );
    }
}
