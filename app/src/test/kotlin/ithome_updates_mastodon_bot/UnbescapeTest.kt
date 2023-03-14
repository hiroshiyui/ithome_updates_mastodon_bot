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
import org.unbescape.html.HtmlEscape
import kotlin.test.assertEquals

class UnbescapeTest {
    @Test
    fun unbescapeShouldBeAbleToUnescapedString() {
        val escapedString = "第三方廠商被駭導致AT&amp;T 900萬用戶個資外洩"
        val unescapedString = HtmlEscape.unescapeHtml(escapedString)
        assertEquals("第三方廠商被駭導致AT&T 900萬用戶個資外洩", unescapedString)
        val escapedAgainString = unescapedString.let { HtmlEscape.escapeHtml4Xml(it) }
        assertEquals(escapedString, escapedAgainString)
    }
}