/*
 * Copyright (c) 2023 YOU, Hui-Hong
 *
 * This file is part of ithome_updates_mastodon_bot.
 *
 * ithome_updates_mastodon_bot is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * ithome_updates_mastodon_bot is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Foobar. If not, see <https://www.gnu.org/licenses/>.
 */


package ithome_updates_mastodon_bot

import ithome_updates_mastodon_bot.helpers.LoggerHelper
import ithome_updates_mastodon_bot.singleton.HttpClientSingleton
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

class RssFeeds(rssFeedsUrl: String) : LoggerHelper {
    private val client = HttpClientSingleton.client
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

    val items: NodeList
        get() {
            return document.getElementsByTagName("item")
        }

    val channel: String
        get() {
            val titleXPath = "//channel/title"
            val titleNode: Node = xPath.compile(titleXPath).evaluate(document, XPathConstants.NODE) as Node
            return titleNode.textContent.trim()
        }

    fun updateRepository(dbFilename: String = "rssfeeds.db") {
        val rssFeedsRepository = RssFeedsRepository(dbFilename)
        rssFeedsRepository.update(this.channel, this.items)
    }
}