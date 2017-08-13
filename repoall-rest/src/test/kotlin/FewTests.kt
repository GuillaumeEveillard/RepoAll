import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ggye.repoall.FieldDefinition
import com.ggye.repoall.FieldId
import com.ggye.repoall.ObjectDefinition
import com.ggye.repoall.ObjectHeader

/**
 * Created by ggye on 8/8/17.
 */

fun main(args: Array<String>) {

    val weaponDefinition = ObjectDefinition(
            ObjectHeader("WEAPON", 1),
            listOf(
                    FieldDefinition.StringField(FieldId(1, "Name")),
                    FieldDefinition.StringField(FieldId(1, "Description")),
                    FieldDefinition.IntegerField(FieldId(1, "Level")),
                    FieldDefinition.FloatField(FieldId(1, "DPS")),
                    FieldDefinition.EnumField(FieldId(1, "Quality"), listOf("Poor", "Common", "Uncommon", "Rare", "Epic"))))




    val mapper = jacksonObjectMapper()

    val json = mapper.writeValueAsString(weaponDefinition)

    System.out.println(json)

    val def: ObjectDefinition =  mapper.readValue(json)

    System.out.println(def)
}