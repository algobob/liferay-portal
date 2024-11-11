/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Anderson Luiz
 */
public class StyleBookManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@FeatureFlags("LPD-30204")
	@Test
	public void testGetStyleBooks() {
		FrontendTokenDefinition frontendTokenDefinition = Mockito.mock(
			FrontendTokenDefinition.class);

		String themeId = RandomTestUtil.randomString();

		Mockito.when(
			frontendTokenDefinition.getThemeId()
		).thenReturn(
			themeId
		);

		StyleBookEntry styleBookEntry1 = Mockito.mock(StyleBookEntry.class);
		StyleBookEntry styleBookEntry2 = Mockito.mock(StyleBookEntry.class);

		Mockito.when(
			styleBookEntry1.getThemeId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			styleBookEntry2.getThemeId()
		).thenReturn(
			themeId
		);

		Mockito.when(
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			frontendTokenDefinition
		);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_styleBookEntryLocalService.getStyleBookEntries(
				Mockito.eq(groupId), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.any())
		).thenReturn(
			Arrays.asList(styleBookEntry1, styleBookEntry2)
		);

		List<StyleBookEntry> styleBookEntries =
			_styleBookManager.getStyleBookEntries(
				RandomTestUtil.randomLong(), groupId,
				RandomTestUtil.randomLong(), themeId);

		Mockito.verify(
			_styleBookEntryLocalService
		).getStyleBookEntries(
			Mockito.eq(groupId), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any()
		);

		Mockito.verify(
			_frontendTokenDefinitionRegistry
		).getFrontendTokenDefinition(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString()
		);

		Assert.assertEquals(
			styleBookEntries.toString(), 1, styleBookEntries.size());

		Assert.assertEquals(styleBookEntry2, styleBookEntries.get(0));
	}

	@Mock
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Mock
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@InjectMocks
	private StyleBookManager _styleBookManager;

}