package net.arjunsatarkar.rot13share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("ROT13ed text", rot(13, text))
            clipboard.setPrimaryClip(clip)
        }

        finish()
    }
}

