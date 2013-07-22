#!/bin/bash

# Fixup script after running roo command:
# database reverse engineer --schema njsp --package ~.model --includeNonPortableAttributes true

find src/main/java/com/mindalliance/sb/model/ -name \*DbManaged.aj | xargs sed -i -e 's/, columnDefinition = "VARCHAR"//'
find src/main/java/com/mindalliance/sb/model/ -name \*Roo_Identifier.aj | xargs sed -i -e 's/, columnDefinition = "VARCHAR"//'
find src/main/java/com/mindalliance/sb/model/ -name \*PK.java | xargs sed -i -e '/RooEquals/d;/RooSerializable/d'
find src/main/java/com/mindalliance/sb/model/ -name \*PK.java | xargs sed -i -e '/import/iimport org.springframework.roo.addon.equals.RooEquals;\
import org.springframework.roo.addon.serializable.RooSerializable;
/public/i@RooEquals\
@RooSerializable'
sed -i -e '/@GeneratedValue/d' src/main/java/com/mindalliance/sb/model/Respondent_Roo_Jpa_Entity.aj
