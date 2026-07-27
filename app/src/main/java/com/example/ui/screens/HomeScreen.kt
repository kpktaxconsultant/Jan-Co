package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.service.WhatsAppService
import com.example.ui.theme.CorporateGold
import com.example.ui.theme.CorporateNavy
import com.example.ui.theme.CorporateNavyDark
import com.example.ui.viewmodels.MainViewModel
import com.example.ui.widgets.JanCoHeaderCard

data class DashboardCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val iconColor: Color,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val activeYear by viewModel.activeTaxYear.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val dashboardCards = remember {
        listOf(
            DashboardCardItem(
                id = "calc",
                title = "Business Tax Calculator",
                subtitle = "Module 01 • Business Income Tax",
                icon = Icons.Default.Calculate,
                containerColor = CorporateNavy,
                iconColor = CorporateGold,
                route = "calculator"
            ),
            DashboardCardItem(
                id = "history",
                title = "Previous Calculations",
                subtitle = "View and search saved tax records",
                icon = Icons.Default.History,
                containerColor = Color(0xFF1E293B),
                iconColor = Color(0xFF38BDF8),
                route = "history"
            ),
            DashboardCardItem(
                id = "guide",
                title = "Tax Guide & Slabs",
                subtitle = "Active & Historical FBR Slabs",
                icon = Icons.Default.MenuBook,
                containerColor = Color(0xFF1E293B),
                iconColor = Color(0xFFF59E0B),
                route = "tax_guide"
            ),
            DashboardCardItem(
                id = "whatsapp",
                title = "WhatsApp Advisory",
                subtitle = "Direct contact +92 327 7669933",
                icon = Icons.Default.Chat,
                containerColor = Color(0xFF15803D),
                iconColor = Color.White,
                route = "whatsapp"
            ),
            DashboardCardItem(
                id = "contact",
                title = "Contact & Consultation",
                subtitle = "Legal & Corporate Advisory",
                icon = Icons.Default.Phone,
                containerColor = Color(0xFF1E293B),
                iconColor = Color(0xFF10B981),
                route = "about"
            ),
            DashboardCardItem(
                id = "about",
                title = "About JAN & CO",
                subtitle = "Advocate Tehsin Ullah Jan",
                icon = Icons.Default.Info,
                containerColor = Color(0xFF1E293B),
                iconColor = CorporateGold,
                route = "about"
            ),
            DashboardCardItem(
                id = "share",
                title = "Share App",
                subtitle = "Recommend to business peers",
                icon = Icons.Default.Share,
                containerColor = Color(0xFF1E293B),
                iconColor = Color(0xFFA855F7),
                route = "share"
            ),
            DashboardCardItem(
                id = "admin",
                title = "Admin Panel",
                subtitle = "Manage Slabs & Tax Years (Protected)",
                icon = Icons.Default.Lock,
                containerColor = Color(0xFF0F172A),
                iconColor = Color(0xFFEF4444),
                route = "admin"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.jan_co_official_logo_1785185619463),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = "JAN & CO Tax Calculators",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Tax & Corporate Consultants",
                                style = MaterialTheme.typography.labelSmall,
                                color = CorporateGold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateTo("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = CorporateGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CorporateNavy)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner Card
            JanCoHeaderCard()

            // Active Tax Year Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "ACTIVE TAX YEAR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "FY ${activeYear?.taxYear ?: "2024-2025"}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Surface(
                        color = CorporateNavy,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "FBR Standard",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CorporateGold
                        )
                    }
                }
            }

            // Hero Graphic Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_tax_banner_1785156401489),
                        contentDescription = "Tax Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(CorporateNavyDark.copy(alpha = 0.9f), Color.Transparent)
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                    ) {
                        Surface(
                            color = CorporateGold.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "JAN & CO CALCULATORS SUITE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = CorporateGold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "JAN & CO Tax Calculators",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Module 01: Business Tax Calculator (Active)\nPrecision FBR slab calculations for Pakistan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CorporateGold
                        )
                    }
                }
            }

            Text(
                text = "Services & Features",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Feature Cards Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                dashboardCards.chunked(2).forEach { rowCards ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCards.forEach { item ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_card_${item.id}")
                                    .clickable {
                                        when (item.route) {
                                            "whatsapp" -> WhatsAppService.openWhatsAppChat(context)
                                            "share" -> shareApp(context)
                                            else -> onNavigateTo(item.route)
                                        }
                                    },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(item.containerColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.iconColor
                                        )
                                    }
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer credits
            Text(
                text = "JAN & CO Tax & Corporate Consultants\nTEHSIN ULLAH JAN • Advocate High Court",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private fun shareApp(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Download JAN & CO Tax Calculators - Official Pakistan Tax Assessment Suite (Business Tax, Salary Tax & Advisory) by Advocate Tehsin Ullah Jan (+92 327 7669933)."
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share JAN & CO Tax Calculators"))
}
