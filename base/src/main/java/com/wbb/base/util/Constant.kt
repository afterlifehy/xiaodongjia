package com.wbb.base.util

import com.wbb.base.pf.PreferencesHelp

/**
 * Created by hy on 2021/1/20.
 */
object Constant {
    const val crtStr =
        "-----BEGIN CERTIFICATE-----\n" +
                "MIIFkjCCBHqgAwIBAgIQDjCCHd5PokpzpFDCUFib6jANBgkqhkiG9w0BAQsFADBy\n" +
                "MQswCQYDVQQGEwJDTjElMCMGA1UEChMcVHJ1c3RBc2lhIFRlY2hub2xvZ2llcywg\n" +
                "SW5jLjEdMBsGA1UECxMURG9tYWluIFZhbGlkYXRlZCBTU0wxHTAbBgNVBAMTFFRy\n" +
                "dXN0QXNpYSBUTFMgUlNBIENBMB4XDTIxMDMyMzAwMDAwMFoXDTIyMDMyMjIzNTk1\n" +
                "OVowGzEZMBcGA1UEAxMQYXBpLmppbmlsaWZlLmNvbTCCASIwDQYJKoZIhvcNAQEB\n" +
                "BQADggEPADCCAQoCggEBAIjAJlGEfoLH5GD6jTGw9EgjhZxdrATnRrfVyWNUJdgc\n" +
                "PLGAGgd1KXIuDgtCwzjoS1rlZSZFY+m/ZN+A+XqqOn0N0x02uD6/ojpeC5NLywjj\n" +
                "DW89AjLPR50lE5rhjn+QD1JKHHVwXNDKVxcU14D+7iushdmNMu+CEDiP+gQ4vJvK\n" +
                "uXKeypNxINvaF0fCfiKOSw41iFqynreWA51NFGPXPoumLK+2O/yKFlXXTMc+lNzh\n" +
                "FkSuu/BIKJIdL1MtD2INC5tM3VSCZgxLNW11ydeSM7DTojkYGJBn+LNPHGxPali2\n" +
                "Qp96GmJEMMwE0lL4V2RXQn0+TZ5udwQ9XHmgn6jxpXMCAwEAAaOCAnkwggJ1MB8G\n" +
                "A1UdIwQYMBaAFH/TmfOgRw4xAFZWIo63zJ7dygGKMB0GA1UdDgQWBBStABnu5st4\n" +
                "ET2nbROfsazy4UNR7jAbBgNVHREEFDASghBhcGkuamluaWxpZmUuY29tMA4GA1Ud\n" +
                "DwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwPgYDVR0g\n" +
                "BDcwNTAzBgZngQwBAgEwKTAnBggrBgEFBQcCARYbaHR0cDovL3d3dy5kaWdpY2Vy\n" +
                "dC5jb20vQ1BTMIGSBggrBgEFBQcBAQSBhTCBgjA0BggrBgEFBQcwAYYoaHR0cDov\n" +
                "L3N0YXR1c2UuZGlnaXRhbGNlcnR2YWxpZGF0aW9uLmNvbTBKBggrBgEFBQcwAoY+\n" +
                "aHR0cDovL2NhY2VydHMuZGlnaXRhbGNlcnR2YWxpZGF0aW9uLmNvbS9UcnVzdEFz\n" +
                "aWFUTFNSU0FDQS5jcnQwCQYDVR0TBAIwADCCAQUGCisGAQQB1nkCBAIEgfYEgfMA\n" +
                "8QB2AEalVet1+pEgMLWiiWn0830RLEF0vv1JuIWr8vxw/m1HAAABeF4E380AAAQD\n" +
                "AEcwRQIhALRRDoSf5Mcw3OBw+0EL7Vjxp2zNvWYWLWDpYBVg/oI0AiA2m8VzZ5xh\n" +
                "7HlryjgXIpSUvwYEHwQabqKP9x0SATgN0wB3ACJFRQdZVSRWlj+hL/H3bYbgIyZj\n" +
                "rcBLf13Gg1xu4g8CAAABeF4E3+0AAAQDAEgwRgIhALx3rPF0+qT4TdHiy6t2qkXH\n" +
                "XlPc5coVIIX8/lfR2kaNAiEAqziNVW4y4nKvUA++T4fw6ztiAEy4923H9Lvb5i2V\n" +
                "088wDQYJKoZIhvcNAQELBQADggEBABI5puMlYLEWOOK/QM2xdJ3eteI45qgM8yPC\n" +
                "rOoHlYr2kCJAFwGEcQD1NhK5Y5LFYnE3+c47dR0Tnm4zbQDwthb809U/wZmV3kB8\n" +
                "s87ut6nUDqaWv+q5JZqzATEd1rpbAJAJtNHUONSlN6eNhkQSwIxtnK65ermiFD/I\n" +
                "X9pfraLoLQ4MSHzZMmEoYRQ2XeBDcZSVwYdqbDAHGyX/JwP67Bsri9LjolRw4XWB\n" +
                "VREndpYCi8zWJ9Lm6xQF3rFXVv4FVSpQQ1GestFpwqUhsKqrOPDRaurT2rjKQw3V\n" +
                "8lCJE3XWr8CJdLBSpaHdPMKdlQ/Crky1SSZKKfs+zIeCsJGh8KI=\n" +
                "-----END CERTIFICATE-----"

    const val h5CrtStr = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFjzCCBHegAwIBAgIQBRHIsEkfotH3YfYEV8YpjTANBgkqhkiG9w0BAQsFADBy\n" +
            "MQswCQYDVQQGEwJDTjElMCMGA1UEChMcVHJ1c3RBc2lhIFRlY2hub2xvZ2llcywg\n" +
            "SW5jLjEdMBsGA1UECxMURG9tYWluIFZhbGlkYXRlZCBTU0wxHTAbBgNVBAMTFFRy\n" +
            "dXN0QXNpYSBUTFMgUlNBIENBMB4XDTIxMDMyMzAwMDAwMFoXDTIyMDMyMjIzNTk1\n" +
            "OVowGzEZMBcGA1UEAxMQYXBwLmppbmlsaWZlLmNvbTCCASIwDQYJKoZIhvcNAQEB\n" +
            "BQADggEPADCCAQoCggEBAPm5Bcy9Sg4x6zz7t0g3aZjMtT/V3Lzex78DvJcpFeKC\n" +
            "qPwnj2E9jH9Yq4KG1dsTP6pOdmmicIaJru3r7InsB+d5tx+6AxOoUqO1QO1y5XJ2\n" +
            "TV+sH0n0WMufjJPJZbcKiPJby1qxO3eMKOp/d8dkdWB1nWsGdhpJg9ElBIK3A110\n" +
            "0i1RrpP+N8Ftt+HG/2X7kP3UeHmh9cZ22qJYZSKoaCemg1MYOgVUydI2TpXZJGNn\n" +
            "eg5MCTr7I1xXVAMpIrJM1zxS/cb3pYCw4Tj7W20m6av3tk00VeQExECpFmFzmkvu\n" +
            "Kg+gXxarUX/ki5Xu7P8jhLwegpEPNHVAtNMy6zXYjo0CAwEAAaOCAnYwggJyMB8G\n" +
            "A1UdIwQYMBaAFH/TmfOgRw4xAFZWIo63zJ7dygGKMB0GA1UdDgQWBBTyFfJrjpVT\n" +
            "Zd3wuDC+2lrQwxzAIzAbBgNVHREEFDASghBhcHAuamluaWxpZmUuY29tMA4GA1Ud\n" +
            "DwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwPgYDVR0g\n" +
            "BDcwNTAzBgZngQwBAgEwKTAnBggrBgEFBQcCARYbaHR0cDovL3d3dy5kaWdpY2Vy\n" +
            "dC5jb20vQ1BTMIGSBggrBgEFBQcBAQSBhTCBgjA0BggrBgEFBQcwAYYoaHR0cDov\n" +
            "L3N0YXR1c2UuZGlnaXRhbGNlcnR2YWxpZGF0aW9uLmNvbTBKBggrBgEFBQcwAoY+\n" +
            "aHR0cDovL2NhY2VydHMuZGlnaXRhbGNlcnR2YWxpZGF0aW9uLmNvbS9UcnVzdEFz\n" +
            "aWFUTFNSU0FDQS5jcnQwCQYDVR0TBAIwADCCAQIGCisGAQQB1nkCBAIEgfMEgfAA\n" +
            "7gB1AEalVet1+pEgMLWiiWn0830RLEF0vv1JuIWr8vxw/m1HAAABeF4NEaoAAAQD\n" +
            "AEYwRAIgVt0GKmA+MxAxQ1z2iAJCXpFIxSZJYvcur5R/v0tDUEgCIDg5+Mh9jAg1\n" +
            "5Y4y5W3nc4iXqCxAsvtT0XbSiph/TzdbAHUAIkVFB1lVJFaWP6Ev8fdthuAjJmOt\n" +
            "wEt/XcaDXG7iDwIAAAF4Xg0RzwAABAMARjBEAiAR2Mje1Cpsde8BrGsAynmvEefR\n" +
            "lD9JJwYN7X3nid4AWwIgOckHb6lHc6pR3LQqEgOY67OGC9AxA3P028NPu9gbUqww\n" +
            "DQYJKoZIhvcNAQELBQADggEBACT45mtOQKYwAiTQP3ANb9rA268Al4V8GWRyoz7B\n" +
            "UD8ZS5ffWevWYos6PUK3FAgIMst2aQv77WvIdQRPp2+eMX4Af0o8oA0E/o6yT3Rc\n" +
            "WTfVODerqOvhT3azzXN/dpdr+7g91QJ5Wkn9AOSPqWzYz9wX7Sa0qRafyppYdHbz\n" +
            "t8thTWT7+DJ08/pHfA4Q773XT+t0GvkVYp/ydMopVmqmJrf9LmrtQCxeAj76FaMi\n" +
            "Lee7rJ6fNZj7c/AQPQqGonD7+xmSNWTfdfcJWD5G2e2o82mM7d28rKn16P4YVBqk\n" +
            "1E3zoH2E+6ODvPjsH5cbUDM7uXFD+e7r3p8dCRmKA4BJDkY=\n" +
            "-----END CERTIFICATE-----"
    const val WX_APPLET_ID = "gh_8b33cbae7513"
    const val WX_APP_ID = "wx02dcd66ba6f2b82d"

    const val ISDEV = "isDev"
    const val UUID = "uuid"
    const val TIMESTAMP = "timeStamp"
    const val TIMEOUT = 100L
    const val USERID = "m_id"
    const val TICKETCODE = "ticketCode"
    const val VALUATIONCURRENCY = "valuationCurrency"
    const val LANGUAGE = "language"
    const val WBID = "wbid" //哪个版本
    const val SYSTEM_MODEL = "systemModel"
    const val SIGN = "sign"
    const val TIMESTEP = "timestep"
    const val XAUTH_TOKEN = "x-auth-token"
    const val INVITE_CODE = "inviteCode"

    //保存最新消息的ID
    const val NEW_MSG_ID = "new_msg_id"

    /**
     * 用户token
     */
    const val USER_TOKEN = "X-TOKEN"

    const val USER_TERMINAL = "X-TERMINAL"

    //开发
    const val h5_dev = "http://dev.app.jinilife.com/"
//    const val h5_dev = "http://192.168.110.213:8000/"

    //测试
    const val h5_test = "http://test.app.jinilife.com/"

    //预发
    const val h5_pre = "http://pre.app.jinilife.com/"

    //正式
    const val h5_formal = "https://app.jinilife.com/"

    //用户协议
    var USER_POLICY_URL = getH5ServerPath() + "pages/small/useragreement"

    //隐私协议
    var USER_YS_URL = getH5ServerPath() + "pages/small/agreement"

    //活动详情
    var ACTIVITY_DETAIL = getH5ServerPath() + "pages/site/life/detail"

    //个人设置
    var PERSONAL_SETTING = getH5ServerPath() + "pages/user/info/info"

    //消息
    var NOTICATION = getH5ServerPath() + "pages/site/message/list"

    //商户类型列表
    var MERCHANT_TYPE_LIST = getH5ServerPath() + "pages/site/shop/list"

    //商户详情
    var MERCHANT_DETAIL = getH5ServerPath() + "pages/site/shop/detail"

    //充值
    var PLUS_RECHARGE = getH5ServerPath() + "pages/user/others/plusrecharge"

    //卡包详情
    var OTHER_MEMBER = getH5ServerPath() + "pages/user/others/member"

    //东家plus
    var DONG_PLUS = getH5ServerPath() + "pages/small/plus/plushome"

    //poster分享地址PreferencesHelp.getDevValue()
    var POST_SHARE = getH5ServerPath() + "pages/download/share"

    fun getH5ServerPath(): String {
        return AppFlag.h5PathMap[PreferencesHelp.getDevValue()].toString()
    }
}