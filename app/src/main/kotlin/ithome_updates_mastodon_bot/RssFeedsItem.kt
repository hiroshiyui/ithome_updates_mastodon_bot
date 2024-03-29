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

import org.unbescape.html.HtmlEscape
import org.w3c.dom.Element
import org.w3c.dom.Node

class RssFeedsItem(node: Node) {
    private val item = node as Element

    val title: String = item.getElementsByTagName("title").item(0).textContent.trim().let { HtmlEscape.unescapeHtml(it) }

    val description: String = item.getElementsByTagName("description").item(0).textContent.trim().let { HtmlEscape.unescapeHtml(it) }

    val link: String = item.getElementsByTagName("link").item(0).textContent.trim()

    val guid: String = item.getElementsByTagName("guid").item(0).textContent.trim()
}