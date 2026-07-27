package com.example.ui.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.service.WhatsAppService
import com.example.ui.theme.CorporateGold

@Composable
fun FloatingWhatsAppButton(
    modifier: Modifier = Modifier,
    customMessage: String = "Hello JAN & CO, I need assistance with Business Income Tax Calculation."
) {
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            WhatsAppService.openWhatsAppChat(context, customMessage)
        },
        modifier = modifier
            .padding(16.dp)
            .testTag("whatsapp_fab"),
        shape = CircleShape,
        containerColor = Color(0xFF25D366), // Official WhatsApp Green
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "Contact JAN & CO on WhatsApp"
        )
    }
}
