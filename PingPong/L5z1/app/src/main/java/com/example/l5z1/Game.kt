package com.example.l5z1

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random

class Game(private val gameWidth: Int, private val gameHeight: Int, private val gameView : GameView) {

    private var leftPoints = 0
    private var rightPoints = 0

    private val paddleWidth = 50f
    private val paddleHeight = 300f
    private val paddleLeftPositionX = 10f
    private var paddleLeftPositionY = gameHeight / 2f
    private val paddleRightPositionX = gameWidth.toFloat() - 10f
    private var paddleRightPositionY = gameWidth / 2f

    private val ballSize = 50f
    private var ballDeltaX = 15f
    private var ballDeltaY = 15f
    private var ballPositionX = gameHeight / 2f
    private var ballPositionY = gameHeight / 2f

    private val textPaint = TextPaint()
    private val textPaintPositionX = gameWidth / 2f
    private val textPaintPositionY = gameHeight / 2f

    init {
        textPaint.color = Color.WHITE
        textPaint.textSize = 300f
        textPaint.alpha = 100
        textPaint.textAlign = Paint.Align.CENTER
    }

    fun draw(canvas: Canvas) {
        canvas.drawText("$leftPoints : $rightPoints",
            textPaintPositionX, textPaintPositionY, textPaint)
        canvas.drawOval(ballPositionX, ballPositionY,
            ballPositionX + ballSize, ballPositionY + ballSize,
            Paint().also { it.setARGB(255, 255, 255, 255) })
        canvas.drawRect(paddleLeftPositionX, paddleLeftPositionY,
            paddleLeftPositionX + paddleWidth, paddleLeftPositionY + paddleHeight,
            Paint().also { it.setARGB(255, 255, 255, 255) })
        canvas.drawRect(paddleRightPositionX - paddleWidth, paddleRightPositionY,
            paddleRightPositionX, paddleRightPositionY + paddleHeight,
            Paint().also { it.setARGB(255, 255, 255, 255) })
    }

    fun movePaddle(newX: Float, newY: Float) {
        if (paddleLeftPositionX <= newX && newX <= paddleWidth + paddleLeftPositionX &&
            paddleLeftPositionY <= newY && newY <= paddleLeftPositionY + paddleHeight) {
                paddleLeftPositionY = newY - paddleHeight / 2

        } else if (paddleRightPositionX - paddleWidth <= newX && newX <= paddleRightPositionX &&
            paddleRightPositionY <= newY && newY <= paddleRightPositionY + paddleHeight) {
                paddleRightPositionY = newY - paddleHeight / 2
        }
    }

    private fun moveBall() {
        ballPositionX += ballDeltaX
        ballPositionY += ballDeltaY
        checkDirection()
    }

    private fun checkDirection() {
        if (ballPositionX <= 0f) {
            rightPoints += 1
            reset()
        } else if (ballPositionX + ballSize >= gameWidth.toFloat()) {
            leftPoints += 1
            reset()
        } else if (ballPositionY <= 0f || ballPositionY + ballSize >= gameHeight.toFloat()) {
            ballDeltaY *= -1
        } else if (ballPositionX <= paddleLeftPositionX + paddleWidth) {
            if (ballPositionY + ballSize == paddleLeftPositionY &&
                ballPositionY == paddleLeftPositionY + paddleHeight) {
                ballDeltaY *= -1
            } else if (ballPositionY + ballSize > paddleLeftPositionY &&
                ballPositionY < paddleLeftPositionY + paddleHeight) {
                ballDeltaX *= -1
            }
        } else if (ballPositionX + ballSize >= paddleRightPositionX - paddleWidth) {
            if (ballPositionY + ballSize == paddleRightPositionY &&
                ballPositionY == paddleRightPositionY + paddleHeight) {
                ballDeltaY *= -1
            } else if (ballPositionY + ballSize > paddleRightPositionY &&
                ballPositionY < paddleRightPositionY + paddleHeight) {
                ballDeltaX *= -1
            }
        }
    }

    fun update() {
        moveBall()
        GlobalScope.launch {
            if (leftPoints == 10 || rightPoints == 10) {
                GlobalScope.launch {
                    DatabaseConnector.insertGame(GameStatus(0, leftPoints, rightPoints))
                }
                gameView.endGame()
            }
        }
    }

    private fun reset() {
        ballPositionX = gameWidth / 2f
        ballPositionY = gameHeight / 2f
        ballDeltaX = -(15 + 5 * Random.nextFloat()) * (-1.0)
            .pow(Random.nextInt(2)).toFloat()
        ballDeltaY = (15 + 5 * Random.nextFloat()) * (-1.0)
            .pow(Random.nextInt(2)).toFloat()
    }
}