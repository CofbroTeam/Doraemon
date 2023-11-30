package com.cofbro.qian.utils

import com.amap.api.services.help.Tip

object TipUtils {

    /*
    为适配部分机型不适配getParcelableExtra，API33可以使用，使用将Tip转换为ArrayString
    {
      "name":,
      "address",
      "poiID"
      "latitude"
      "longitude"
    }
     */
    fun TipParseToArray(tip:Tip):ArrayList<String>{
        val tipArray:ArrayList<String> = ArrayList()
        tipArray.add(tip.name) //0
        tipArray.add(tip.address) //1
        tipArray.add(tip.poiID) //2
        tipArray.add(tip.point.latitude.toString())  //3
        tipArray.add(tip.point.longitude.toString())  //4
        tipArray.add(tip.district) //5
        return  tipArray
    }
}
