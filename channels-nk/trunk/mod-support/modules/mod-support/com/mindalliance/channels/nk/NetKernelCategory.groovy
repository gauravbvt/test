package com.mindalliance.channels.nk

import com.ten60.netkernel.urii.IURAspect
import com.ten60.netkernel.urii.IURRepresentation
import com.ten60.netkernel.urii.aspect.IAspectString
import com.ten60.netkernel.urii.aspect.StringAspect
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import org.ten60.netkernel.layer1.representation.IAspectNVP
import org.ten60.netkernel.xml.representation.DOMXDAAspect
import org.ten60.netkernel.xml.representation.IAspectXDA
import org.ten60.netkernel.xml.xda.DOMXDA
import org.ten60.netkernel.xml.xda.IXDA
import org.ten60.netkernel.xml.xda.IXDAReadOnly
import org.ten60.netkernel.layer1.nkf.INKFResponse

/**
 * 
 */
public class NetKernelCategory {

    public static final String LOG_URL = "ffcpl:/etc/LogConfig.xml";

    public static final int NEW = INKFRequestReadOnly.RQT_NEW;
    public static final int SOURCE = INKFRequestReadOnly.RQT_SOURCE;
    public static final int SINK = INKFRequestReadOnly.RQT_SINK;
    public static final int DELETE = INKFRequestReadOnly.RQT_DELETE;
    public static final int EXISTS = INKFRequestReadOnly.RQT_EXISTS;

    public static final String URI_SYSTEM = INKFRequestReadOnly.URI_SYSTEM;

    public static Object get(IAspectNVP aspect, String name) {
        switch(name) {
            case 'map' : return map(aspect);
            default : return aspect.getValue(name);
        }
    }

    public static Object get(INKFConvenienceHelper context, String name) {
        switch (name) {
            case 'params': return params(context);
            case 'args': return args(context);
            case 'request': return context.thisRequest();
            case 'xdaHelper': return new XDAHelper(context);
            case 'session': return getSession(context);
            case ~/(.*)\?/: return context.thisRequest.argumentExists(name.substring(0, name.length() - 1));
            default: return getArgumentIfExists(context.thisRequest, name);
        }
    }

    public static Object get(INKFRequestReadOnly self, String name) {
        switch (name) {
            case 'type': return self.getRequestType();
            case 'activeType': return self.getActiveType();
            case ~/(.*)\?/: return self.argumentExists(name.substring(0, name.length() - 1));
            default: return self.getArgument(name);
        }

    }

    public static Set names(IAspectNVP aspect) {
        return aspect.getNames();
    }
    public static List list(IAspectNVP aspect, String name) {
        return aspect.getValues(name);
    }


    public static Map map(IAspectNVP aspect) {
        Map map = new HashMap();
        Set names = aspect.names
        for (name in names) {
            map.put(name, aspect.getValue(name))
        }
        return map;
    }
    public static IAspectNVP params(INKFConvenienceHelper context) {
        return params(context, "this:param:param");
    }

    public static IAspectNVP params(INKFConvenienceHelper context, String uri) {
        return (IAspectNVP) context.sourceAspect(uri, IAspectNVP.class);
    }

    public static String getArgumentIfExists(INKFRequestReadOnly request, String name) {
        if (request.argumentExists(name))
            return request.getArgument(name);
        return null;
    }



    public static Iterator args(INKFConvenienceHelper context) {
        return context.getThisRequest().getArguments();
    }


    public String getProperty(INKFConvenienceHelper context, String name, String uri) throws IOException, Exception {
        String content = ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString();
        Properties props = new Properties();
        props.load(new ByteArrayInputStream(content.getBytes()));
        String value = props.getProperty(name);
        return value;
    }

    public static StringAspect string(Object obj, String value) {
        return new StringAspect(value);
    }

    public static String data(Object obj, Object value) throws Exception {
        return "data:text/plain," + URLEncoder.encode(value.toString(), "UTF-8");
    }
    public static IAspectXDA xda(Object obj, IXDAReadOnly document) {
        return (IAspectXDA) new DOMXDAAspect((DOMXDA) document);
    }

    public static IURRepresentation subrequest(INKFConvenienceHelper context, String uri, Map args) {

        def reqType = (args['type'] == null) ? "source" : args['type'];
        def req = context.createSubRequest(uri);
        req.setRequestType(INKFRequestReadOnly."RQT_${reqType.toUpperCase()}")

        def mimeType;
        def expired;

        args.each {key, value ->
            switch (key) {
                case 'type': break;
                case 'mimeType': mimeType = value; break;
                case 'expired': expired = value; break;

                case 'SYSTEM': req.addSystemArgument(value); break;
                default: req.addArgument(key, value)
            }
        }
        def rep = context.issueSubRequest(req);
        def response;

        if (mimeType) {
            response = context.createResponseFrom(rep)

            response.setMimeType mimeType
        }

        if (expired) {
            if (!response) {
                response = context.createResponseFrom(rep)

                response.setExpired()
            }
        }
        if (response != null) {
            context.setResponse(response)
        }
        return rep;
    }
    public static IURAspect transrept(INKFConvenienceHelper context, String uri, Class aspectClass, Map args) {

        return context.transrept(subrequest(context, uri, args), aspectClass);
    }


    public static INKFResponse respond(INKFConvenienceHelper context, IURRepresentation res, String mimeType, boolean expired) {
        //def rep = context.issueSubRequest(context.thisRequest)
        def resp = context.createResponseFrom(res);
        if (mimeType != null) resp.setMimeType(mimeType);
        if (expired) resp.setExpired();
        context.setResponse(resp);
        return resp;
    }
    public static INKFResponse respond(INKFConvenienceHelper context, IURAspect res, String mimeType, boolean expired) {
        //def rep = context.issueSubRequest(context.thisRequest)
        def resp = context.createResponseFrom(res);
        if (mimeType != null) resp.setMimeType(mimeType);
        if (expired) resp.setExpired();
        context.setResponse(resp);
        return resp;
    }
    public static INKFResponse respond(INKFConvenienceHelper context, String uri, String mimeType) {
        return subrequest(context, uri, ["mimeType": mimeType]);
    }

    public static Session getSession(INKFConvenienceHelper context) {
        return new Session(new ContextSupport(context));
    }



    public static String sourceString(INKFConvenienceHelper context, String uri) {
        return ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString();
    }

    public static IXDA sourceXDA(INKFConvenienceHelper context, String uri) {
        return sourceXDAAspect(context, uri).getClonedXDA();
    }

    public static IAspectXDA sourceXDAAspect(INKFConvenienceHelper context, String uri) {
        return (IAspectXDA) context.sourceAspect(uri, IAspectXDA.class);
    }

    public static IXDAReadOnly sourceXDAReadOnly(INKFConvenienceHelper context, String uri) {
        String xml = ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString(); // get string aspect first otherwise db.* also gathers whitespace children
        IXDAReadOnly db = new XDAHelper(context).makeXDA(xml);
        return db;
    }
}