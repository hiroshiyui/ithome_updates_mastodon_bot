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
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

class SqliteDb(dbFilename: String = "rssfeeds.db") : LoggerHelper {
    val connection: Connection = DriverManager.getConnection("jdbc:sqlite:${dbFilename}")

    init {
        logger.info("Connection to SQLite database has been established.")
        val statement: Statement = connection.createStatement()
        statement.queryTimeout = 10
        // create database table for RSS Feeds items
        statement.executeUpdate(
            """
            CREATE TABLE IF NOT EXISTS rss_feeds_items
                (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    channel TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    link TEXT NOT NULL,
                    guid TEXT NOT NULL UNIQUE,
                    post_status INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
        """.trimIndent()
        )
        statement.close()
    }

    fun close() {
        connection.close()
        logger.info("Connection to SQLite database has been closed.")
    }
}