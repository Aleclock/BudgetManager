/*
package com.example.aleclock.budgetmanager

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View

open class SwipeActionListener(
    val activity : Activity
) : ItemTouchHelper.Callback() {

    var background: RectF = RectF()

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        // Non implementata in quanto non è previsto il drag
        return false
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        // po: viewHolder , p1: direction
        val position = p0.adapterPosition

        val displayMetrics = DisplayMetrics()
        TransactionsFragment.activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels

        if (p1 == ItemTouchHelper.LEFT) {
            Log.d("aaaaa", "Elimina")
        } else if (p1 == ItemTouchHelper.RIGHT){
            Log.d("aaaaa", "Visualizza transizione")
        }
    }


    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            var icon : Bitmap
            var itemView : View = viewHolder.itemView
            var height = itemView.getBottom() - itemView.getTop()
            //var width = height / 3
            var p = Paint()

            val corner = resources.getDimension(R.dimen.corners)

            val displayMetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            var width = displayMetrics.widthPixels

            if (dX > 0) {

                //Drawing for Swife Right

                p.setColor(resources.getColor(R.color.colorPrimary))
                //background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat() + dX/3 ,itemView.bottom.toFloat())
                background = RectF(-corner, itemView.top.toFloat(), dX/3 ,itemView.bottom.toFloat())
                c.drawRoundRect(background, corner, corner, p)

            } else if (dX < 0){

                //Drawing for Swife Left

                p.setColor(resources.getColor(R.color.colorError))
                // TODO il bordo destro è a filo con l'item a differenza dello swipe sinistro
                background = RectF(width.toFloat() + dX/3, itemView.top.toFloat(), width.toFloat() + corner ,itemView.bottom.toFloat())
                c.drawRoundRect(background, corner, corner, p)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder,  dX/3, dY, actionState, isCurrentlyActive)
    }

    data class SwipeAction(val color: Int, val drawable: Drawable, val canDo: (Int) -> Boolean, val action: (Int) -> Unit)


}*/
