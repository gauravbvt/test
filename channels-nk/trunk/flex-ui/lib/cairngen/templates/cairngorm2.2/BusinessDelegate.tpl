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
		
		override public function fromXML(obj:Object):ElementVO {
			return new @delegate@VO(obj.id, obj.name, obj.description);
		}
		
		override public function toXML(obj:ElementVO) : XML {
			return <@delegate@ schema="/channels/schema/project.rng">
						<id>{obj.id}</id>
						<name>{obj.name}</name>
						<description>{obj.description}</description>
					</@delegate@>;
		}
	}
}