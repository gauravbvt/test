package com.mindalliance.channels.sharingneed.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.common.business.IElementAdapter;
	import com.mindalliance.channels.model.ChannelsModelLocator;
	import com.mindalliance.channels.vo.SharingNeedVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class SharingNeedAdapter extends BaseElementAdapter implements IElementAdapter
	{
		public function SharingNeedAdapter()
		{
			super("sharingNeed", SharingNeedVO);
		}
		
		/**
         * parses /channels/schema/SharingNeed.rng
         */
        override public function fromXML(xml:XML):ElementVO {
            return new SharingNeedVO(xml.id,xml.knowId, xml.needToKnowId);
        }
        /**
         * generates /channels/schema/SharingNeed.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : SharingNeedVO = (element as SharingNeedVO);
            var xml : XML = <sharingNeed schema="/channels/schema/sharingNeed.rng">
                        <id>{obj.id}</id>
                        <knowId>{obj.knowId}</knowId>
                        <needToKnowId>{obj.needToKnowId}</needToKnowId>
                    </sharingNeed>;
            
            return xml;
        }
		
		override public function create(params:Object):XML
		{
			return <sharingNeed schema="/channels/schema/sharingNeed.rng">
                        <knowId>{params["knowId"]}</knowId>
                        <needToKnowId>{params["needToKnowId"]}</needToKnowId>
                    </sharingNeed>;
		}
		
		override public function fromXMLListElement(element:XML):ElementVO
		{
			return fromXML(element);
		}
		override public function postCreate(element : ElementVO, parameters : Object) : void {
            ChannelsModelLocator.getInstance().getElementListModel('sharingneeds').data.addItem(element);
        } 
        
        override public function updateElement(element : ElementVO, values : Object) : void {
            
        }   
	}
}