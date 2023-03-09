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

class RssFeeds(rssFeedsUrl: String) : LoggerHelper {
    private val client = JettyClient()
    private val request = Request(Method.GET, rssFeedsUrl)
    private val bodyString: String = client(request).bodyString()
    private var response: InputSource = InputSource(StringReader(bodyString))
    private var document: Document
    private val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val xPath: XPath = XPathFactory.newInstance().newXPath()

    enum class PostStatus(val status: Int) {
        QUEUED(0),
        DONE(1),
        FAILED(2)
    }

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

    private fun saveItem(item: RssFeedsItem, sqliteDb: SqliteDb) {
        val preparedStatement = sqliteDb.statement.connection.prepareStatement(
            """
            INSERT OR IGNORE INTO rss_feeds_items (channel, title, description, link, guid, post_status)
                VALUES(?, ?, ?, ?, ?, ?)
        """.trimIndent()
        )

        preparedStatement.apply {
            this.setString(1, title())
            this.setString(2, item.title())
            this.setString(3, item.description())
            this.setString(4, item.link())
            this.setString(5, item.guid())
            this.setInt(6, PostStatus.QUEUED.status)
        }
        preparedStatement.executeUpdate()
    }

    fun updateDb(dbFilename: String = "rssfeeds.db") {
        val sqliteDb = SqliteDb(dbFilename)

        try {
            repeat(this.items().length) { entry ->
                val itemNode: Node = this.items().item(entry)
                val item = RssFeedsItem(itemNode)
                saveItem(item, sqliteDb)
            }
        } catch (e: Exception) {
            logger.error(e.message)
        } finally {
            sqliteDb.statement.close()
            sqliteDb.close()
        }
    }
}