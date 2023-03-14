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
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class RssFeedsTest {
    private val testTableName: String = "rss_feeds_items"
    private val testDbFilename: String = "rssfeeds_test.db"
    private val testDb = SqliteDb(testDbFilename)

    @Test
    fun rssFeedsShouldHaveTitle() {
        val classUnderTest = RssFeeds("https://www.ithome.com.tw/rss")
        assertNotNull(classUnderTest.title())
    }

    @Test
    fun rssFeedsShouldHaveItems() {
        val classUnderTest = RssFeeds("https://www.ithome.com.tw/rss")
        val items = classUnderTest.items()

        assertIs<NodeList>(items)
        assert(items.length > 0)

        val item = RssFeedsItem(items.item(1) as Node)
        item.apply {
            assertNotNull(this.title())
            assertNotNull(this.description())
            assertNotNull(this.link())
            assertNotNull(this.guid())
        }

        classUnderTest.updateDb(testDbFilename)
        assert(
            testDb.connection.createStatement().executeQuery("SELECT COUNT(*) FROM ${testTableName}")
                .getInt(1) == items.length
        )
        // clean database
        testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")
        testDb.close()
    }

    @Test
    fun rssFeedsMightHavePendingItem() {
        // simulates part of PostToMastodonInstanceJob#execute() to get random pending item
        testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")

        val rssFeeds = RssFeeds("https://www.ithome.com.tw/rss")
        rssFeeds.updateDb(testDbFilename)

        val preparedStatement = testDb.connection.prepareStatement(
            "SELECT * FROM rss_feeds_items WHERE post_status = ? OR post_status = ? ORDER BY RANDOM() LIMIT 1;"
        )

        preparedStatement.setInt(1, RssFeeds.PostStatus.QUEUED.status)
        preparedStatement.setInt(2, RssFeeds.PostStatus.FAILED.status)
        try {
            val resultSet = preparedStatement.executeQuery()

            // the initial, untouched resultSet cursor should be at row 0:
            assertEquals(0, resultSet.row)
            resultSet.next()
            // if the above query have row results, then the cursor should be at row 1 now:
            assertEquals(1, resultSet.row)
            // and we can read each value in the item columns:
            assertNotNull(resultSet.getString("id"))
            assertNotNull(resultSet.getString("title"))

            resultSet.close()
            // must be executed here, or resultSet will be released too early. :-(
            preparedStatement.close()
        } finally {
            // clean database
            testDb.connection.createStatement().execute("DELETE FROM ${testTableName}")
            testDb.close()
        }
    }
}