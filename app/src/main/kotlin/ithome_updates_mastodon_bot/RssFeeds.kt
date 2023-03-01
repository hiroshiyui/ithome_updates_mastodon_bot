package ithome_updates_mastodon_bot

import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class RssFeeds(rssFeedsUrl: String) {
    private val client = JettyClient()
    private val request = Request(Method.GET, rssFeedsUrl)
    private val bodyString: String = client(request).bodyString()
    private var response: InputSource = InputSource(StringReader(bodyString))
    private var document: Document
    private val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val xPath: XPath = XPathFactory.newInstance().newXPath()

    init {
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        document = documentBuilder.parse(response)
        document.documentElement.normalize()
    }

    fun source(): String {
        return bodyString
    }

    fun items(): NodeList {
        return document.getElementsByTagName("item")
    }

    fun title(): String {
        val titleXPath = "//channel/title"
        val titleNode: Node = xPath.compile(titleXPath).evaluate(document, XPathConstants.NODE) as Node
        return titleNode.textContent.trim()
    }
}