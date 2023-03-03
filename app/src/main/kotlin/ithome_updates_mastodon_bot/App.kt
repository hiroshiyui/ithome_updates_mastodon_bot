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
import ithome_updates_mastodon_bot.scheduler_jobs.UpdateRssFeedsDbJob
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

class App : LoggerHelper {
    private val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()

    init {
        scheduler.start()
    }

    fun registerScheduleUpdateRssFeedsDbJob(rssFeedsUrl: String, identity: String) {
        val updateRssFeedsDbJob: JobDetail = JobBuilder.newJob(UpdateRssFeedsDbJob::class.java)
            .withIdentity("updateRssFeedsDbJob_${identity}", "defaultGroup")
            .usingJobData("rssFeedsUrl", rssFeedsUrl)
            .build()
        val updateRssFeedsDbJobTrigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("updateRssFeedsDbJobTrigger_${identity}", "defaultGroup")
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(10).repeatForever()
            )
            .build()
        scheduler.scheduleJob(updateRssFeedsDbJob, updateRssFeedsDbJobTrigger)
    }

    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    val app = App()

    app.logger.info("Starting ithome_updates_mastodon_bot...")
    println(app.greeting)

    app.registerScheduleUpdateRssFeedsDbJob("https://www.ithome.com.tw/rss", "ithome")
}
