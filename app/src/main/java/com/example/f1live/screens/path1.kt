package com.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/** One colored span of the track, expressed as fractions of total path length (0f..1f). */
data class TrackSector(
    val startFraction: Float,
    val endFraction: Float,
    val color: Color
)

/** A turn number, sector label, or callout that fades in as the draw-in passes it. */
data class TrackLabel(
    val text: String,
    val distanceFraction: Float, // where along the path (0f start .. 1f end) this appears
    val textColor: Color = Color.White,
    val dotColor: Color? = null, // draw a small numbered dot if non-null
    val offset: Offset = Offset(0f, -18f), // px offset from the path point, pre-scale
    val isPill: Boolean = false, // draw as a rounded highlight box (e.g. "SPEED TRAP")
    val pillColor: Color = Color(0xFFE91E8C)
)

@Composable
fun AnimatedCircuitTrack(
    modifier: Modifier = Modifier,
    sectors: List<TrackSector>,
    labels: List<TrackLabel> = emptyList(),
    strokeWidthPx: Float = 10f,
    durationMillis: Int = 2500,
    background: Color = Color.Black,
) {
    val circuitPath = remember { buildCircuitPath() }
    val bounds = remember(circuitPath) { circuitPath.getBounds() }
    val textMeasurer = rememberTextMeasurer()

    val pathMeasure = remember { PathMeasure() }
    val pathLength = remember(circuitPath) {
        pathMeasure.setPath(circuitPath, false)
        pathMeasure.length
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(circuitPath) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis))
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        val scale = minOf(size.width / bounds.width, size.height / bounds.height)
        val dx = (size.width - bounds.width * scale) / 2f - bounds.left * scale
        val dy = (size.height - bounds.height * scale) / 2f - bounds.top * scale

        fun toScreen(p: Offset) = Offset(p.x * scale + dx, p.y * scale + dy)

        // --- Draw the line itself, sector by sector ---
        withTransform({
            translate(left = dx, top = dy)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            sectors.forEach { sector ->
                val revealEnd = progress.value.coerceIn(sector.startFraction, sector.endFraction)
                if (revealEnd <= sector.startFraction) return@forEach

                val segment = Path()
                pathMeasure.getSegment(
                    startDistance = pathLength * sector.startFraction,
                    stopDistance = pathLength * revealEnd,
                    destination = segment,
                    startWithMoveTo = true
                )

                drawPath(
                    segment, sector.color.copy(alpha = 0.15f),
                    style = Stroke(strokeWidthPx * 8, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    segment, sector.color.copy(alpha = 0.3f),
                    style = Stroke(strokeWidthPx * 4, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    segment, sector.color,
                    style = Stroke(strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Comet head at current tip
            if (progress.value in 0.001f..0.999f) {
                val tip = pathMeasure.getPosition(pathLength * progress.value)
                val activeColor = sectors.lastOrNull { progress.value <= it.endFraction }?.color
                    ?: sectors.last().color
                drawCircle(activeColor.copy(alpha = 0.4f), radius = strokeWidthPx * 3f, center = tip)
                drawCircle(Color.White, radius = strokeWidthPx * 1.1f, center = tip)
            }
        }

        // --- Labels, drawn in screen space so text size stays constant ---
        labels.forEach { label ->
            val fadeWindow = 0.02f
            val alpha = ((progress.value - label.distanceFraction) / fadeWindow).coerceIn(0f, 1f)
            if (alpha <= 0f) return@forEach

            val rawPos = pathMeasure.getPosition(pathLength * label.distanceFraction.coerceIn(0f, 1f))
            val screenPos = toScreen(rawPos) + label.offset

            if (label.dotColor != null) {
                drawCircle(label.dotColor.copy(alpha = alpha), radius = 5f, center = toScreen(rawPos))
            }

            val style = TextStyle(
                color = if (label.isPill) Color.White else label.textColor.copy(alpha = alpha),
                fontSize = if (label.isPill) 12.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            val measured = textMeasurer.measure(label.text, style)

            if (label.isPill) {
                val padH = 10f
                val padV = 6f
                drawRoundRect(
                    color = label.pillColor.copy(alpha = alpha),
                    topLeft = Offset(
                        screenPos.x - measured.size.width / 2f - padH,
                        screenPos.y - measured.size.height / 2f - padV
                    ),
                    size = Size(
                        measured.size.width + padH * 2,
                        measured.size.height + padV * 2
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
            }

            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    screenPos.x - measured.size.width / 2f,
                    screenPos.y - measured.size.height / 2f
                )
            )
        }
    }
}

/**
 * Rebuilds the raw geometry from your ImageVector's path commands as a
 * real Path so PathMeasure can walk it. This mirrors Path1 exactly.
 */
private fun buildCircuitPath(): Path = Path().apply {
    moveTo(1468.7353f, 944.40726f)
    relativeLineTo(-1.2338f, -11.10342f)
    relativeLineTo(24.6743f, -64.15307f)
    relativeLineTo(12.3371f, -33.31025f)
    relativeLineTo(17.8889f, -25.90797f)
    relativeLineTo(18.5057f, -26.52483f)
    relativeLineTo(30.2259f, -32.69339f)
    relativeLineTo(44.4137f, -49.34852f)
    relativeLineTo(17.8888f, -24.0574f)
    relativeLineTo(10.4866f, -24.05741f)
    relativeLineTo(14.8046f, -15.42141f)
    relativeLineTo(18.5056f, -11.72027f)
    relativeLineTo(24.6743f, -6.78542f)
    relativeLineTo(9.8697f, -4.93485f)
    relativeLineTo(36.3945f, -27.75854f)
    relativeLineTo(35.1609f, -30.84283f)
    relativeLineTo(32.0765f, -27.14168f)
    relativeLineTo(8.0191f, -4.93485f)
    relativeLineTo(6.7855f, -6.16857f)
    relativeLineTo(35.1608f, -16.03827f)
    relativeLineTo(310.2788f, -135.09156f)
    relativeLineTo(11.1034f, -0.61686f)
    relativeLineTo(8.0191f, 2.46743f)
    relativeLineTo(6.1686f, 6.78542f)
    relativeLineTo(7.4023f, 8.63599f)
    relativeLineTo(6.7854f, 4.318f)
    relativeLineTo(9.8697f, 2.46742f)
    relativeLineTo(8.636f, -3.70114f)
    relativeLineTo(9.8697f, -3.70114f)
    relativeLineTo(30.8428f, -12.33713f)
    relativeLineTo(9.8697f, 0f)
    relativeLineTo(8.636f, 1.85057f)
    relativeLineTo(6.7854f, 6.78542f)
    relativeLineTo(96.2297f, 103.01503f)
    relativeLineTo(5.5517f, 8.63599f)
    relativeLineTo(-0.6169f, 6.16857f)
    relativeLineTo(0f, 7.40227f)
    relativeLineTo(-2.4674f, 6.16857f)
    relativeLineTo(-4.9349f, 4.93485f)
    relativeLineTo(-5.5517f, 4.318f)
    relativeLineTo(-8.636f, 1.23371f)
    relativeLineTo(-7.4022f, -0.61686f)
    relativeLineTo(-9.2529f, -4.31799f)
    relativeLineTo(-46.2642f, -53.04966f)
    relativeLineTo(-8.636f, -1.23371f)
    relativeLineTo(-10.4866f, -0.61686f)
    relativeLineTo(-3.7011f, 2.46743f)
    relativeLineTo(-45.6474f, 20.35626f)
    relativeLineTo(-35.7777f, 15.42141f)
    relativeLineTo(-120.287f, 38.2451f)
    relativeLineTo(-18.5057f, 14.1877f)
    relativeLineTo(-4.9348f, 12.33713f)
    relativeLineTo(-4.318f, 9.86971f)
    relativeLineTo(0f, 9.8697f)
    relativeLineTo(1.8505f, 22.82369f)
    relativeLineTo(1.8506f, 12.33713f)
    relativeLineTo(4.9348f, 12.33713f)
    relativeLineTo(8.636f, 17.88884f)
    relativeLineTo(14.1877f, 9.8697f)
    relativeLineTo(17.8889f, 13.57084f)
    relativeLineTo(149.2792f, 35.16082f)
    relativeLineTo(9.8697f, 4.318f)
    relativeLineTo(6.7855f, 6.16856f)
    relativeLineTo(6.1685f, 11.10342f)
    relativeLineTo(4.318f, 15.42141f)
    relativeLineTo(-2.4674f, 7.40228f)
    relativeLineTo(-3.7011f, 14.80455f)
    relativeLineTo(-4.9349f, 11.72028f)
    relativeLineTo(0f, 17.27198f)
    relativeLineTo(6.7854f, 9.8697f)
    relativeLineTo(9.2529f, 8.63599f)
    relativeLineTo(12.954f, 5.55171f)
    relativeLineTo(77.107f, 35.77767f)
    relativeLineTo(4.9349f, 4.318f)
    relativeLineTo(3.7011f, 7.40228f)
    relativeLineTo(1.2337f, 8.01913f)
    relativeLineTo(0f, 4.93485f)
    relativeLineTo(-37.0113f, 64.15308f)
    relativeLineTo(-5.5518f, 6.78544f)
    relativeLineTo(-6.7854f, 3.0843f)
    relativeLineTo(-6.7854f, 1.8505f)
    relativeLineTo(-12.3371f, 1.2337f)
    relativeLineTo(-20.3563f, 0f)
    relativeLineTo(-26.5248f, -7.4022f)
    relativeLineTo(-20.3563f, -7.40231f)
    relativeLineTo(-16.6551f, -9.25285f)
    relativeLineTo(-16.0383f, -11.10342f)
    relativeLineTo(-19.7394f, -14.80455f)
    relativeLineTo(-22.2068f, -16.03827f)
    relativeLineTo(-17.8889f, -18.50569f)
    relativeLineTo(-19.7394f, -27.75855f)
    relativeLineTo(-38.2451f, -54.28337f)
    relativeLineTo(-11.1034f, -11.72027f)
    relativeLineTo(-16.6551f, -13.57084f)
    relativeLineTo(-13.5709f, -10.48656f)
    relativeLineTo(-104.2487f, -35.16082f)
    relativeLineTo(-12.3371f, 0f)
    relativeLineTo(-14.8046f, 1.23371f)
    relativeLineTo(-16.0383f, 8.63599f)
    relativeLineTo(-44.0878f, 22.25455f)
    relativeLineTo(-35.7671f, 18.3197f)
    relativeLineTo(-20.9368f, 13.0855f)
    relativeLineTo(-73.2788f, 15.7026f)
    relativeLineTo(-43.6183f, 10.4684f)
    relativeLineTo(-1.5267f, -2.61709f)
    relativeCubicTo(0f, 0f, -1.7447f, -1.09046f, -1.7447f, -2.39901f)
    relativeCubicTo(0f, -1.30855f, -0.4362f, -17.22925f, -0.4362f, -17.22925f)
    relativeLineTo(-1.9628f, -3.70756f)
    relativeLineTo(-2.8352f, -2.39901f)
    relativeLineTo(-3.0533f, 0.65428f)
    relativeLineTo(-4.798f, 1.52664f)
    relativeLineTo(-199.9901f, 156.15366f)
    relativeLineTo(-6.8623f, 3.61893f)
    relativeLineTo(-3.7012f, 1.54215f)
    relativeLineTo(-4.7806f, 0.46264f)
    close()
}