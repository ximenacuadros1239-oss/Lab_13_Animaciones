package com.example.laboratorio13_animaciones

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding

@Composable
fun Ejercicio3() {
    var activo by remember { mutableStateOf(false) }
    val tamano by animateDpAsState(if (activo) 200.dp else 80.dp, tween(700), label = "tamano")
    val desplazamiento by animateDpAsState(if (activo) 120.dp else 0.dp, tween(700), label = "offset")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { activo = !activo }) { Text("Mover y escalar") }
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .size(tamano)
                .offset(x = desplazamiento, y = desplazamiento / 2)
                .background(Color(0xFFFF9800), RoundedCornerShape(12.dp))
        )
    }
}