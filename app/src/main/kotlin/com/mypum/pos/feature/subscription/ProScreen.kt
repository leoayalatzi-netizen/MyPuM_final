package com.mypum.pos.feature.subscription

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mypum.pos.domain.model.subscription.SubscriptionPricing

@Composable
fun ProScreen(
    onUpgrade: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val price = SubscriptionPricing.PRO_ANNUAL_PRICE
    val currency = SubscriptionPricing.PRO_ANNUAL_CURRENCY

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar"
                )
            }

            Text(
                text = "MyPuM PRO",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Text(
            text = "Haz crecer tu negocio sin cambiar de sistema.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "PRO anual",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "$price $currency / año",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Un solo plan para llevar MyPuM más lejos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ProFeature(
            icon = Icons.Default.Inventory2,
            title = "Productos ilimitados",
            description = "Sin el límite de 50 productos del plan FREE."
        )

        ProFeature(
            icon = Icons.Default.FileDownload,
            title = "Importación y exportación",
            description = "Trabaja con datos mediante Excel y CSV."
        )

        ProFeature(
            icon = Icons.Default.Insights,
            title = "Reportes avanzados",
            description = "Más estadísticas, márgenes y análisis de tu negocio."
        )

        ProFeature(
            icon = Icons.Default.Security,
            title = "Respaldos avanzados",
            description = "Más opciones para proteger y recuperar tus datos."
        )

        ProFeature(
            icon = Icons.Default.Groups,
            title = "Empleados y permisos",
            description = "Administra usuarios y controla sus permisos."
        )

        ProFeature(
            icon = Icons.Default.CloudSync,
            title = "Sincronización en la nube",
            description = "Prepara tu negocio para trabajar con información sincronizada."
        )

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = onUpgrade,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar a PRO")
        }

        Text(
            text = "La compra se realizará mediante Google Play cuando el sistema de suscripciones esté habilitado.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.padding(6.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
