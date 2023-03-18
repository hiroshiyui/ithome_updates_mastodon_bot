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

package ithome_updates_mastodon_bot.doubles

import ithome_updates_mastodon_bot.helpers.LoggerHelper
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

class MastodonMockServer : LoggerHelper {
    private var http4kServer: Http4kServer
    private fun server(): RoutingHttpHandler {
        return routes(
            "/api/v1/statuses" bind Method.POST to {
                if (it.header("Content-Type").isNullOrEmpty()) {
                    Response(Status.UNPROCESSABLE_ENTITY)
                }

                // header should have "Content-Type" and its value should be "multipart/form-data"
                if (it.header("Content-Type") == "multipart/form-data") {
                    Response(Status.OK)
                } else {
                    Response(Status.UNPROCESSABLE_ENTITY)
                }
            }
        )
    }

    fun close() {
        http4kServer.stop()
    }

    init {
        http4kServer = server().asServer(Jetty(5566))
        http4kServer.start()
    }
}