(: All sharingNeeds in a scenario. 							:)
(: Variables:															  :)
(: 			scenarioId -- the id of a scenario	:)

<list>
	{
		let $k := collection('__MODEL__')/know
		let $e := collection('__MODEL__')/event
		let $t := collection('__MODEL__')/task
		let $ac := collection('__MODEL__')/acquirement
		let $ar := collection('__MODEL__')/artifact
		for 
		 	$sn in collection('__MODEL__')/sharingNeed
		where 
		 	$k/id = $sn/knowId and
		 	(
			 	( $k/about/eventId = $e/id and $e/scenarioId = $scenarioId ) or
			 	( $k/about/taskId = $t/id and $t/scenarioId = $scenarioId ) or
			 	( $k/about/acquirementId = $ac/id and $ac/product/taskId = $t/id and $t/scenarioId = $scenarioId ) or
			 	( $k/about/artifactId = $ar/id and $ar/product/taskId = $t/id and $t/scenarioId = $scenarioId )
			)
		return
		 	<sharingNeed>
		 		<id>{$sn/id/text()}</id>
				<knowId>{$sn/knowId/text()}</knowId>
				<needToKnowId>{$sn/needToKnowId/text()}</needToKnowId>
		 	</sharingNeed>
	}
</list>