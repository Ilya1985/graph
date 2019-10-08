package ru.tatarchuk.graph.surface

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import ru.tatarchuk.R
import ru.tatarchuk.graph.Element
import ru.tatarchuk.util.MetricsConverter

class SurfaceGraph : SurfaceView, SurfaceHolder.Callback {

    companion object {
        private val TAG = "<>${SurfaceGraph::class.java.simpleName}"
    }

    private val coordinateSystem = CoordinateSystem(
        context.resources.getColor(R.color.colorPrimary),
        context.resources.getColor(R.color.colorPrimaryLight),
        MetricsConverter.spToPx(10f, context).toFloat()
    ).apply { setDuration(500L) }

    private val data =
        arrayListOf(
            PathManager(
                context.resources.getColor(R.color.colorGreen),
                context.resources.getColor(R.color.colorDivider)
            ),
            PathManager(
                context.resources.getColor(R.color.colorYellow),
                context.resources.getColor(R.color.colorDivider)
            ),
            PathManager(
                context.resources.getColor(R.color.colorPurple),
                context.resources.getColor(R.color.colorDivider)
            )
        )

    private lateinit var drawThread: DrawThread

    constructor(context: Context?) : super(context) {
        initialization()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialization()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialization()
    }

    private fun initialization() {
        holder.addCallback(this)
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        drawThread = DrawThread().apply {
            isRunning = true
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        coordinateSystem.setWidth(p2)
        coordinateSystem.setHeight(p3)

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        var retry = true
        drawThread.isRunning = false //останавливает процесс
        while (retry) {
            try {
                drawThread.join()
                retry = false
            } catch (e: InterruptedException) {

            }
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            it.apply {
                drawColor(context.resources.getColor(android.R.color.background_light))
                coordinateSystem.draw(it)
                data.forEach { path -> path.drawPath(coordinateSystem.coordX, coordinateSystem.coordY, it) }
                //rates.forEach { path -> drawPath(path, it) }
            }
        }
    }

    inner class DrawThread : Thread() {

        var isRunning = false
        var start = 0L

        override fun run() {
            super.run()
            var canvas: Canvas? = null
            while (isRunning) {
                try {
                    start = System.currentTimeMillis()
                    // canvas = holder.lockCanvas()

                    canvas =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) holder.lockHardwareCanvas() else holder.lockCanvas()
                    //.i(TAG, "canvas.isHardwareAccelerated = ${canvas.isHardwareAccelerated}")
                    synchronized(holder) {
                        canvas?.let { draw(it) }
                    }
                } finally {
                    //Log.i(TAG, "elapsed time: ${System.currentTimeMillis() - start} ms")
                    canvas?.let { holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    fun setCurrency(value: MutableList<Element>, position: Position) {
        //Log.i(TAG, "set to ${position.num} size: ${value.size}")
        data[position.num].elements = value
        determineSizeOfCoordinateSystem()
    }

    fun deleteCurrency(position: Position) {
        data[position.num].elements = mutableListOf()
        determineSizeOfCoordinateSystem()
    }

    private fun setMaxY() {
        //Log.i(TAG, "setMaxY: ${rates.maxBy { it.getGetMax() ?: 0f }?.getGetMax() ?: 0f}")
        coordinateSystem.setMaxY(data.maxBy { it.getMax() ?: 0f }?.getMax() ?: 0f)
    }

    private fun setMinY() {
        //Log.i(TAG, "setMinY: ${rates.minBy { it.getGetMin() }?.getGetMin() ?: 0f}")
        coordinateSystem.setMinY(data.minBy { it.getMin() }?.getMin() ?: Float.MAX_VALUE)
    }

    private fun setMaxX() {
        //Log.i(TAG, "setMaxX: ${rates.maxBy { it.getSize() }?.getSize() ?: 0f}")
        coordinateSystem.setMaxX(data.maxBy { it.getSize() }?.getSize() ?: 0f)
    }

    private fun determineSizeOfCoordinateSystem() {
        setMaxY()
        setMinY()
        setMaxX()
    }

    enum class Position(val num: Int) {
        First(0), Second(1), Third(2);
    }
}