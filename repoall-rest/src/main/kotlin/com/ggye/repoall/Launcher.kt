package com.ggye.repoall

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.ktor.netty.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.host.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.request.accept
import org.jetbrains.ktor.request.document
import org.jetbrains.ktor.response.*

data class FieldId(val id: Long, val label: String)

enum class FieldType {
    STRING, INTEGER, FLOAT, ENUM, GROUP
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes(
        JsonSubTypes.Type(FieldDefinition.StringField::class),
        JsonSubTypes.Type(FieldDefinition.IntegerField::class),
        JsonSubTypes.Type(FieldDefinition.FloatField::class),
        JsonSubTypes.Type(FieldDefinition.EnumField::class),
        JsonSubTypes.Type(FieldDefinition.Group::class)
)
sealed class FieldDefinition(val type: FieldType) {
    @JsonTypeName("STRING")
    data class StringField(val id: FieldId) : FieldDefinition(FieldType.STRING)
    @JsonTypeName("INTEGER")
    data class IntegerField(val id: FieldId) : FieldDefinition(FieldType.INTEGER)
    @JsonTypeName("FLOAT")
    data class FloatField(val id: FieldId) : FieldDefinition(FieldType.FLOAT)
    @JsonTypeName("ENUM")
    data class EnumField(val id: FieldId, val possibleChoices: List<String>) : FieldDefinition(FieldType.ENUM)
    @JsonTypeName("GROUP")
    data class Group(val title: String, val fields: List<FieldDefinition>) : FieldDefinition(FieldType.GROUP)
}

data class ObjectHeader(val label: String, val version: Long)

data class ObjectDefinition(val header: ObjectHeader, val fields: List<FieldDefinition>)



fun main(args: Array<String>) {
    val repo = MapBasedRepository()

    val weaponDefinition = ObjectDefinition(
            ObjectHeader("WEAPON", 1),
            listOf(
                    FieldDefinition.StringField(FieldId(1, "Name")),
                    FieldDefinition.StringField(FieldId(1, "Description")),
                    FieldDefinition.IntegerField(FieldId(1, "Level")),
                    FieldDefinition.FloatField(FieldId(1, "DPS")),
                    FieldDefinition.EnumField(FieldId(1, "Quality"), listOf("Poor", "Common", "Uncommon", "Rare", "Epic"))))
    repo.storeObjectDefinition(weaponDefinition)


    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
            get("/definition/{label}") {
                val label = call.parameters["label"]
                if(label != null) {
                    val objectDefinition = repo.loadObjectDefinition(ObjectHeader(label, 1))
                    if(objectDefinition != null) {
                        call.respondText(toJson(objectDefinition), ContentType.Application.Json)
                    } else {
                        call.respondText("No object with for "+label, ContentType.Text.Html)
                    }
                } else {
                    call.respondText("A label must be specified", ContentType.Text.Html)
                }
            }
            post("/new") {
                val a = call.request;
                val q = a.queryParameters;
                val s = a.document()
                val s1 = a.accept();
                val s2 = a.accept();
                val s3 = a.accept();
                val at = call.attributes;
                call.respondText("salut"+ call.request.headers.names())
            }
        }
    }.start(wait = true)
}

fun toJson(objectDefinition: ObjectDefinition): String {
    val mapper = jacksonObjectMapper()
    return mapper.writeValueAsString(objectDefinition)
}

fun fromJson(json: String) : ObjectDefinition {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(json)
}



