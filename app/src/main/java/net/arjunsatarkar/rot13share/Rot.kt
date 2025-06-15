package net.arjunsatarkar.rot13share

fun rot(by: Int, text: String): String {
    val lowerA = 'a'
    val lowerZ = 'z'
    val upperA = 'A'
    val upperZ = 'Z'

    return buildString {
        for (char in text) {
            val base: Int = if (char in lowerA.rangeTo(lowerZ)) {
                lowerA.code
            } else if (char in upperA.rangeTo(upperZ)) {
                upperA.code
            } else {
                append(char)
                continue
            }

            append(((char.code - base + by) % 26 + base).toChar())
        }
    }
}

