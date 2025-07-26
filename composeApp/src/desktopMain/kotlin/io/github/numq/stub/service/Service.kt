package io.github.numq.stub.service

import io.github.numq.stub.method.Method

data class Service(
    val name: String,
    val fullName: String,
    val methods: List<Method>,
    val fileContent: String,
)