package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource

var mapView: MapView? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sources = getSources().map { (sourceId, sourceRaw) -> getFeatureCollection(sourceId, sourceRaw) }
        val image = BitmapFactory.decodeStream(assets.open("img/carrow-10-#000000.png"))?.copy(Bitmap.Config.ARGB_8888, false)
        mapView = findViewById(R.id.mapView)
        val styleJson = resources.openRawResource(R.raw.style).bufferedReader().use { it.readText() }

        mapView?.getMapboxMap()?.loadStyleJson(styleJson) {
            sources.forEach { (id, featureCollection) ->
                it.addSource(geoJsonSource(id) {
                    featureCollection(featureCollection)
                })
            }
            it.addImage("carrow-10-#000000.png", image!!)
        }
    }

    private fun getSources(): Map<String, Int> = mapOf(
        "maatvoering-lijn-endpoint" to R.raw.maatvoering_lijn_endpoint,
        "maatvoering-lijn-startpoint" to R.raw.maatvoering_lijn_startpoint,
        "maatvoering-lijn" to R.raw.maatvoering_lijn,
        "maatvoering-punt" to R.raw.maatvoering_punt,
    )

    private fun getFeatureCollection(sourceId: String, sourceRaw: Int): Pair<String, FeatureCollection> {
        val json = resources.openRawResource(sourceRaw).bufferedReader().use { it.readText() }
        val collection = FeatureCollection.fromJson(json)
        return sourceId to collection
    }
}
