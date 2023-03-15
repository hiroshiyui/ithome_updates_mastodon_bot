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

import org.junit.jupiter.api.Test

class RssFeedsRepositoryTest {
    private val testTableName: String = "rss_feeds_items"
    private val testDbFilename: String = "rssfeeds_test.db"
    private val testDb = SqliteDb(testDbFilename)

    @Test
    fun rssFeedsRepositoryMightHavePendingItem() {
        testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")

        val rssFeeds = RssFeeds("https://www.ithome.com.tw/rss")
        rssFeeds.updateRepository(testDbFilename)
        val rssFeedsRepository = RssFeedsRepository(testDbFilename)

        try {
            val item = rssFeedsRepository.randomPendingItem()
            assert(item.title.isNotEmpty())
            assert(item.description.isNotEmpty())
            assert(item.link.isNotEmpty())
        } finally {
            // clean database
            testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")
        }

        // re-test with an empty table (to simulates no pending items)
        try {
            val item = rssFeedsRepository.randomPendingItem()
            // it's possible if there is no pending item.
            assert(item.title.isEmpty())
            assert(item.description.isEmpty())
            assert(item.link.isEmpty())
        } finally {
            // clean database
            testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")
            testDb.close()
        }
    }
}