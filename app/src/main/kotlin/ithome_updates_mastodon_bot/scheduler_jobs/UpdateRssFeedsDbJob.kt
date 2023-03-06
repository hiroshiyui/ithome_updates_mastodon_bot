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
import ithome_updates_mastodon_bot.helpers.LoggerHelper
import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext

class UpdateRssFeedsDbJob : Job, LoggerHelper {
    override fun execute(context: JobExecutionContext) {
        logger.info("Running UpdateRssFeedsDbJob...")
        val data: JobDataMap = context.mergedJobDataMap
        val rssFeedsUrl: String = data.getString("rssFeedsUrl")
        val rssFeeds = RssFeeds(rssFeedsUrl)
        rssFeeds.updateDb()
    }
}
