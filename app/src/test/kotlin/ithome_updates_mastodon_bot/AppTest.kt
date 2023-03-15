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

import org.apache.commons.configuration2.XMLConfiguration
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.slf4j.Logger
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AppTest {
    @Test
    fun appHasLogger() {
        val classUnderTest = App()
        assertIs<Logger>(classUnderTest.logger, "app should have a logger")
        assertTrue(classUnderTest.logger.isInfoEnabled, "logger should be able to do logging at level INFO")
        assertTrue(classUnderTest.logger.isErrorEnabled, "logger should be able to do logging at level ERROR")
    }

    @Test
    fun appHasConfigurations() {
        val classUnderTest = App()
        assertIs<Configurations>(classUnderTest.configurations, "app should have configurations")
        assertIs<XMLConfiguration>(classUnderTest.config, "app should have configurations in XML format")
    }
}
