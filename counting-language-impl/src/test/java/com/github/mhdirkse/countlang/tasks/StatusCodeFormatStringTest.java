/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.tasks;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.github.mhdirkse.utils.AbstractStatusCode;

@RunWith(Parameterized.class)
public class StatusCodeFormatStringTest {
    @Parameters(name = "Enum value {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(StatusCode.values()).stream()
                .map(sc -> new Object[] {sc})
                .collect(Collectors.toList());
    }

    @Parameter
    public StatusCode statusCode;

    @Before
    public void setUp() {
        StatusCode.setTestMode(true);
    }

    @After
    public void tearDown() {
        StatusCode.setTestMode(false);
    }

    @Test
    public void allFormattersAreBracketDigitsBracket() {
        String validFormattersRemoved = statusCode.getFormatString()
                .replaceAll(AbstractStatusCode.anyFormatter(), "");
        Assert.assertFalse(validFormattersRemoved.contains("{"));
        Assert.assertFalse(validFormattersRemoved.contains("}"));
    }

    @Test
    public void allFormattersEndWithDot() {
        String fmt = statusCode.getFormatString();
        Assert.assertEquals(
                Character.valueOf('.').toString(),
                Character.valueOf(fmt.charAt(fmt.length() - 1)).toString());
    }
}
