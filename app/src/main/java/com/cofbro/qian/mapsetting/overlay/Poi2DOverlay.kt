package com.cofbro.qian.mapsetting.overlay

import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.BitmapDescriptor
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.LatLngBounds
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.PoiItemV2


/**
 * Poi图层类。在高德地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层。
 */
class Poi2DOverlay(amap: AMap?, pois: java.util.ArrayList<PoiItemV2>?) {
    private val mPois: List<PoiItemV2>?
    private val mAMap: AMap?
    private val mPoiMarks: ArrayList<Marker> = ArrayList()

    /**
     * 通过此构造函数创建Poi图层。
     * @param amap 地图对象。
     * @param pois 要在地图上添加的poi。列表中的poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类** [PoiItem](../../../../../../Search/com/amap/api/services/core/PoiItem.html)**。
     * @since V2.1.0
     */
    init {
        mAMap = amap
        mPois = pois
    }

    /**
     * 添加Marker到地图中。
     */
    fun addToMap() {
        try {
            for (i in mPois!!.indices) {
                val marker: Marker = mAMap!!.addMarker(getMarkerOptions(i))
                marker.setObject(i)
                mPoiMarks.add(marker)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 去掉PoiOverlay上所有的Marker。
     */
    fun removeFromMap() {
        for (mark in mPoiMarks) {
            mark.remove()
        }
    }

    /**
     * 移动镜头到当前的视角。
     */
    fun zoomToSpan() {
        try {
            if (mPois != null && mPois.size > 0) {
                if (mAMap == null) return
                if (mPois.size == 1) {
                    mAMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                mPois[0].latLonPoint.latitude,
                                mPois[0].latLonPoint.longitude
                            ), 18f
                        )
                    )
                } else {
                    val bounds: LatLngBounds = latLngBounds
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30))
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private val latLngBounds: LatLngBounds
        private get() {
            val b: LatLngBounds.Builder = LatLngBounds.builder()
            for (i in mPois!!.indices) {
                b.include(
                    LatLng(
                        mPois[i].latLonPoint.latitude,
                        mPois[i].latLonPoint.longitude
                    )
                )
            }
            return b.build()
        }

    private fun getMarkerOptions(index: Int): MarkerOptions {
        return MarkerOptions()
            .position(
                LatLng(
                    mPois!![index].latLonPoint
                        .latitude, mPois[index]
                        .latLonPoint.longitude
                )
            )
            .title(getTitle(index)).snippet(getSnippet(index))
            .icon(getBitmapDescriptor(index))
    }

    /**
     * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @param index 第几个Marker。
     * @return 更换的Marker图片。
     */
    protected fun getBitmapDescriptor(index: Int): BitmapDescriptor? {
        return null
    }

    /**
     * 返回第index的Marker的标题。
     * @param index 第几个Marker。
     * @return marker的标题。
     */
    protected fun getTitle(index: Int): String {
        return mPois!![index].title
    }

    /**
     * 返回第index的Marker的详情。
     * @param index 第几个Marker。
     * @return marker的详情。
     */
    protected fun getSnippet(index: Int): String {
        return mPois!![index].snippet
    }

    /**
     * 从marker中得到poi在list的位置。
     * @param marker 一个标记的对象。
     * @return 返回该marker对应的poi在list的位置。
     * @since V2.1.0
     */
    fun getPoiIndex(marker: Marker?): Int {
        for (i in mPoiMarks.indices) {
            if (mPoiMarks[i].equals(marker)) {
                return i
            }
        }
        return -1
    }

    /**
     * 返回第index的poi的信息。
     * @param index 第几个poi。
     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 **[PoiItem](../../../../../../Search/com/amap/api/services/core/PoiItem.html)**。
     */
    fun getPoiItem(index: Int): PoiItemV2? {
        return if (index < 0 || index >= mPois!!.size) {
            null
        } else mPois[index]
    }
}
