(: All events in a scenario. 							:)

(: Variables:															  :)
(: 			scenarioId -- the id of the scenario	:)

<list>
	{
		 for $e in collection('__MODEL__')/event
		 where $e/scenarioId = $scenarioId
		 order by $e/name
		 return
		 	<event>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</event>
	}
</list>