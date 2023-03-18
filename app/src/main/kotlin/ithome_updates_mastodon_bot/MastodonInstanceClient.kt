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

import ithome_updates_mastodon_bot.helpers.ConfigHelper
import ithome_updates_mastodon_bot.singleton.HttpClientSingleton
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.body.form

class MastodonInstanceClient(instanceUrl: String = "") : ConfigHelper {
    private val client = HttpClientSingleton.client
    var instanceUrl: String

    init {
        this.instanceUrl = instanceUrl.ifEmpty {
            config.getString("mastodon.instance-url")
        }

        if (this.instanceUrl.isEmpty()) {
            throw InstanceUrlInvalidException("Invalid Mastodon instance URL.")
        }
    }

    fun postToInstance(item: RssFeedsRepository.Item): Boolean {
        val postStatusApiEndpoint = "${instanceUrl}/api/v1/statuses"
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

class InstanceUrlInvalidException(message: String) : Exception(message)