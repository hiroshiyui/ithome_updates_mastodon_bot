package ithome_updates_mastodon_bot

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

class RssFeedsFetcher {
    private val rssFeedsUrl = "https://www.ithome.com.tw/rss"
    val client = JettyClient()
    private val request: Request = Request(Method.GET, rssFeedsUrl)

    fun getRssBody(): String {
        return client(request).bodyString()
    }

    fun getRssFeedsItems(): NodeList {
        val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val inputSource: InputSource = InputSource(StringReader(getRssBody()))
        val document = documentBuilder.parse(inputSource)
        document.documentElement.normalize()
        return document.getElementsByTagName("item")
    }
}