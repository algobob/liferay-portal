/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.servlet.taglib.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Anderson Luiz
 */
public class HTMLUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAsHtmlAttribute() {
		String result = HTMLUtil.asHtmlAttribute("key", "value");

		Assert.assertEquals("key=\"value\" ", result);
	}

	@Test
	public void testAsHtmlAttributeInvalid() {
		String key = "some-key";
		String invalidValue1 = StringPool.BLANK;
		String invalidValue2 = "invalid";

		Assert.assertEquals(
			StringPool.BLANK, HTMLUtil.asHtmlAttribute(key, invalidValue1));
		Assert.assertEquals(
			StringPool.BLANK,
			HTMLUtil.asHtmlAttribute(
				key, invalidValue2, Objects.equals(invalidValue2, "valid")));
	}

	@Test
	public void testToHTMLJSScript() {
		Assert.assertEquals(
			"<script src=\"src-url\" attr2=\"value2\" attr1=\"value1\" > " +
				"</script>",
			HTMLUtil.toHTMLJSScript(
				HashMapBuilder.put(
					"attr1", "value1"
				).put(
					"attr2", "value2"
				).put(
					"src", "src-url"
				).build()));
	}

}