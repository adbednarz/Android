package com.example.l5z1

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("ViewConstructor")
class GameView(cont: AppCompatActivity) : SurfaceView(cont), SurfaceHolder.Callback {

    private val thread: GameThread
    private lateinit var game: Game
    private var activity : AppCompatActivity

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        activity = cont
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        game = Game(width, height, this)
        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.setRunning(false)
        thread.join()
    }

    fun update() {
        game.update()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas == null) return
        game.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            for (i in 0 until event.pointerCount) {
                game.movePaddle(event.getX(i), event.getY(i))
            }
        }
        return true
    }

    fun endGame() {
        thread.setRunning(false)
        this.activity.finish()
    }
}