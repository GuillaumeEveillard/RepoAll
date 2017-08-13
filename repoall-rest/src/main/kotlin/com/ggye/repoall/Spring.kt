package com.ggye.repoall

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.response.respondText
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by ggye on 8/9/17.
 */


data class Greeting(val id: Long, val content: String)

@RestController
class GreetingController {

    val repo = MapBasedRepository()

    init {
        val weaponDefinition = ObjectDefinition(
                ObjectHeader("WEAPON", 1),
                listOf(
                        FieldDefinition.StringField(FieldId(1, "Name")),
                        FieldDefinition.StringField(FieldId(1, "Description")),
                        FieldDefinition.IntegerField(FieldId(1, "Level")),
                        FieldDefinition.FloatField(FieldId(1, "DPS")),
                        FieldDefinition.EnumField(FieldId(1, "Quality"), listOf("Poor", "Common", "Uncommon", "Rare", "Epic"))))
        repo.storeObjectDefinition(weaponDefinition)
    }


    val counter = AtomicLong()

    @GetMapping("/definition")
    fun getDefinition(@RequestParam(value = "label") label: String) : ObjectDefinition {
        val objectDefinition = repo.loadObjectDefinition(ObjectHeader(label, 1))
        if(objectDefinition != null) {
            return objectDefinition
        } else {
            return ObjectDefinition(
                    ObjectHeader("WEAPON", 1),
                    listOf())
        }
    }

    @PostMapping("/definition/new")
    fun addDefinition(@RequestParam(value = "def") json: String) {
        val objectDefinition = fromJson(json)
        repo.storeObjectDefinition(objectDefinition)
    }



    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String) = Greeting(counter.incrementAndGet(), "Hello, $name")

    @PostMapping("/pg")
    fun postGreeting(@RequestParam(value = "name") name: String) = Greeting(counter.incrementAndGet(), "Hello, $name")

}

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}