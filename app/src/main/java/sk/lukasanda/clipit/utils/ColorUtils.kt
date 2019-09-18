package sk.lukasanda.clipit.utils

import sk.lukasanda.clipit.utils.RandomColor.Color
import java.util.Random

class Range(var start: Int, var end: Int) {

    fun contain(value: Int): Boolean {
        return value >= start && value <= end
    }

    override fun toString(): String {
        return "start: $start end: $end"
    }
}

class ColorInfo(
    var hueRange: Range,
    var saturationRange: Range,
    var brightnessRange: Range,
    var lowerBounds: List<Range>
)

fun randomColorNew(string: String): Int {
    val seed = string.hashCode().toLong()
    val color = enumValues<Color>()[string.hashCode() % enumValues<Color>().size]
    return RandomColor(seed).randomColor(color)
}