(: All persons in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/person
		 order by $e/name
		 return
		 	<person>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</person>
	}
</list>