package com.oytask.app.ui.adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.oytask.app.R

abstract class SwipeToDeleteCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteBackground = ColorDrawable(ContextCompat.getColor(context, R.color.error))
    private val completeBackground = ColorDrawable(ContextCompat.getColor(context, R.color.success))
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX < 0) {
            deleteBackground.setBounds(
                itemView.right + dX.toInt(), itemView.top,
                itemView.right, itemView.bottom
            )
            deleteBackground.draw(c)

            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 36f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            c.drawText(
                "Eliminar",
                itemView.right - 120f,
                itemView.top + (itemView.bottom - itemView.top) / 2f + 12f,
                textPaint
            )
        } else if (dX > 0) {
            completeBackground.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt(), itemView.bottom
            )
            completeBackground.draw(c)

            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 36f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            c.drawText(
                "✓ Completar",
                itemView.left + 160f,
                itemView.top + (itemView.bottom - itemView.top) / 2f + 12f,
                textPaint
            )
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }
}
