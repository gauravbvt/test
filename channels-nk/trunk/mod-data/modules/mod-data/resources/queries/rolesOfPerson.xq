(: All roles of a given person. 							:)

(: Variables:															  :)
(: 			personId -- the id of a person	:)

<list>
	{
	 for
		$p in collection('__MODEL__')/person,
		$r in collection('__MODEL__')/role
	 where 
	 	$p/id = $personId and
	 	$r/id = $p/roles/roleId
	 return
	 		<role>
		 		<id>{$r/id/text()}</id>
		 		<name>{$r/name/text()}</name>
			</role>
	}
</list>