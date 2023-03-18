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

import ithome_updates_mastodon_bot.MastodonInstanceClient
import ithome_updates_mastodon_bot.RssFeedsRepository
import ithome_updates_mastodon_bot.helpers.ConfigHelper
import ithome_updates_mastodon_bot.helpers.LoggerHelper
import org.quartz.Job
import org.quartz.JobExecutionContext

class PostToMastodonInstanceJob : Job, LoggerHelper, ConfigHelper {
    private val mastodonInstanceClient = MastodonInstanceClient()

    override fun execute(context: JobExecutionContext) {
        logger.info("Running PostToMastodonInstanceJob...")
        try {
            val rssFeedsRepository = RssFeedsRepository()
            val item = rssFeedsRepository.randomPendingItem()
            if (item.title.isNotEmpty()) {
                val postResult: Boolean = mastodonInstanceClient.postToInstance(item)
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
}
