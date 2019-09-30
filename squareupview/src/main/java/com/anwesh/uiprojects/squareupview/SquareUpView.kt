package com.anwesh.uiprojects.squareupview

/**
 * Created by anweshmishra on 30/09/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color

val nodes : Int = 5
val parts : Int = 3
val scGap : Float = 0.01f
val delay : Long = 30
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val squareColor : Int = Color.parseColor("#2196F3")
val circleColor : Int = Color.parseColor("#f44336")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawSquareUp(sc : Float, size : Float, h : Float, paint : Paint) {
    val sc1 : Float = sc.divideScale(0, parts)
    val sc2 : Float = sc.divideScale(1, parts)
    val sc3 : Float = sc.divideScale(2, parts)
    val y : Float = size + (h - size) * (sc1 - sc3)
    save()
    translate(size, size + (h - size) * (sc1 - sc3))
    paint.color = squareColor
    drawRect(RectF(-size, -size, size, size), paint)
    paint.color = circleColor
    drawCircle(0f, 0f, size * sc, paint)
    restore()
    paint.color = squareColor
    drawLine(size, 0f, size, y - size, paint)
}

fun Canvas.drawSUPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), 0f)
    drawSquareUp(scale, size, h / 2, paint)
    restore()
}

class SquareUpView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}