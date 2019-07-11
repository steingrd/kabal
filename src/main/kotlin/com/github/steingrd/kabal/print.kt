package com.github.steingrd.kabal

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun lesFraDisk(): Kabal {
    FileReader(File("dump.st")).use {
        return objectMapper().readValue(it, Kabal::class.java)
    }
}

fun skrivTilDisk(kabal: Kabal) {
    FileWriter(File("dump.st")).use {
        objectMapper().writeValue(it, kabal)
    }
}

fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .configure(SerializationFeature.INDENT_OUTPUT, true)

fun skrivUt(kabal: Kabal) {
    println("Bunke: ${kabal.bunke}")
    println()

    kabal.mål.målSpor.values.map { l -> if (l.kort.isEmpty()) "------- " else l.kort.last().toString() + " " }.forEach(::print)
    println()
    println()

    val maksLinjer = kabal.spor.liste.map { e ->  e.value.topp.size + e.value.bunn.size}.max()
    val linjer = (0 until maksLinjer!!).map { StringBuilder() }
    kabal.spor.liste.forEach { (_, spor) -> skrivUtSpor(spor, linjer) }
    linjer.forEach { println(it) }

    println()
}

fun skrivUtSpor(spor: Spor, linjer: List<StringBuilder>) {
    spor.bunn.forEachIndexed { index, _ -> linjer[index].append("XXXXXXX ") }
    spor.topp.forEachIndexed { index, kort -> linjer[index + spor.bunn.size].append(kort).append(" ") }
}