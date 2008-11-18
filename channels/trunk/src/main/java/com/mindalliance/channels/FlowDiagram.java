package com.mindalliance.channels;

import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import org.apache.wicket.markup.html.link.ImageMap;

import java.io.OutputStream;

/**
 * Information flow diagram generator interface.
 */
public interface FlowDiagram {

    OutputStream getPNG( Scenario scenario, Part selected );

    ImageMap getImageMap( Scenario scenario, Part selected );

}
