package com.mindalliance.reportingui.accessor;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;

import com.ten60.netkernel.container.Container;
import com.ten60.netkernel.module.ModuleDefinition;
import com.ten60.netkernel.urii.aspect.StringAspect;
import com.ten60.netkernel.util.NetKernelException;

public class ReportingUiAccessor extends NKFAccessorImpl {

	public ReportingUiAccessor() {
		super(SAFE_FOR_CONCURRENT_USE, INKFRequestReadOnly.RQT_SOURCE);
	}

	public void initialise(Container container, ModuleDefinition moduledef) {
		try {
			super.initialise(container, moduledef);
			System.out.println("Performing initialization");
		} catch(NetKernelException nkfe) {
			nkfe.printStackTrace();
		}
	}

	public void processRequest(INKFConvenienceHelper context) throws Exception {
		String path=context.getThisRequest().getArgument("path");
		INKFResponse resp = null;

		String[] parts=path.split("/");
		for(int i = 0; i < parts.length; i++) {
			System.out.println(parts[i]);
		}

		if(parts[parts.length-1].equals("initialize")) {
			resp = context.createResponseFrom(new StringAspect("<initialized/>"));
			context.setResponse(resp);
			return;
		}

		switch(parts.length) {
		case 3:
			// Expecting ffcpl:/reporting-ui/<something>
			resp = context.createResponseFrom(new StringAspect("<hello>reporting-ui</hello>"));
			//resp = context.createResponseFrom(new ReportingUiAspect(new ReportingUi(3)));
			resp.setCacheable();
			resp.setMimeType("text/xml");
			break;
		default :
			resp = generateError(context, "Expecting path of ffcpl:/reporting-ui/something");
		}

		context.setResponse(resp);
	}

	private INKFResponse generateError(INKFConvenienceHelper context,String message) {
		INKFResponse retValue = context.createResponseFrom( new StringAspect("<error message=\"" + message + "\"/>"));
		retValue.setMimeType("text/xml");
		return retValue;
	}
}
