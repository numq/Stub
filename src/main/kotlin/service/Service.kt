package service

import method.Method

data class Service(
    val name: String,
    val fullName: String,
    val methods: List<Method>,
    val fileContent: String,
)