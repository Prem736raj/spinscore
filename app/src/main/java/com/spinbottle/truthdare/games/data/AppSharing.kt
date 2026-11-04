package com.spinbottle.truthdare.games.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object AppSharing {
    const val STORE_URL = "https://play.google.com/store/apps/details?id=com.spinbottle.truthdare.games"

    fun recap(playerCount: Int, rounds: Int, completed: Int): String =
        "We played Spin Bottle: $playerCount players, $rounds rounds and $completed challenges completed! 🍾\n" +
            "Bring your friends for the next round.\n$STORE_URL"

    fun share(context: Context, text: String = "Join our next game night with Spin Bottle: Truth or Dare! 🍾\n$STORE_URL") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try { context.startActivity(Intent.createChooser(intent, "Share Spin Bottle")) }
        catch (_: ActivityNotFoundException) { Toast.makeText(context, "No sharing app is available.", Toast.LENGTH_SHORT).show() }
    }

    fun rate(context: Context) {
        try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spinbottle.truthdare.games"))) }
        catch (_: ActivityNotFoundException) {
            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(STORE_URL))) }
            catch (_: ActivityNotFoundException) { Toast.makeText(context, "No store or browser is available.", Toast.LENGTH_SHORT).show() }
        }
    }
}
