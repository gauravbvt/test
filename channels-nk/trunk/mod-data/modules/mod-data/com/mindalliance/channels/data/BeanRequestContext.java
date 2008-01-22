package com.mindalliance.channels.data;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;

import java.util.Map;
import java.util.HashMap;

import com.mindalliance.channels.data.BeanMemory;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 8:51:57 AM
 */
public class BeanRequestContext {

    public static final String REQUEST_CONTEXT = "requestContext";
    public static final String BEAN_MEMORY = "beanMemory";

    private static class BeanThreadLocal extends ThreadLocal {

        public Object initialValue() {
            Map<String, Object> values = new HashMap<String,Object>();
            values.put(REQUEST_CONTEXT, null);
            values.put(BEAN_MEMORY, null);
            return values;
        }

        public Map getValues() {
            return (Map) super.get();
        }

    }

    private static BeanThreadLocal beanThreadLocal = new BeanThreadLocal();

    public static INKFConvenienceHelper getRequestContext() {
        Map values = (Map) beanThreadLocal.get();
        return (INKFConvenienceHelper) values.get(REQUEST_CONTEXT);
    }

    public static BeanMemory getBeanMemory() {
        Map values = (Map) beanThreadLocal.get();
        return (BeanMemory)values.get(BEAN_MEMORY);
    }

    public static void setRequestContext(INKFConvenienceHelper context) {
        Map<String,Object> values = (Map) beanThreadLocal.get();
        values.put(REQUEST_CONTEXT, context);
    }

       public static void setBeanMemory(BeanMemory beanMemory) {
        Map<String,Object> values = (Map) beanThreadLocal.get();
        values.put(BEAN_MEMORY, beanMemory);
    }
}
