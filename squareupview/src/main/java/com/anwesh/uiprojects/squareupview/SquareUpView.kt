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
