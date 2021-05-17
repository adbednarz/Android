package com.example.l5z1

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView)
    : Thread() {

    private var running: Boolean = false
    private var targetFPS = 30

    override fun run() {
        var startTime : Long
        var timeMillis : Long
        var waitTime: Long
        var targetTime = (1000/targetFPS).toLong()

        while (running) {
            startTime = System.nanoTime()
            var canvas = surfaceHolder.lockCanvas()
            gameView.update()
            gameView.draw(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)
            timeMillis = (System.nanoTime() - startTime)/ 1000000
            waitTime = targetTime - timeMillis

            if (waitTime >= 0)
                sleep(waitTime)
        }
    }

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }
}