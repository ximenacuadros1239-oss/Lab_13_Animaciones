package com.example.laboratorio13_animaciones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay

enum class Fase { Inicio, Jugando, GameOver }

class Objeto(val id: Int, x: Float, y: Float, hp: Int = 1) {
    var x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)
    var hp by mutableIntStateOf(hp)
}

class Explosion(val id: Int, val x: Float, val y: Float, val nacimiento: Float)

@Composable
fun EjercicioFinal() {
    var fase by remember { mutableStateOf(Fase.Inicio) }
    var puntos by remember { mutableIntStateOf(0) }
    var vidas by remember { mutableIntStateOf(3) }
    var bombas by remember { mutableIntStateOf(3) }
    var nivel by remember { mutableIntStateOf(1) }
    var naveX by remember { mutableFloatStateOf(0f) }
    var naveY by remember { mutableFloatStateOf(0f) }
    var inclinacion by remember { mutableFloatStateOf(0f) }
    var golpes by remember { mutableIntStateOf(0) }
    var destello by remember { mutableStateOf(false) }
    var idGen by remember { mutableIntStateOf(0) }

    val balas = remember { mutableStateListOf<Objeto>() }
    val enemigos = remember { mutableStateListOf<Objeto>() }
    val explosiones = remember { mutableStateListOf<Explosion>() }
    val estrellas = remember { List(30) { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextInt(1, 4)) } }
    val density = LocalDensity.current

    // Fondo de estrellas en movimiento
    val fondoT = rememberInfiniteTransition(label = "fondo")
    val scroll by fondoT.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scroll"
    )

    // Destello rojo al recibir daño
    LaunchedEffect(golpes) {
        if (golpes > 0) {
            destello = true
            delay(250)
            destello = false
        }
    }
    val colorDestello by animateColorAsState(
        targetValue = if (destello) Color(0x66FF0000) else Color.Transparent,
        animationSpec = tween(150),
        label = "destello"
    )
    val rotacion by animateFloatAsState(
        targetValue = inclinacion,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rotacion"
    )

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B0B1E), Color(0xFF241046))))
    ) {
        val w = with(density) { maxWidth.toPx() }
        val h = with(density) { maxHeight.toPx() }
        val rNave = with(density) { 24.dp.toPx() }
        val rEnemigo = with(density) { 20.dp.toPx() }
        val rBala = with(density) { 5.dp.toPx() }

        fun iniciar() {
            balas.clear(); enemigos.clear(); explosiones.clear()
            puntos = 0; vidas = 3; bombas = 3; nivel = 1
            naveX = w / 2; naveY = h * 0.8f
            fase = Fase.Jugando
        }

        // Bucle del juego
        LaunchedEffect(fase, w, h) {
            if (fase != Fase.Jugando) return@LaunchedEffect
            var ultimo = withFrameNanos { it }
            var t = 0f
            var tDisparo = 0f
            var tEnemigo = 0f
            while (fase == Fase.Jugando) {
                val ahora = withFrameNanos { it }
                val dt = ((ahora - ultimo) / 1_000_000_000f).coerceAtMost(0.05f)
                ultimo = ahora
                t += dt; tDisparo += dt; tEnemigo += dt

                // Disparo automático
                if (tDisparo > 0.22f) {
                    tDisparo = 0f
                    balas.add(Objeto(idGen++, naveX, naveY - rNave))
                }
                // Aparición de enemigos
                val intervalo = (1.1f - nivel * 0.08f).coerceAtLeast(0.35f)
                if (tEnemigo > intervalo) {
                    tEnemigo = 0f
                    val resistente = nivel >= 3 && Random.nextInt(4) == 0
                    enemigos.add(
                        Objeto(
                            idGen++,
                            Random.nextFloat() * (w - 2 * rEnemigo) + rEnemigo,
                            -rEnemigo,
                            if (resistente) 3 else 1
                        )
                    )
                }

                // Movimiento
                val velBala = with(density) { 520.dp.toPx() }
                val velEnemigo = with(density) { (110 + nivel * 25).dp.toPx() }
                for (b in balas) b.y -= velBala * dt
                for (e in enemigos) e.y += velEnemigo * dt
                balas.removeAll { it.y < -50f }

                // Balas contra enemigos
                val balasUsadas = mutableListOf<Objeto>()
                val muertos = mutableListOf<Objeto>()
                for (e in enemigos.toList()) {
                    for (b in balas.toList()) {
                        if (b in balasUsadas) continue
                        val dx = e.x - b.x
                        val dy = e.y - b.y
                        val r = rEnemigo + rBala
                        if (dx * dx + dy * dy < r * r) {
                            balasUsadas.add(b)
                            e.hp--
                            if (e.hp <= 0) {
                                muertos.add(e)
                                break
                            }
                        }
                    }
                }
                balas.removeAll(balasUsadas)
                for (e in muertos) {
                    puntos += 10
                    explosiones.add(Explosion(idGen++, e.x, e.y, t))
                }
                enemigos.removeAll(muertos)

                // Enemigos contra la nave o que escapan
                for (e in enemigos.toList()) {
                    val dx = e.x - naveX
                    val dy = e.y - naveY
                    val r = rEnemigo + rNave * 0.8f
                    val choca = dx * dx + dy * dy < r * r
                    if (choca || e.y > h + rEnemigo) {
                        enemigos.remove(e)
                        vidas--
                        golpes++
                        if (choca) explosiones.add(Explosion(idGen++, e.x, e.y, t))
                    }
                }

                nivel = 1 + puntos / 100
                explosiones.removeAll { t - it.nacimiento > 0.7f }
                if (vidas <= 0) fase = Fase.GameOver
            }
        }

        // Zona de arrastre (mueve la nave por toda la pantalla)
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(w, h) {
                    detectDragGestures(
                        onDragEnd = { inclinacion = 0f },
                        onDragCancel = { inclinacion = 0f }
                    ) { change, drag ->
                        change.consume()
                        if (fase == Fase.Jugando) {
                            naveX = (naveX + drag.x).coerceIn(rNave, w - rNave)
                            naveY = (naveY + drag.y).coerceIn(rNave, h - rNave)
                            inclinacion = (drag.x * 3f).coerceIn(-25f, 25f)
                        }
                    }
                }
        ) {
            // Estrellas
            for ((ex, ey, tam) in estrellas) {
                Box(
                    Modifier
                        .offset { IntOffset((ex * w).roundToInt(), (((ey + scroll * tam) % 1f) * h).roundToInt()) }
                        .size(tam.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                )
            }

            // Balas
            for (b in balas) {
                key(b.id) {
                    Box(
                        Modifier
                            .offset { IntOffset((b.x - rBala).roundToInt(), (b.y - rBala).roundToInt()) }
                            .size(10.dp)
                            .background(Color(0xFFFFEB3B), CircleShape)
                    )
                }
            }

            // Enemigos
            for (e in enemigos) {
                key(e.id) {
                    Box(
                        Modifier
                            .offset { IntOffset((e.x - rEnemigo).roundToInt(), (e.y - rEnemigo).roundToInt()) }
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (e.hp > 1) "👹" else "👾", fontSize = 30.sp)
                    }
                }
            }

            // Explosiones
            for (ex in explosiones) {
                key(ex.id) { ExplosionView(ex.x, ex.y) }
            }

            // Nave
            if (fase == Fase.Jugando) {
                Box(
                    Modifier
                        .offset { IntOffset((naveX - rNave).roundToInt(), (naveY - rNave).roundToInt()) }
                        .size(48.dp)
                        .rotate(rotacion),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛸", fontSize = 36.sp)
                }
            }
        }

        // Destello de daño
        Box(Modifier.fillMaxSize().background(colorDestello))

        // HUD
        if (fase == Fase.Jugando) {
            Row(
                Modifier.fillMaxWidth().padding(12.dp).align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedContent(
                    targetState = puntos,
                    transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                    label = "puntos"
                ) { p -> Text("⭐ $p", color = Color.White, fontSize = 22.sp) }
                Text("Nivel $nivel", color = Color(0xFF03DAC5), fontSize = 22.sp)
                Text("❤️".repeat(vidas.coerceAtLeast(0)), fontSize = 22.sp)
            }

            Button(
                onClick = {
                    if (bombas > 0 && enemigos.isNotEmpty()) {
                        bombas--
                        for (e in enemigos.toList()) {
                            puntos += 10
                            explosiones.add(Explosion(idGen++, e.x, e.y, 0f))
                        }
                        enemigos.clear()
                        golpes++
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
            ) { Text("💣 x$bombas") }
        }

        // Pantallas de inicio y game over
        AnimatedContent(
            targetState = fase,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) },
            label = "pantallas"
        ) { f ->
            when (f) {
                Fase.Inicio -> Column(
                    Modifier.fillMaxSize().background(Color(0xAA000000)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🛸 DEFENSOR GALÁCTICO", color = Color.White, fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Arrastra para mover la nave", color = Color.LightGray)
                    Text("Dispara automáticamente", color = Color.LightGray)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { iniciar() }) { Text("Empezar") }
                }

                Fase.GameOver -> Column(
                    Modifier.fillMaxSize().background(Color(0xAA000000)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("GAME OVER", color = Color.White, fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Puntos: $puntos  |  Nivel: $nivel", color = Color.LightGray, fontSize = 20.sp)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { iniciar() }) { Text("Jugar de nuevo") }
                }

                Fase.Jugando -> Spacer(Modifier)
            }
        }
    }
}

@Composable
fun ExplosionView(x: Float, y: Float) {
    var activa by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { activa = true }
    val escala by animateFloatAsState(if (activa) 2.2f else 0.3f, tween(500), label = "escala")
    val alfa by animateFloatAsState(if (activa) 0f else 1f, tween(500), label = "alfa")

    Box(
        Modifier
            .offset { IntOffset((x - 20.dp.toPx()).roundToInt(), (y - 20.dp.toPx()).roundToInt()) }
            .size(40.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                this.alpha = alfa
            },
        contentAlignment = Alignment.Center
    ) {
        Text("💥", fontSize = 30.sp)
    }
}