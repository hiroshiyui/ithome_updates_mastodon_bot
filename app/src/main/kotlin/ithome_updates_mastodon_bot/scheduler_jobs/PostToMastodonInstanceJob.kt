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

package ithome_updates_mastodon_bot.scheduler_jobs

import ithome_updates_mastodon_bot.RssFeeds
import ithome_updates_mastodon_bot.SqliteDb
import ithome_updates_mastodon_bot.helpers.ConfigHelper
import ithome_updates_mastodon_bot.helpers.LoggerHelper
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.sql.ResultSet

class PostToMastodonInstanceJob : Job, LoggerHelper, ConfigHelper {
    private val sqliteDb = SqliteDb()

    override fun execute(context: JobExecutionContext?) {
        logger.info("Running PostToMastodonInstanceJob...")
        try {
            getRandomSinglePendingItemFromDb()?.apply {
                while (this.next()) {
                    val postResult: Boolean = postToMastodonInstance(this)

                    if (postResult) {
                        updateItemAsStatusToDb(this, RssFeeds.PostStatus.DONE)
                    } else {
                        updateItemAsStatusToDb(this, RssFeeds.PostStatus.FAILED)
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        } finally {
            sqliteDb.statement.close()
            sqliteDb.close()
        }
    }

    private fun postToMastodonInstance(resultSet: ResultSet): Boolean {
        val client = JettyClient()
        val postStatusApiEndpoint = "${config.getString("mastodon.instance-url")}/api/v1/statuses"
        val statusContent =
            "〈${resultSet.getString("title")}〉\n\n${resultSet.getString("description")}\n${resultSet.getString("link")}".trim()
        val request = Request(Method.POST, postStatusApiEndpoint)
            .header("Content-Type", "multipart/form-data")
            .header(
                "Authorization",
                "Bearer ${config.getString("mastodon.access-token")}"
            )
            .form("status", statusContent)

        val requestCall = client(request)
        client.close()

        return requestCall.status.successful
    }

    private fun updateItemAsStatusToDb(resultSet: ResultSet, postStatus: RssFeeds.PostStatus): Boolean {
        val preparedStatement =
            sqliteDb.statement.connection.prepareStatement("UPDATE rss_feeds_items SET post_status = ? WHERE id = ?;")
        preparedStatement.setInt(1, postStatus.status)
        preparedStatement.setInt(2, resultSet.getInt("id"))
        preparedStatement.executeUpdate()

        return true
    }

    private fun getRandomSinglePendingItemFromDb(): ResultSet? {
        val preparedStatement = sqliteDb.statement.connection.prepareStatement(
            """
            SELECT * FROM rss_feeds_items WHERE post_status = ? OR post_status = ? ORDER BY RANDOM() LIMIT 1;
            """.trimIndent()
        )

        preparedStatement.setInt(1, RssFeeds.PostStatus.QUEUED.status)
        preparedStatement.setInt(2, RssFeeds.PostStatus.FAILED.status)
        return preparedStatement.executeQuery()
    }

    private fun getPendingItemsFromDb(): ResultSet? {
        val preparedStatement = sqliteDb.statement.connection.prepareStatement(
            """
            SELECT * FROM rss_feeds_items WHERE post_status = ? OR post_status = ?;
            """.trimIndent()
        )

        preparedStatement.setInt(1, RssFeeds.PostStatus.QUEUED.status)
        preparedStatement.setInt(2, RssFeeds.PostStatus.FAILED.status)
        return preparedStatement.executeQuery()
    }
}
