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

    print("    ")
    kabal.mål.målSpor.values.map { l -> if (l.kort.isEmpty()) "------- " else l.kort.last().toString() + " " }.forEach(::print)
    println()
    println()

    val maksLinjer = kabal.spor.liste.map { e ->  e.value.topp.size + e.value.bunn.size}.max()
    val linjer = (0 until maksLinjer!!).map { StringBuilder().append("$it".padStart(2, ' ')).append(": ") }
    (0 until 7).forEach { skrivUtSpor(it, kabal.spor.liste[it] ?: error("Ugyldig index"), linjer) }
    println("    0       1       2       3       4       5       6")
    linjer.forEach { println(it) }

    println()
}

fun skrivUtSpor(sporIndex: Int, spor: Spor, linjer: List<StringBuilder>) {
    // fyll evt på med blanke på alle linjer først
    linjer.forEach {
        while (sporIndex * 8 > fjernKontroltegn(it).length) {
            it.append("        ")
        }
    }
    spor.bunn.forEachIndexed { index, _ ->
        linjer[index].append("XXXXXXX ")
    }
    spor.topp.forEachIndexed { index, kort ->
        linjer[index + spor.bunn.size].append(kort).append(" ")
    }
}

fun fjernKontroltegn(stringBuilder: StringBuilder): String {
    return stringBuilder.toString()
            .replace(Regex("\\P{Print}"), "")
            .replace("[31m", "")
            .replace("[30m", "")
            .replace("[0m", "")
}