package com.mindalliance.channels.categories.business
{
	import com.mindalliance.channels.common.business.BaseElementAdapter;
	import com.mindalliance.channels.util.XMLHelper;
	import com.mindalliance.channels.vo.CategoryVO;
	import com.mindalliance.channels.vo.common.ElementVO;

	public class CategoryAdapter extends BaseElementAdapter
	{
		public function CategoryAdapter() {
		  super("category", CategoryVO);	
		}
		
		/**
         * parses /channels/schema/category.rng
         */
        override public function fromXML(xml:XML):ElementVO {
            return new CategoryVO(xml.id, 
                                     xml.name, 
                                     xml.description,
                                     xml.@taxonomy,
                                     XMLHelper.xmlToIdList("categoryId", xml.disciplines),
                                     XMLHelper.xmlToIdList("categoryId", xml.implies),
                                     XMLHelper.xmlToInformation(xml.information[0]));
        }
        
        /**
         * generates /channels/schema/category.rng
         */
        override public function toXML(element:ElementVO) : XML {
            var obj : CategoryVO = (element as CategoryVO);
            var xml : XML = <category schema="/channels/schema/category.rng">
                        <id>{obj.id}</id>
                        <name>{obj.name}</name>
                        <description>{obj.description}</description>
                    </category>;
            xml.appendChild(XMLHelper.idListToXML("disciplines","categoryId",obj.disciplines));
            xml.appendChild(XMLHelper.idListToXML("implies","categoryId",obj.implies));
            xml.appendChild(XMLHelper.informationToXML(obj.information));
            return xml;
        }

                
        override public function updateElement(element : ElementVO, parameters : Object) : void {
            var data : CategoryVO = element as CategoryVO;
            data.name = parameters["name"];
            data.description = parameters["description"];
            data.taxonomy = parameters["taxonomy"]
            data.disciplines = parameters["disciplines"];
            data.implies= parameters["implies"];
            data.information = parameters["information"];
            
        }          
	}
}