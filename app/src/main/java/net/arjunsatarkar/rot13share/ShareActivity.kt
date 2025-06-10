package net.arjunsatarkar.rot13share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ROT13ed text", rot13(text))
            clipboard.setPrimaryClip(clip)
        }

        finish()
    }
}

fun rot13(text: String): String {
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

            append(((char.code - base + 13) % 26 + base).toChar())
        }
    }
}

