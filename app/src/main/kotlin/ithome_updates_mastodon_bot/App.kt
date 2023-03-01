/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package ithome_updates_mastodon_bot

import org.w3c.dom.Element
import org.w3c.dom.Node
import kotlin.system.exitProcess

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(App().greeting)
    println(RssFeedsFetcher().getRssBody())
    val rssFeedsItems = RssFeedsFetcher().getRssFeedsItems()
    repeat(rssFeedsItems.length) {
        val itemNode: Node = rssFeedsItems.item(it)
        val element: Element = itemNode as Element
        println("---")
        println(element.getElementsByTagName("title").item(0).textContent.trim())
        println(element.getElementsByTagName("description").item(0).textContent.trim())
        println(element.getElementsByTagName("link").item(0).textContent)
    }

    exitProcess(0)
}
