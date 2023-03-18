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

import ithome_updates_mastodon_bot.doubles.MastodonMockServer
import ithome_updates_mastodon_bot.helpers.ConfigHelper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MastodonInstanceClientTest : ConfigHelper {
    @Test
    fun mastodonInstanceClientShouldHasInstanceUrl() {
        val classUnderTest = MastodonInstanceClient()
        val defaultInstanceUrl = config.getString("mastodon.instance-url")
        assertEquals(defaultInstanceUrl, classUnderTest.instanceUrl)
        val customizedClassUnderTest = MastodonInstanceClient("https://example.com")
        assertEquals("https://example.com", customizedClassUnderTest.instanceUrl)
    }

    @Test
    fun postToMockServerShouldBeOk() {
        val mockServer = MastodonMockServer()
        val classUnderTest = MastodonInstanceClient("http://localhost:5566")
        val rssFeedsRepository = RssFeedsRepository()
        val item = rssFeedsRepository.randomPendingItem()
        assert(classUnderTest.postToInstance(item))
        mockServer.close()
    }
}