package com.github.steingrd.kabal

import com.fasterxml.jackson.databind.ObjectMapper
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

fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(KotlinModule())

fun skrivUt(kabal: Kabal) {
    println("Bunke: ${kabal.bunke}")
    println()

    kabal.mål.målSpor.values.map { l -> if (l.kort.isEmpty()) "------- " else l.kort.last().toString() + " " }.forEach(::print)
    println()
    println()

    val linjer = mutableListOf<StringBuilder>()
    kabal.spor.liste.keys.sorted().forEach { skrivUtSpor(linjer, kabal.spor.liste[it] ?: error("Ugyldig index $it")) }
    linjer.forEach(::println)

    println()
}

fun skrivUtSpor(linjer: MutableList<StringBuilder>, spor: Spor) {
    // legger til evt nye linjer og padder disse med mellomrom
    if ((spor.bunn.size + spor.topp.size) > linjer.size) {
        val nyeLinjer = spor.bunn.size + spor.topp.size - linjer.size
        (0 until nyeLinjer).forEach { _ ->
            val sb = StringBuilder("".padStart(if (linjer.isEmpty()) 0 else linjer[0].length, ' '))
            linjer.add(sb)
        }
    } else if (spor.bunn.isEmpty() && spor.topp.isEmpty() && linjer.isEmpty()) {
        linjer.add(StringBuilder())
    }

    // skriver ut linjene til linjebufrene
    linjer.forEachIndexed { i, sb ->
        when {
            spor.bunn.isEmpty() && spor.topp.isEmpty() -> sb.append("        ")
            i < spor.bunn.size -> sb.append("XXXXXXX ")
            i < (spor.bunn.size + spor.topp.size) -> sb.append(spor.topp[i - spor.bunn.size].toString() + " ")
            else -> sb.append("        ")
        }
    }
}
