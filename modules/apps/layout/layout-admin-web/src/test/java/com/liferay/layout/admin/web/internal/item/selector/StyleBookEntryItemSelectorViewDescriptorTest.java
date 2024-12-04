/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.item.selector;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsImpl;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortalContext;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletURL;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Anderson Luiz
 */
public class StyleBookEntryItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PropsUtil.setProps(new PropsImpl());
	}

	@FeatureFlags("LPD-30204")
	@Test
	public void testGetStyleBookEntries() throws PortalException {
		try (MockedStatic<LanguageUtil> languageUtilMockedStatic =
				Mockito.mockStatic(LanguageUtil.class);
			MockedStatic<StyleBookEntryLocalServiceUtil>
				styleBookEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					StyleBookEntryLocalServiceUtil.class)) {

			languageUtilMockedStatic.when(
				() -> LanguageUtil.get(
					Mockito.any(HttpServletRequest.class), Mockito.anyString())
			).thenReturn(
				RandomTestUtil.randomString()
			);

			StyleBookEntry styleBookEntry1 = Mockito.mock(StyleBookEntry.class);

			styleBookEntryLocalServiceUtilMockedStatic.when(
				StyleBookEntryLocalServiceUtil::create
			).thenReturn(
				styleBookEntry1
			);

			StyleBookEntry styleBookEntry2 = Mockito.mock(StyleBookEntry.class);

			Mockito.when(
				styleBookEntry2.getThemeId()
			).thenReturn(
				"dialect"
			);

			StyleBookEntry styleBookEntry3 = Mockito.mock(StyleBookEntry.class);

			Mockito.when(
				styleBookEntry3.getThemeId()
			).thenReturn(
				RandomTestUtil.randomString()
			);

			styleBookEntryLocalServiceUtilMockedStatic.when(
				() -> StyleBookEntryLocalServiceUtil.getStyleBookEntries(
					Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
					Mockito.any())
			).thenReturn(
				ListUtil.fromArray(styleBookEntry2, styleBookEntry3)
			);

			StyleBookEntryItemSelectorViewDescriptor
				styleBookEntryItemSelectorViewDescriptor =
					new StyleBookEntryItemSelectorViewDescriptor(
						_setUpHttpRequest(),
						new MockPortletURL(new MockPortalContext(), null),
						new StyleBookEntryItemSelectorCriterion());

			ReflectionTestUtil.setFieldValue(
				styleBookEntryItemSelectorViewDescriptor, "_selLayout",
				Mockito.mock(Layout.class));

			SearchContainer<StyleBookEntry> searchContainer =
				styleBookEntryItemSelectorViewDescriptor.getSearchContainer();

			List<StyleBookEntry> results = searchContainer.getResults();

			Assert.assertEquals(results.toString(), 2, results.size());
			Assert.assertEquals(styleBookEntry1, results.get(0));
			Assert.assertEquals(styleBookEntry2, results.get(1));
		}
	}

	private HttpServletRequest _setUpHttpRequest() {
		FrontendTokenDefinition frontendTokenDefinition = Mockito.mock(
			FrontendTokenDefinition.class);

		Mockito.when(
			frontendTokenDefinition.getThemeId()
		).thenReturn(
			"dialect"
		);

		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry =
			Mockito.mock(FrontendTokenDefinitionRegistry.class);

		Mockito.when(
			frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any())
		).thenReturn(
			frontendTokenDefinition
		);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			FrontendTokenDefinitionRegistry.class.getName(),
			frontendTokenDefinitionRegistry);

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, new MockPortletRequest());

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

}