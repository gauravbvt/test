/**
 * 
 */
package org.zkforge.timeline;

import org.zkforge.timeline.event.SelectEvent;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * @author dfeeney
 *
 */
public class SelectCommand extends Command {

	protected SelectCommand(String arg0, int arg1) {
		super(arg0, arg1);
	}
	/* (non-Javadoc)
	 * @see org.zkoss.zk.au.Command#process(org.zkoss.zk.au.AuRequest)
	 */
	@Override
	protected void process(AuRequest request) {
		Component comp = request.getComponent();
		if (comp == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		if (!(comp instanceof Timeline))
			throw new UiException(MZk.ILLEGAL_REQUEST_COMPONENT_REQUIRED, this);
		Timeline timeline = (Timeline)comp;
		String[] ids = request.getData();
		if (ids == null){
			ids = new String[0];
		}
		timeline.setLocalSelection(ids);
		Events.postEvent(new SelectEvent(getId(), comp,timeline.getSelectedData(), ids));
	}

	
}
