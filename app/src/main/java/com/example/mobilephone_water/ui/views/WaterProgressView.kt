package com.example.mobilephone_water.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class WaterProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f // 0-100
    private var currentWaterLevel = 0f
    private var targetWaterLevel = 0f
    private var wavePhase = 0f
    private var waveAnimator: ValueAnimator? = null

    
    private val waterDrops = mutableListOf<WaterDrop>()
    private val maxDrops = 15

    
    private val bubbles = mutableListOf<Bubble>()
    private val maxBubbles = 8

  
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circleBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dropPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    
    private val circleColor = Color.parseColor("#1a1f3a")
    private val waterColor = Color.parseColor("#00BCD4")
    private val waterDarkColor = Color.parseColor("#00897B")
    private val backgroundColor = Color.parseColor("#0a0e27")
    private val labelTextColor = Color.parseColor("#b0b0b0")
    private val valueTextColor = Color.parseColor("#ffffff")

    
    private var dailyGoalAmount = 2237
    private var currentAmount = 0

    init {
        circlePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 24f
            color = circleColor
            strokeCap = Paint.Cap.ROUND
        }

        circleBackgroundPaint.apply {
            style = Paint.Style.FILL
            color = backgroundColor
        }

        waterPaint.apply {
            style = Paint.Style.FILL
        }

        textPaint.apply {
            textSize = 72f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(4f, 0f, 2f, Color.parseColor("#40000000"))
        }

        labelPaint.apply {
            color = labelTextColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
        }

        valuePaint.apply {
            color = valueTextColor
            textSize = 48f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        dropPaint.apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#804FC3F7")
        }

        bubblePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.parseColor("#60FFFFFF")
        }

        glowPaint.apply {
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        }

        startWaveAnimation()
        startParticleAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2f
        val centerY = height * 0.35f
        val radius = minOf(width, height) * 0.32f

       
        if (progress >= 100f) {
            drawGoalAchievedGlow(canvas, centerX, centerY, radius)
        }

        
        canvas.drawCircle(centerX, centerY, radius, circleBackgroundPaint)

        
        canvas.save()
        val clipPath = Path()
        clipPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        canvas.clipPath(clipPath)

        drawWater(canvas, centerX, centerY, radius)

        
        if (currentWaterLevel > 5f) {
            drawBubbles(canvas, centerX, centerY, radius)
        }

        canvas.restore()

      
        drawWaterDrops(canvas)

       
        drawProgressCircle(canvas, centerX, centerY, radius)

        
        drawCenterText(canvas, centerX, centerY, radius)

        
        drawBottomInfo(canvas, centerX, centerY + radius + 80f)
    }

    private fun drawProgressCircle(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
       
        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        
        if (progress > 0) {
            val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 24f
                strokeCap = Paint.Cap.ROUND
                shader = SweepGradient(
                    centerX, centerY,
                    intArrayOf(
                        Color.parseColor("#4FC3F7"),
                        Color.parseColor("#0288D1"),
                        Color.parseColor("#01579B")
                    ),
                    null
                )
            }

            val sweepAngle = (progress / 100f) * 360f
            canvas.drawArc(
                centerX - radius, centerY - radius,
                centerX + radius, centerY + radius,
                -90f, sweepAngle, false, progressPaint
            )
        }
    }

    private fun drawGoalAchievedGlow(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
      
        val glowAlpha = ((sin(wavePhase * Math.PI * 4) + 1) / 2 * 100 + 50).toInt()
        glowPaint.color = Color.argb(glowAlpha, 79, 195, 247)

        canvas.drawCircle(centerX, centerY, radius + 30f, glowPaint)
    }

    private fun drawCenterText(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val progressText = "${progress.toInt()}%"

        val textColor = if (currentWaterLevel > 60f) {
            Color.WHITE
        } else {
            Color.parseColor("#4FC3F7")
        }

      
        textPaint.setShadowLayer(8f, 2f, 2f, Color.parseColor("#80000000"))
        textPaint.color = textColor
        canvas.drawText(progressText, centerX, centerY + 25f, textPaint)


        val motivationText = when {
            progress >= 100f -> "🎉 Цель достигнута!"
            progress >= 75f -> "Почти готово! 💪"
            progress >= 50f -> "Отличный прогресс! ✨"
            progress >= 25f -> "Продолжай! 💧"
            else -> "Начни день правильно!"
        }

      
        val motivationPaint = Paint(labelPaint).apply {
            textSize = 40f
            color = if (currentWaterLevel > 40f) Color.WHITE else labelTextColor
            setShadowLayer(6f, 1f, 1f, Color.parseColor("#80000000"))
        }
        canvas.drawText(motivationText, centerX, centerY + 85f, motivationPaint)

        
        textPaint.clearShadowLayer()
    }

    private fun drawWater(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        
        if (kotlin.math.abs(currentWaterLevel - targetWaterLevel) > 0.01f) {
            currentWaterLevel += (targetWaterLevel - currentWaterLevel) * 0.03f // Медленнее (было 0.05f)
            invalidate()
        }

        val waterLevel = centerY + radius - (currentWaterLevel / 100f) * (radius * 2)

        if (currentWaterLevel <= 0) return

      
        val waterGradient = LinearGradient(
            0f, waterLevel - 50f,
            0f, centerY + radius,
            intArrayOf(
                Color.parseColor("#80DEEA"),
                Color.parseColor("#4FC3F7"),
                Color.parseColor("#0288D1"),
                Color.parseColor("#01579B")
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        waterPaint.shader = waterGradient

        val path = Path()

       
        val waveAmplitude1 = 12f
        val waveAmplitude2 = 8f
        val waveFrequency = 2.5f
        val startX = centerX - radius
        val endX = centerX + radius

        path.moveTo(startX, waterLevel)

        val steps = 100
        for (i in 0..steps) {
            val x = startX + (endX - startX) * (i.toFloat() / steps)
            val angle1 = (i.toFloat() / steps * waveFrequency + wavePhase) * Math.PI * 2
            val angle2 = (i.toFloat() / steps * waveFrequency * 1.5 - wavePhase * 0.7) * Math.PI * 2
            val waveOffset = sin(angle1).toFloat() * waveAmplitude1 +
                    sin(angle2).toFloat() * waveAmplitude2
            path.lineTo(x, waterLevel + waveOffset)
        }

        path.lineTo(endX, centerY + radius)
        path.lineTo(startX, centerY + radius)
        path.close()

        canvas.drawPath(path, waterPaint)

       
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#60FFFFFF")
            maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
        }

        val highlightOffset = (sin(wavePhase * Math.PI * 2) * 20).toFloat()
        canvas.drawCircle(
            centerX + radius * 0.3f + highlightOffset,
            waterLevel + 30f,
            18f,
            highlightPaint
        )
        canvas.drawCircle(
            centerX - radius * 0.2f - highlightOffset,
            waterLevel + 50f,
            13f,
            highlightPaint
        )
    }

    private fun drawBubbles(canvas: Canvas, centerX: Float, centerY: Float, radius: Float) {
        val waterLevel = centerY + radius - (currentWaterLevel / 100f) * (radius * 2)

        bubbles.forEach { bubble ->
            bubble.update()
            if (bubble.y < waterLevel - 10f) {
                bubble.reset(centerX, centerY, radius, waterLevel)
            }

            bubblePaint.alpha = (bubble.alpha * 255).toInt()
            canvas.drawCircle(bubble.x, bubble.y, bubble.size, bubblePaint)
        }
    }

    private fun drawWaterDrops(canvas: Canvas) {
        waterDrops.forEach { drop ->
            drop.update()
            dropPaint.alpha = (drop.alpha * 255).toInt()

            val path = Path()
            path.moveTo(drop.x, drop.y)
            path.cubicTo(
                drop.x - drop.size, drop.y - drop.size * 1.5f,
                drop.x + drop.size, drop.y - drop.size * 1.5f,
                drop.x, drop.y
            )
            path.lineTo(drop.x, drop.y + drop.size * 2)
            path.close()

            canvas.drawPath(path, dropPaint)
        }

        waterDrops.removeAll { it.alpha <= 0f }
    }

    private fun drawBottomInfo(canvas: Canvas, centerX: Float, startY: Float) {
        val consumed = currentAmount
        val remaining = dailyGoalAmount - consumed


        val leftX = centerX - 130f
        canvas.drawText("Осталось", leftX, startY, labelPaint)
        canvas.drawText("$remaining мл", leftX, startY + 55f, valuePaint)


        val rightX = centerX + 130f
        canvas.drawText("Цель", rightX, startY, labelPaint)
        canvas.drawText("$dailyGoalAmount мл", rightX, startY + 55f, valuePaint)
    }

    fun setProgress(newProgress: Float, animated: Boolean = true) {
        val target = newProgress.coerceIn(0f, 100f)
        currentAmount = ((target / 100f) * dailyGoalAmount).toInt()

        if (animated) {
           
            if (target > progress) {
                addWaterDrops()
            }

            // Анимация процента
            ValueAnimator.ofFloat(progress, target).apply {
                duration = 2500 
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    progress = animation.animatedValue as Float
                    invalidate()
                }
                start()
            }

            targetWaterLevel = target
        } else {
            progress = target
            currentWaterLevel = target
            targetWaterLevel = target
            invalidate()
        }
    }

    fun setDailyGoal(goal: Int) {
        dailyGoalAmount = goal
        invalidate()
    }

    private fun addWaterDrops() {
        val centerX = width / 2f
        val centerY = height * 0.35f

        repeat(5) {
            waterDrops.add(
                WaterDrop(
                    centerX + Random.nextFloat() * 100 - 50,
                    centerY - 50f,
                    Random.nextFloat() * 8 + 5f
                )
            )
        }
    }

    private fun startWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 8000 
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animation ->
                wavePhase = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun startParticleAnimation() {
       
        val centerX = width / 2f
        val centerY = height * 0.35f
        val radius = minOf(width, height) * 0.32f

        post {
            repeat(maxBubbles) {
                bubbles.add(Bubble(centerX, centerY, radius, 0f))
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
    }

    
    private data class WaterDrop(
        var x: Float,
        var y: Float,
        val size: Float,
        var velocityY: Float = 0f,
        var alpha: Float = 1f
    ) {
        fun update() {
            velocityY += 0.4f 
            y += velocityY
            alpha -= 0.01f 
        }
    }


    private data class Bubble(
        var x: Float,
        var y: Float,
        val size: Float,
        var alpha: Float,
        var velocityY: Float = Random.nextFloat() * 1 + 0.5f, 
        var wobble: Float = Random.nextFloat() * 1f 
    ) {
        private var wobblePhase = Random.nextFloat() * Math.PI.toFloat() * 2

        fun update() {
            y -= velocityY
            wobblePhase += 0.05f 
            x += sin(wobblePhase.toDouble()).toFloat() * wobble
            alpha = 0.6f
        }

        fun reset(centerX: Float, centerY: Float, radius: Float, waterLevel: Float) {
            x = centerX + (Random.nextFloat() - 0.5f) * radius * 1.5f
            y = centerY + radius - 20f
            velocityY = Random.nextFloat() * 1 + 0.5f 
            wobble = Random.nextFloat() * 1f
        }
    }
}