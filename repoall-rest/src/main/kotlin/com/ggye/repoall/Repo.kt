package com.ggye.repoall

/**
 * Created by ggye on 8/8/17.
 */

interface Repository {
    fun storeObjectDefinition(def: ObjectDefinition)
    fun loadObjectDefinition(header: ObjectHeader) : ObjectDefinition?
}

class MapBasedRepository : Repository {
    val objectDefinition: MutableMap<ObjectHeader, ObjectDefinition> = HashMap()

    override fun storeObjectDefinition(def: ObjectDefinition) {
        objectDefinition.putIfAbsent(def.header, def)
    }

    override fun loadObjectDefinition(header: ObjectHeader): ObjectDefinition? {
        return objectDefinition.get(header)
    }

}