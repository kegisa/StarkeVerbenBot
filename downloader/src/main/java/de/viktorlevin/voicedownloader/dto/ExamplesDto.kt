package de.viktorlevin.voicedownloader.dto

data class ExamplesDto(val result: List<Result> = emptyList()) {

    data class Result(val text: String?, val examples: List<Example> = emptyList())

    data class Example(val src: String?, val dst: String?, val ref: Reference?)

    data class Reference(val title: String?, val type: String?)
}