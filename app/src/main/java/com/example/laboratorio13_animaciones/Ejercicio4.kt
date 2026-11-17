package com.example.laboratorio13_animaciones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class Estado { Cargando, Contenido, Error }

@Composable
fun Ejercicio4() {
    var estado by remember { mutableStateOf(Estado.Cargando) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Estado.values().forEach { e ->
                Button(onClick = { estado = e }) { Text(e.name) }
            }
        }
        Spacer(Modifier.height(32.dp))

        AnimatedContent(
            targetState = estado,
            transitionSpec = {
                fadeIn(tween(durationMillis = 600)) togetherWith
                        fadeOut(tween(durationMillis = 300))
            },
            label = "estado"
        ) { e ->
            when (e) {
                Estado.Cargando -> CircularProgressIndicator()
                Estado.Contenido -> Text(
                    "Datos cargados correctamente",
                    style = MaterialTheme.typography.titleLarge
                )
                Estado.Error -> Text(
                    "Ocurrió un error",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}