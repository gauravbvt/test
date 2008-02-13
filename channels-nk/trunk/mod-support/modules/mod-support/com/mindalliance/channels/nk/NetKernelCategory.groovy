package com.mindalliance.channels.nk

import com.ten60.netkernel.urii.IURAspect
import com.ten60.netkernel.urii.IURRepresentation
import com.ten60.netkernel.urii.aspect.BooleanAspect
import com.ten60.netkernel.urii.aspect.IAspectBoolean
import com.ten60.netkernel.urii.aspect.IAspectString
import com.ten60.netkernel.urii.aspect.StringAspect
import groovy.util.slurpersupport.GPathResult
import groovy.xml.DOMBuilder
import groovy.xml.MarkupBuilder
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import org.ten60.netkernel.layer1.nkf.INKFRequest
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import org.ten60.netkernel.layer1.nkf.INKFResponse
import org.ten60.netkernel.layer1.representation.IAspectNVP
import org.ten60.netkernel.xml.representation.DOMXDAAspect
import org.ten60.netkernel.xml.representation.IAspectXDA
import org.ten60.netkernel.xml.representation.IXAspect
import org.ten60.netkernel.xml.xda.DOMXDA
import org.ten60.netkernel.xml.xda.IXDA
import org.ten60.netkernel.xml.xda.IXDAReadOnly
import org.ten60.netkernel.layer1.representation.NVPAspect
import org.ten60.netkernel.layer1.representation.NVPImpl


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

    // CONTEXT

    public static IURRepresentation subrequest(INKFConvenienceHelper context, String uri, Map args) {

        def reqType = (args['type'] == null) ? "source" : args['type'];
        def req = context.createSubRequest(uri);
        req.setRequestType(INKFRequestReadOnly."RQT_${reqType.toUpperCase()}")

        def mimeType;
        def expired;
        def goldenThread;

        args.each {key, value ->
            switch (key) {
                case 'type': break;
                case 'mimeType': mimeType = value; break;
                case 'expired': expired = value; break;
                case 'goldenThread': goldenThread = value; break;
                case 'SYSTEM': req.addSystemArgument(value); break;
                default: req.addArgument(key, value)
            }
        }
        def rep = context.issueSubRequest(req);
        def response;

        if (goldenThread) {
            rep = subrequest(context, "active:attachGoldenThread", [operand: rep, param: uri])
        }

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

    public static boolean isTrue(INKFConvenienceHelper context, IURRepresentation rep) {
        return ((IAspectBoolean) context.transrept(rep, IAspectBoolean.class)).isTrue();
    }

    public static boolean isTrue(INKFConvenienceHelper context, String uri, Map args) {
        isTrue(context, subrequest(context, uri, args))
    }

    public static boolean isTrue(INKFConvenienceHelper context, String uri) {
        return ((IAspectBoolean) context.sourceAspect(uri, IAspectBoolean.class)).isTrue()
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
    public static IURRepresentation respond(INKFConvenienceHelper context, String uri, String mimeType) {
        return subrequest(context, uri, ["mimeType": mimeType]);
    }

    public static INKFResponse respond(INKFConvenienceHelper context, IURAspect res) {
        respond(context, res, "text/xml", true)
    }

    public static SessionHelper getSession(INKFConvenienceHelper context) {
        return new SessionHelper(context);
    }

    public static IURAspect sourceAspect(INKFConvenienceHelper context, String uri, Map args, Class aspectClass) {
        IURRepresentation rep = subrequest(context, uri, args)
        return context.transrept(rep, aspectClass)
    }

    public static IURAspect sourceAspect(INKFConvenienceHelper context, String uri) {
        return context.sourceAspect(uri, IURAspect.class)
    }

    public static IURAspect sourceAspect(INKFConvenienceHelper context, String uri, Class aspectClass) {
        IURRepresentation rep = subrequest(context, uri, [:])
        return context.transrept(rep, aspectClass)
    }

    public static String sourceString(INKFConvenienceHelper context, String uri) {
        return ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString();
    }

    public static String sourceString(INKFConvenienceHelper context, String uri, Map args) {
        def rep = subrequest(context, uri, args)
        String str = ((IAspectString) context.transrept(rep, IAspectString.class)).getString()
        return str
    }

    public static Map sourceNVP(INKFConvenienceHelper context, String uri) {
        IAspectNVP nvp = (IAspectNVP) context.sourceAspect(uri, IAspectNVP.class)
        Map map = nvp.map
        return map
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

    public static GPathResult sourceXML(INKFConvenienceHelper context, String uri) {
        String text = ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString();
        return new XmlSlurper().parseText(text);
    }

    public static GPathResult sourceXML(INKFConvenienceHelper context, String uri, Map args) {
        String text = ((IAspectString) sourceAspect(context, uri, args, IAspectString.class)).getString();
        return new XmlSlurper().parseText(text);
    }

    public static Node sourceDOM(INKFConvenienceHelper context, String uri) {
        String text = ((IAspectString) context.sourceAspect(uri, IAspectString.class)).getString();
        return new XmlParser().parseText(text);
    }

    public static Node sourceDOM(INKFConvenienceHelper context, String uri, Map args) {
        String text = ((IAspectString) sourceAspect(context, uri, args, IAspectString.class)).getString();
        return new XmlParser().parseText(text);
    }

    public static GPathResult getXml(INKFConvenienceHelper context, IURRepresentation representation) {
        return getXml(context, (IURAspect) representation.getAspects()[0]) // TODO - Filter  - Now: Assumes first aspect in representation is only relevant one
    }

    public static GPathResult getXml(INKFConvenienceHelper context, IURAspect aspect) {
        return new XmlSlurper().parseText(getString(context, aspect));
    }

    public static GPathResult getXml(INKFConvenienceHelper context, String uri, Map args) {
        return getXml(context, subrequest(context, uri, args))
    }

    public static String getString(INKFConvenienceHelper context, IURAspect aspect) {
        IAspectString stringAspect = (IAspectString) context.transrept(aspect, IAspectString.class)
        return stringAspect.getString()
    }

    public static String getString(INKFConvenienceHelper context, IURRepresentation representation) {
        return getString(context, (IURAspect) representation.getAspects()[0]) // TODO - Filter  - Now: Assumes first aspect in representation is only relevant one
    }

    public static Object get(INKFConvenienceHelper context, String name) {
        switch (name) {
            case 'params': return params(context);
            case 'args': return args(context);
            case 'request': return context.getThisRequest();
            case 'xdaHelper': return new XDAHelper(context);
            case 'session': return getSession(context);
            case ~/(.*)\?/: return context.thisRequest.argumentExists(name.substring(0, name.length() - 1));
            default: return getArgumentIfExists(context.thisRequest, name);
        }
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

    public static IAspectNVP params(INKFConvenienceHelper context) {
        return params(context, "this:param:param");
    }

    public static IAspectNVP params(INKFConvenienceHelper context, String uri) {
        return (IAspectNVP) context.sourceAspect(uri, IAspectNVP.class);
    }

    public static String getCookie(INKFConvenienceHelper context, String cookieName) {
        String operator = "<cookie><get>" + cookieName + "</get></cookie>";
        IAspectString ias = new StringAspect(operator);
        INKFRequest req = context.createSubRequest("active:HTTPCookie");
        req.addArgument("operand", "this:param:cookie");
        req.addArgument("operator", ias);
        IURRepresentation rep = context.issueSubRequest(req);
        IAspectXDA xda = (IAspectXDA) context.transrept(rep, IXAspect.class);
        String xml = ((IAspectString) context.transrept(xda, IAspectString.class)).getString();
        return xml;
    }
    public static log(INKFConvenienceHelper context, String message, String level) {
        subrequest(context, "active:application-log",
                ["operand": string(context, message),
                        "configuration": LOG_URL,
                        "operator": string(context, "<log>$level</log>")])
    }

    public static void cutGoldenThread(INKFConvenienceHelper context, String uri) {
        subrequest(context, "active:cutGoldenThread", [param: uri])
    }

    public static IAspectString string(INKFConvenienceHelper context, IURRepresentation rep) {
        return (IAspectString) context.transrept(rep, IAspectString.class)
    }

    public static void sleep(INKFConvenienceHelper context, int msecs) {
        subrequest(context, "active:sleep", [operator: string(context, "<time>$msecs</time>")])
    }

    public static String makeGUID(INKFConvenienceHelper context) {
        GPathResult guid = getXml(context, "active:guid", [:])
        return guid.text()
    }

    // REQUEST

    public static Object get(INKFRequestReadOnly self, String name) {
        switch (name) {
            case 'type': return self.getRequestType();
            case 'activeType': return self.getActiveType();
            case ~/(.*)\?/: return self.argumentExists(name.substring(0, name.length() - 1));
            default: return self.getArgument(name);
        }

    }

    public static String getArgumentIfExists(INKFRequestReadOnly request, String name) {
        if (request.argumentExists(name))
            return request.getArgument(name);
        return null;
    }

    // NVP

    public static Object get(IAspectNVP aspect, String name) {
        switch (name) {
            case 'map': return map(aspect);
            case ~/(.*)\?/: return aspect.getValue(name.substring(0, name.length() - 1)) != null;
            default: return aspect.getValue(name);
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

    // Node

    public static String toXml(Node node) {
        StringWriter writer = new StringWriter()
        XmlNodePrinter xmlPrinter = new XmlNodePrinter(new PrintWriter(writer), '')
        xmlPrinter.print(node)
        return writer.toString()
    }

    // OBJECT

    public static StringAspect string(Object obj, String value) {
        return new StringAspect(value);
    }

    // For simple maps only (all values are literals)
    public static IAspectNVP map(Object obj, Map map) {
        NVPImpl nvpImpl = new NVPImpl()
        map.each {key, val ->
            nvpImpl.addNVP(key, "$val")
        }
        return new NVPAspect(nvpImpl)
    }

    public static String data(Object obj, Object value) throws Exception {
        return "data:text/plain," + URLEncoder.encode(value.toString(), "UTF-8");
    }

    public static IAspectXDA xda(Object obj, IXDAReadOnly document) {
        return (IAspectXDA) new DOMXDAAspect((DOMXDA) document);
    }

    public static IAspectBoolean bool(Object obj, boolean value) {
        return (IAspectBoolean) new BooleanAspect(value)
    }

    public static IAspectXDA buildXml(Object obj, Closure yield) {
        StringWriter writer = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(writer);
        yield(builder);
        return new DOMXDAAspect(new DOMXDA(DOMBuilder.parse(new StringReader(writer.toString()))));
    }

    public static IAspectXDA xmlAspect(Object object, Closure yield) {
        StringWriter writer = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(writer);
        yield(builder);
        return new DOMXDAAspect(new DOMXDA(DOMBuilder.parse(new StringReader(writer.toString()))));
    }

}