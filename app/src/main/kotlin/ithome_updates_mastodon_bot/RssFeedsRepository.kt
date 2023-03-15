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
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.sql.PreparedStatement
import java.sql.ResultSet

class RssFeedsRepository(private val dbFilename: String = "rssfeeds.db") : LoggerHelper {
    data class Item(var id: Int, var title: String, var description: String, var link: String)

    enum class PostStatus(val status: Int) {
        QUEUED(0),
        DONE(1),
        FAILED(2)
    }

    fun update(channel: String, items: NodeList) {
        repeat(items.length) { entry ->
            val itemNode: Node = items.item(entry)
            val item = RssFeedsItem(itemNode)
            saveItem(channel, item)
        }
    }

    private fun saveItem(channel: String, item: RssFeedsItem) {
        val db = SqliteDb(dbFilename)
        val preparedStatement = db.connection.prepareStatement(
            """
            INSERT OR IGNORE INTO rss_feeds_items (channel, title, description, link, guid, post_status)
                VALUES(?, ?, ?, ?, ?, ?)
        """.trimIndent()
        )

        preparedStatement.apply {
            this.setString(1, channel)
            this.setString(2, item.title)
            this.setString(3, item.description)
            this.setString(4, item.link)
            this.setString(5, item.guid)
            this.setInt(6, PostStatus.QUEUED.status)
        }
        try {
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } finally {
            db.close()
        }
    }

    fun randomPendingItem(): Item {
        val db = SqliteDb(dbFilename)
        val pendingItem = Item(id = 0, title = "", description = "", link = "")
        val randomPendingItemPreparedStatement: PreparedStatement = db.connection.prepareStatement(
            "SELECT * FROM rss_feeds_items WHERE post_status = ? OR post_status = ? ORDER BY RANDOM() LIMIT 1;"
        )

        randomPendingItemPreparedStatement.apply {
            this.setInt(1, PostStatus.QUEUED.status)
            this.setInt(2, PostStatus.FAILED.status)
        }

        try {
            val randomPendingItem: ResultSet = randomPendingItemPreparedStatement.executeQuery()

            randomPendingItem.apply {
                if (this.next()) {
                    pendingItem.id = this.getInt("id")
                    pendingItem.title = this.getString("title")
                    pendingItem.description = this.getString("description")
                    pendingItem.link = this.getString("link")
                }
            }
            randomPendingItem.close()
            randomPendingItemPreparedStatement.close()
        } finally {
            db.close()
        }

        return pendingItem
    }

    fun updateItemAsStatusToDb(item: Item, postStatus: PostStatus) {
        val db = SqliteDb(dbFilename)
        try {
            val preparedStatement =
                db.connection.prepareStatement("UPDATE rss_feeds_items SET post_status = ? WHERE id = ?;")
            preparedStatement.setInt(1, postStatus.status)
            preparedStatement.setInt(2, item.id)
            preparedStatement.executeUpdate()
            preparedStatement.close()
        } finally {
            db.close()
        }
    }
}
