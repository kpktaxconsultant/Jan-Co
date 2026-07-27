package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppService {

    private const val WHATSAPP_NUMBER = "923277669933"

    fun openWhatsAppChat(context: Context, defaultMessage: String = "") {
        val encodedMessage = Uri.encode(defaultMessage)
        val uri = Uri.parse("https://wa.me/$WHATSAPP_NUMBER?text=$encodedMessage")
        
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch WhatsApp. Please contact +92 327 7669933 directly.", Toast.LENGTH_LONG).show()
        }
    }

    fun makeDirectCall(context: Context) {
        val uri = Uri.parse("tel:+923277669933")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(context: Context, subject: String = "Tax Advisory Inquiry", body: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:tehsinullahjan@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
        }
    }
}
