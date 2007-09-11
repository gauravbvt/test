package com.mindalliance.channels.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ChooserModel extends BaseChannelsModel
	{
		public function ChooserModel(elements : Object, elementLists : Object) {
          super(elements,elementLists);

        }
        
        public var selection : ArrayCollection = new ArrayCollection();
        public var editorModel : EditorModel = ChannelsModelLocator.getInstance().getEditorModel();
	}
}