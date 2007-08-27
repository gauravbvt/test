(: All tasks in a scenario. 							:)

(: Variables:															  :)
(: 			scenarioId -- the id of the scenario	:)

<list>
	{
		 for $e in collection('__MODEL__')/task
		 where $e/scenarioId = $scenarioId
		 order by $e/name
		 return
		 	<task>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</task>
	}
</list>