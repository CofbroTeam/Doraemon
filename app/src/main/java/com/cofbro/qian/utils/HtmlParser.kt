package com.cofbro.qian.utils

import com.alibaba.fastjson.JSONObject
import com.cofbro.qian.mapsetting.util.PreWeb
import org.jsoup.Jsoup

object HtmlParser {
    fun parseHomeworkHTML(html: String): List<JSONObject> {
        val jsonObject = arrayListOf<JSONObject>()
        try {
            val doc = Jsoup.parse(html)
            val bottomList = doc.getElementsByClass("bottomList").getOrNull(0)
            val ul = bottomList?.child(0)
            val li = ul?.children()
            li?.forEach { item ->
                val data = JSONObject()
                val jumpUrl = item.attr("data")
                data["url"] = jumpUrl
                val array = item.text().split(" ")
                data["title"] = array.getOrNull(0) ?: ""
                data["status"] = array.getOrNull(1) ?: ""
                data["deadline"] = array.getOrNull(3) ?: ""
                jsonObject.add(data)
            }
        } catch (_: Exception) {
        }
        return jsonObject
    }

    fun parsePostHomeworkUrl(html: String): JSONObject {
        val data = JSONObject()
        val doc = Jsoup.parse(html)
        val url = doc.getElementById("submitForm")?.attr("action") ?: ""
        val cpi = doc.getElementById("cpi")?.`val`() ?: ""
        val courseId = doc.getElementById("courseId")?.`val`() ?: ""
        val classId = doc.getElementById("classId")?.`val`() ?: ""
        val workId = doc.getElementById("workId")?.`val`() ?: ""
        val answerId = doc.getElementById("answerId")?.`val`() ?: ""
        val standardEnc = doc.getElementById("standardEnc")?.`val`() ?: ""
        val encWork = doc.getElementById("enc_work")?.`val`() ?: ""
        val totalQuestionNum = doc.getElementById("totalQuestionNum")?.`val`() ?: ""
        val workType = doc.getElementsByClass("type_tit")?.getOrNull(0)?.text() ?: ""
        val description = doc.getElementsByClass("mark_name colorDeep fontLabel")?.getOrNull(0)?.text() ?: ""
        val answerwqbid =
            doc.getElementsByClass("padBom50 questionLi fontLabel")?.getOrNull(0)?.attr("data") ?: ""
        data[Constants.Work.WORK_TYPE] = workType
        data[Constants.Work.DESCRIPTION] = description
        data[Constants.Work.SUBMIT_URL] = "https://mooc1.chaoxing.com$url"
        data[Constants.Work.PREFIX_URL] = "courseId=$courseId&classId=$classId&knowledgeid=0&cpi=$cpi&workRelationId=$workId&workAnswerId=$answerId&jobid=&standardEnc=$standardEnc&enc_work=$encWork&totalQuestionNum=$totalQuestionNum&pyFlag=3&answerwqbid=$answerwqbid%2C&mooc2=1&randomOptions=false&answertype$answerwqbid=4&answer$answerwqbid="
        return data
    }


    fun findEnc(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.getElementById("workEnc")?.`val`() ?: ""
    }

    fun parseToUsername(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.getElementsByClass("user-con")?.text() ?: "未查询到姓名"
    }

    fun parseToSignCode(html: String): String {
        val doc = Jsoup.parse(html)
        return doc.getElementById("signCode")?.`val`() ?: ""
    }
    fun parsePreSignWebGet(it:String): PreWeb {
        val html = Jsoup.parse(it)
        val locationText = html.getElementById("locationText")?.`val`()
        val latitude = html.getElementById("locationLatitude")?.`val`()
        val longitude = html.getElementById("locationLongitude")?.`val`()
        val statusContent =
            html.getElementsByClass("zsign_success zsign_hook").select(">h1").text()
        return PreWeb(html, locationText, latitude, longitude, statusContent)
    }

}
