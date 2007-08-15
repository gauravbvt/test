(: VARIABLES 
    part : a string that matches part of a project's name
:)
<results>
   { collection('__CONTAINER__')/project[contains(name,$part)] }
</results>
