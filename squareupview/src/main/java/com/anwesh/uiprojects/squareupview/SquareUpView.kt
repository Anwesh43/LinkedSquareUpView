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
val backColor : Int = Color.parseColor("#BDBDBD")

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
    drawCircle(0f, 0f, size * sc2, paint)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                    Thread.sleep(delay)
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

    data class SUPNode(var i : Int, val state : State = State()) {

        private var next : SUPNode? = null
        private var prev : SUPNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = SUPNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSUPNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SUPNode {
            var curr : SUPNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class SquareUp(var i : Int) {

        private val root : SUPNode = SUPNode(0)
        private var curr : SUPNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : SquareUpView) {

        private val animator : Animator = Animator(view)
        private val su : SquareUp = SquareUp(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            su.draw(canvas, paint)
            animator.animate {
                su.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            su.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : SquareUpView {
            val view : SquareUpView = SquareUpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}