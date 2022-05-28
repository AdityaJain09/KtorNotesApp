package com.stark.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Note(
    val title: String,
    val description: String,
    val createdDate: Long,
    val updatedDate: Long,
    val owners: List<String>,
    val noteColor: String,
    @BsonId val id: String = ObjectId().toString()
)