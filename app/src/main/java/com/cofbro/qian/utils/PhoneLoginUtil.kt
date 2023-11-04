package com.cofbro.qian.utils

import org.apache.commons.codec.binary.Hex
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec


object PhoneLoginUtil {
    fun getStrMD5(str: String?): String? {
        return if (str != null) {
            try {
                val messageDigest = MessageDigest.getInstance("MD5")
                messageDigest.update(str.toByteArray())
                val digest = messageDigest.digest()
                val sb = StringBuilder()
                for (b in digest) {
                    sb.append(String.format("%02x", b))
                }
                sb.toString()
            } catch (unused: Exception) {
                null
            }
        } else null
    }

    /**
     * 超星logininfo加密
     *
     * @param key  密钥 nmnua8WZ8YSgUUirbxxYgaZUCxBxGfAH
     * @param data 数据 json（uname,code）
     * @return 加密后的数据
     * @throws Exception 异常
     */
    @Throws(java.lang.Exception::class)
    fun chaoXingHexCipher(key: String = "nmnua8WZ8YSgUUirbxxYgaZUCxBxGfAH", username: String, code: String): String? {
        val json = "{\"uname\":\"$username\",\"code\":\"$code\"}"
        val generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(
            DESKeySpec(
                key.toByteArray(
                    StandardCharsets.UTF_8
                )
            )
        )
        val cipher = Cipher.getInstance("DES")
        cipher.init(1, generateSecret)
        return Hex.encodeHexString(cipher.doFinal(json.toByteArray()))
    }

    fun getLoginBody(loginInfo: String, isSMS: Boolean): String {
        return "logininfo=" + loginInfo + (if (isSMS) "&countrycode=86" else "") + "&loginType=" + (if (isSMS) 2 else 1) + "&roleSelect=true"
    }

    fun getSendSMSBody(phone: String): String {
        return "to=$phone" + "&countrycode=86&time=" + System.currentTimeMillis() + "&enc=" + getStrMD5(
            phone + "jsDyctOCnay7uotq" + System.currentTimeMillis()
        )
    }


}