/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.factory;

import com.liferay.client.extension.exception.ClientExtensionEntryTypeSettingsException;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.client.extension.type.internal.ThemeCSSCETImpl;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author Anderson Luiz
 */
public class ThemeCSSCETImplFactoryImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFrontendTokenDefinition() throws PortalException {
		expectedEx.expect(ClientExtensionEntryTypeSettingsException.class);
		expectedEx.expectMessage("Invalid frontend token definition");

		ThemeCSSCET themeCSSCET = new ThemeCSSCETImpl(
				"", 0, null, "", "", null, "", null, false, "", 0,
				UnicodePropertiesBuilder.put(
						"frontendTokenDefinition",
						"{frontendTokenCategories\": [{\"frontendTokenSets\": [," +
								"\"label\": \"buttons\",\"name\": \"buttons\"}]}"
				).build());

		ThemeCSSCETImplFactoryImpl themeCSSCETImplFactoryImpl =
				new ThemeCSSCETImplFactoryImpl();

		try (MockedStatic<JSONFactoryUtil> utilities = Mockito.mockStatic(JSONFactoryUtil.class)) {
			utilities.when(() -> JSONFactoryUtil.createJSONObject(anyString())).thenThrow(JSONException.class);

			themeCSSCETImplFactoryImpl.validate(themeCSSCET, null);
		}
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
}