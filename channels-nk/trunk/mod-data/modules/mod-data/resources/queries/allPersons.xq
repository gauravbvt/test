(: All persons in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/person
		 order by $e/lastName , $e/firstName
		 return
		 	<person>
		 		<id>{$e/id/text()}</id>
		 		<firstName>{$e/firstName/text()}</firstName>
		 		<lastName>{$e/lastName/text()}</lastName>
		 	</person>
	}
</list>