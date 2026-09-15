package com.mypum.pos.feature.egresos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mypum.pos.domain.model.Egreso
import java.math.BigDecimal

@Composable
fun EgresosScreen(
    turnoId: Long,
    egresos: List<Egreso>,
    onRegistrar: (String, String) -> Unit,
    onFinished: () -> Unit = {}
) {
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }

    val total = egresos.fold(BigDecimal.ZERO) { acc, egreso ->
        acc.add(egreso.monto)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Egresos",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Turno #$turnoId")

        OutlinedTextField(
            value = concepto,
            onValueChange = { concepto = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Concepto") },
            singleLine = true
        )

        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Monto") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Button(
            onClick = {
                onRegistrar(concepto, monto)
                concepto = ""
                monto = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = concepto.isNotBlank() &&
                monto.replace(",", ".")
                    .toBigDecimalOrNull()
                    ?.let { it > BigDecimal.ZERO } == true
        ) {
            Text("Registrar egreso")
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total de egresos")
                Text(
                    "$ %.2f".format(total),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Text(
            "Movimientos del turno",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(egresos, key = { it.id }) { egreso ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                egreso.concepto,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Turno #${egreso.turnoId}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            "$ %.2f".format(egreso.monto),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
