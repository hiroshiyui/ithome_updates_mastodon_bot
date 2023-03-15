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

import ithome_updates_mastodon_bot.RssFeedsRepository
import ithome_updates_mastodon_bot.helpers.ConfigHelper
import ithome_updates_mastodon_bot.helpers.LoggerHelper
import ithome_updates_mastodon_bot.singleton.HttpClientSingleton
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form
import org.quartz.Job
import org.quartz.JobExecutionContext

class PostToMastodonInstanceJob : Job, LoggerHelper, ConfigHelper {
    private val client = HttpClientSingleton.client

    override fun execute(context: JobExecutionContext) {
        logger.info("Running PostToMastodonInstanceJob...")
        try {
            val rssFeedsRepository = RssFeedsRepository()
            val item = rssFeedsRepository.randomPendingItem()
            if (item.title.isNotEmpty()) {
                val postResult: Boolean = postToMastodonInstance(item)
                if (postResult) {
                    rssFeedsRepository.updateItemAsStatusToDb(item, RssFeedsRepository.PostStatus.DONE)
                } else {
                    rssFeedsRepository.updateItemAsStatusToDb(item, RssFeedsRepository.PostStatus.FAILED)
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    private fun postToMastodonInstance(item: RssFeedsRepository.Item): Boolean {
        val postStatusApiEndpoint = "${config.getString("mastodon.instance-url")}/api/v1/statuses"
        val statusContent =
            "〈${item.title}〉\n\n${item.description}\n${item.link}".trim()
        val request = Request(Method.POST, postStatusApiEndpoint)
            .header("Content-Type", "multipart/form-data")
            .header(
                "Authorization",
                "Bearer ${config.getString("mastodon.access-token")}"
            )
            .form("status", statusContent)

        val requestCall = client(request)

        return requestCall.status.successful
    }
}
