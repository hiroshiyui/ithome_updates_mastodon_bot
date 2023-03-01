package ithome_updates_mastodon_bot

import org.w3c.dom.Element
import org.w3c.dom.Node

class RssFeedsItem(node: Node) {
    private val item = node as Element

    fun title(): String {
        return item.getElementsByTagName("title").item(0).textContent.trim()
    }

    fun description(): String {
        return item.getElementsByTagName("description").item(0).textContent.trim()
    }

    fun link(): String {
        return item.getElementsByTagName("link").item(0).textContent.trim()
    }
}