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

import java.sql.ResultSet
import kotlin.test.Test
import kotlin.test.assertTrue

class SqliteDbTest {
    val tableName: String = "rss_feeds_items"

    @Test
    fun sqliteDbShouldHavaTableRssFeedsItems() {
        val classUnderTest = SqliteDb()
        val dbMetadataResultSet: ResultSet =
            classUnderTest.statement.connection.metaData.getTables(null, null, tableName, arrayOf("TABLE"))
        assertTrue(dbMetadataResultSet.next(), "Database should have table 'rss_feeds_items'")
        classUnderTest.close()
    }

    @Test
    fun tableRssFeedsItemsShouldHaveColumnId() {
        assertTrue(checkTableRssFeedsItemsHaveColumn("id"))
        assertTrue(checkTableRssFeedsItemsHaveColumnInType("id", "INTEGER"))
    }

    @Test
    fun tableRssFeedsItemsShouldHaveColumnChannel() {
        assertTrue(checkTableRssFeedsItemsHaveColumn("channel"))
        assertTrue(checkTableRssFeedsItemsHaveColumnInType("channel", "TEXT"))
    }

    private fun checkTableRssFeedsItemsHaveColumn(columnName: String): Boolean {
        val classUnderTest = SqliteDb()
        val columnIdDescriptionResultSet: ResultSet
        val result: Boolean
        try {
            columnIdDescriptionResultSet =
                classUnderTest.statement.connection.metaData.getColumns(null, null, tableName, columnName)
            result = columnIdDescriptionResultSet.next()
        } finally {
            classUnderTest.close()
        }
        return result
    }

    private fun checkTableRssFeedsItemsHaveColumnInType(columnName: String, columnType: String): Boolean {
        val classUnderTest = SqliteDb()
        val columnIdDescriptionResultSet: ResultSet
        val typeNameIndex: Int
        val result: Boolean
        try {
            columnIdDescriptionResultSet = classUnderTest.statement.connection.metaData.getColumns(null, null, tableName, columnName)
            typeNameIndex = columnIdDescriptionResultSet.findColumn("TYPE_NAME")
            result = (columnIdDescriptionResultSet.getString(typeNameIndex) == columnType)
        } finally {
            classUnderTest.close()
        }
        return result
    }
}