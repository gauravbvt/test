package com.mindalliance.channels.view.common
{
	import com.mindalliance.channels.model.IChannelsModel;
	public interface IChannelsModelComponent
	{
		function set channelsModel(model : IChannelsModel) : void;
		function get channelsModel() : IChannelsModel;
	}
}