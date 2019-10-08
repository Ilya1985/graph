package ru.tatarchuk.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import ru.tatarchuk.R
import ru.tatarchuk.graph.GraphElement
import ru.tatarchuk.graph.view.CustomView
import ru.tatarchuk.graph.zone_view.Cs
import ru.tatarchuk.graph.zone_view.Graph
import ru.tatarchuk.rest.api.DynamicApi
import ru.tatarchuk.rest.client.CentralBankClient
import ru.tatarchuk.rest.request.DynamicRequest
import ru.tatarchuk.rest.response.DynamicValCurs
import ru.tatarchuk.util.AppDateFormatter

class MainActivity : AppCompatActivity() {

    companion object {

        private val TAG = "<>${MainActivity::class.java.simpleName}"

        private const val EUR = "R01239"

        private const val USD = "R01235"

        private const val GBP = "R01035"

    }

    private lateinit var customView: CustomView

    private lateinit var graph: Graph

    private lateinit var removeFirst: Button

    private lateinit var removeSecond: Button

    private lateinit var removeThird: Button

    /**-----------------------------------------*/

    private lateinit var progressBar: ProgressBar

    private lateinit var eur: Button
    private lateinit var usd: Button
    private lateinit var gbp: Button

    private val api = CentralBankClient().createService(DynamicApi::class.java)

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  customView = findViewById(R.id.customView)
        //customView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        graph = findViewById(R.id.graph)
        //graph.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        removeFirst = findViewById(R.id.del_eur)
        removeFirst.setOnClickListener { graph.delCurrency(Cs.Colors.GREEN) }

        removeSecond = findViewById(R.id.del_usd)
        removeSecond.setOnClickListener { graph.delCurrency(Cs.Colors.ORANGE) }

        removeThird = findViewById(R.id.del_gbp)
        removeThird.setOnClickListener { graph.delCurrency(Cs.Colors.PURPLE) }

        progressBar = findViewById(R.id.progressBar)

        eur = findViewById(R.id.eur)
        eur.setOnClickListener { loadDynamic(2019, Cs.Colors.GREEN, EUR) }

        usd = findViewById(R.id.usd)
        usd.setOnClickListener { loadDynamic(2019, Cs.Colors.ORANGE, USD) }

        gbp = findViewById(R.id.gbp)
        gbp.setOnClickListener { loadDynamic(2019, Cs.Colors.PURPLE, GBP) }

    }

    private fun loadDynamic(year: Int, color: Cs.Colors, currency: String) {

        val dateTo = LocalDate.of(year, Month.DECEMBER, 31)
        val dateFrom = LocalDate.of(year, Month.JANUARY, 1)

        disposable?.dispose()
        disposable = api
            .getDynamic(DynamicRequest(currency, dateFrom, dateTo).toMap())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                Log.i(TAG, "start = ${LocalTime.now().second}")
                Handler(Looper.getMainLooper()).post { progressBar.visibility = View.VISIBLE }
            }
            .doFinally {
                Log.i(TAG, "finish = ${LocalTime.now().second}")
                Handler(Looper.getMainLooper()).post { progressBar.visibility = View.GONE }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { graph.setCurrency(dynamicValCursToGraph(it), color) },
                { Log.i(TAG, it.message, it) })
    }

    private fun dynamicValCursToGraph(valCurs: DynamicValCurs): GraphElement =
        GraphElement(valCurs.id, valCurs.id, mutableListOf(), mutableListOf()).apply {
            valCurs.records.forEach {
                rates.add(it.value.replace(',', '.').toFloat())
                dates.add(LocalDate.parse(it.date, AppDateFormatter.responseDateFormat))
            }
        }
}
