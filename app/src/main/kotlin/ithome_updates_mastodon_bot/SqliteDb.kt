package ithome_updates_mastodon_bot

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.logging.Logger

class SqliteDb {
    private var connection: Connection = DriverManager.getConnection("jdbc:sqlite:rssfeeds.db")
    private val logger: Logger = Logger.getLogger(this.javaClass.name)
    val statement: Statement = connection.createStatement()

    init {
        logger.info("Connection to SQLite database has been established.")
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
                    post_status INTEGER NOT NULL
                );
        """.trimIndent()
        )
    }

    fun close() {
        connection.close()
        logger.info("Connection to SQLite database has been closed.")
    }
}