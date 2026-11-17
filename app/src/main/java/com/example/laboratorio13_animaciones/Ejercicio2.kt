package com.example.laboratorio13_animaciones

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

@Composable
fun Ejercicio2() {
    var azul by remember { mutableStateOf(true) }
    val color by animateColorAsState(
        targetValue = if (azul) Color(0xFF2196F3) else Color(0xFF4CAF50),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "color"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { azul = !azul }) { Text("Cambiar color") }
        Spacer(Modifier.height(24.dp))
        Box(Modifier.size(150.dp).background(color, RoundedCornerShape(16.dp)))
    }
}