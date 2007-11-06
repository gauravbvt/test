package com.mindalliance.channels.common.view
{
	import com.mindalliance.channels.model.EditorModel;
	
	public interface IEditor
	{
		function get model() : EditorModel;
		function set model(model : EditorModel) : void;
	}
}