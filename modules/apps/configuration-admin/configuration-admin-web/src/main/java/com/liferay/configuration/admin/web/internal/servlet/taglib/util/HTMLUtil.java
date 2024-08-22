/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.servlet.taglib.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Anderson Luiz
 */
public class HTMLUtil {

	public static String asHtmlAttribute(String key, String value) {
		if (Validator.isNull(key) || Validator.isNull(value)) {
			return StringPool.BLANK;
		}

		return String.format("%s=\"%s\" ", key, value);
	}

	public static String asHtmlAttribute(
		String key, String value, boolean valid) {

		if (!valid) {
			return StringPool.BLANK;
		}

		return asHtmlAttribute(key, value);
	}

	public static String toHTMLJSScript(
		Map<String, String> attributesValues, String... dynamicAttributes) {

		StringBuilder stringBuilder = new StringBuilder("<script ");

		attributesValues.forEach(
			(key, value) -> stringBuilder.append(asHtmlAttribute(key, value)));

		for (String dynamicAttribute : dynamicAttributes) {
			stringBuilder.append(dynamicAttribute);
		}

		stringBuilder.append("> </script>");

		return stringBuilder.toString();
	}

}