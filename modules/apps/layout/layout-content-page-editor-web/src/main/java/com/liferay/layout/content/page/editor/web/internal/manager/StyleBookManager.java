/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.manager;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Anderson Luiz
 */
@Component(service = StyleBookManager.class)
public class StyleBookManager {

	public List<StyleBookEntry> getStyleBookEntries(
		long companyId, long groupId, long layoutId, String themeId) {

		List<StyleBookEntry> styleBookEntries =
			_styleBookEntryLocalService.getStyleBookEntries(
				groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				StyleBookEntryNameComparator.getInstance(true));

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-30204")) {
			FrontendTokenDefinition frontendTokenDefinition =
				_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
					companyId, layoutId, themeId);

			if (frontendTokenDefinition == null) {
				return styleBookEntries;
			}

			return ListUtil.filter(
				styleBookEntries,
				styleBookEntry -> Objects.equals(
					styleBookEntry.getThemeId(),
					frontendTokenDefinition.getThemeId()));
		}

		return styleBookEntries;
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}
