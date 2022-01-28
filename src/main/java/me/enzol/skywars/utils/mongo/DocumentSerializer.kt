package me.enzol.skywars.utils.mongo

import org.bson.Document

interface DocumentSerializer {

    fun serialize() : Document

}