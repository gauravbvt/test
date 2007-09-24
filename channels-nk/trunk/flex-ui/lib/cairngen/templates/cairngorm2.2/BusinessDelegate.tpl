// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package @namespace@.@business@.@submodule@
{
    import com.mindalliance.channels.business.BaseDelegate;
    
    import mx.rpc.IResponder;
    import com.mindalliance.channels.vo.ElementVO;
    import com.mindalliance.channels.vo.@delegate@VO;
    
    public class @delegate@Delegate extends BaseDelegate
    {   
        public function @delegate@Delegate(responder:IResponder)
        {
            super(responder);
        }
        /**
         * parses /channels/schema/@delegate@.rng
         */
        override public function fromXML(xml:XML):ElementVO {
            return new @delegate@VO(xml.id, xml.name, xml.description);
        }
        /**
         * generates /channels/schema/@delegate@.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : @delegate@VO = (element as @delegate@VO);
            var xml : XML = <@delegate@ schema="/channels/schema/@delegate@.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </@delegate@>;
            
            return xml;
        }
    }
}