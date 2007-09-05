(: Phases in a scenario 							:)
(: Variables:			scenarioId -- the id of a scenario	:)

<list>
	{
		 for
		 		$p in collection('__MODEL__')/phase,
		 		$e in  collection('__MODEL__')/event
		 where 
		 	$p/eventId = $e/id and
		 	$e/scenarioId = $scenarioId
		 order by $p/name
		 return
		 	<phase>
		 		<id>{$p/id/text()}</id>
		 		<name>{$p/name/text()}</name>
		 		<event>
		 			<id>{$e/id/text()}</id>
		 			<name>{$e/name/text()}</name>
		 		</event>
		 	</phase>
	}
</list>