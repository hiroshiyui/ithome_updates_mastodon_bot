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

import org.w3c.dom.Node
import java.util.logging.Logger

class App {
    val logger: Logger = Logger.getLogger(this.javaClass.name)

    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    val app = App()
    val logger = app.logger
    val sqliteDb = SqliteDb()

    logger.info("Starting ithome_updates_mastodon_bot...")

    println(app.greeting)

    val rssFeeds = RssFeeds("https://www.ithome.com.tw/rss")
    println(rssFeeds.source())
    logger.info("RSS feeds '${rssFeeds.title()}' has ${rssFeeds.items().length} items.")

    repeat(rssFeeds.items().length) { entry ->
        val itemNode: Node = rssFeeds.items().item(entry)
        val item = RssFeedsItem(itemNode)
        println("---")
        println(item.title())
        println(item.description())
        println(item.link())
        rssFeeds.saveItem(item, sqliteDb)
    }

    sqliteDb.close()
}
