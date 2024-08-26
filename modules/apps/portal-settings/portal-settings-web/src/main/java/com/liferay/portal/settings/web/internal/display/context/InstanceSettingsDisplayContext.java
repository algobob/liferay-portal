/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.display.context;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.item.selector.CETItemSelectorReturnType;
import com.liferay.client.extension.type.item.selector.criterion.CETItemSelectorCriterion;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.configuration.admin.configuration.InstanceSettingsClientExtensionsConfiguration;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.settings.web.internal.constants.PortalSettingsWebKeys;

import java.util.Map;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Anderson Luiz
 */
public class InstanceSettingsDisplayContext {

	public InstanceSettingsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_cetManager = (CETManager)httpServletRequest.getAttribute(
			CETManager.class.getName());

		_itemSelector = (ItemSelector)httpServletRequest.getAttribute(
			ItemSelector.class.getName());

		_configurationProvider =
			(ConfigurationProvider)httpServletRequest.getAttribute(
				ConfigurationProvider.class.getName());
	}

	public Map<String, Object> getGlobalJSCETsConfigurationProps()
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"globalJSCETs", _getAppliedGlobalJCETJSONArray()
		).put(
			"globalJSCETSelectorURL",
			() -> PortletURLBuilder.create(
				_getCETItemSelectorURL(
					true, "selectGlobalJSCETs",
					ClientExtensionEntryConstants.TYPE_GLOBAL_JS)
			).buildString()
		).put(
			"isReadOnly", false
		).put(
			"selectGlobalJSCETsEventName", "selectGlobalJSCETs"
		).build();
	}

	private JSONArray _getAppliedGlobalJCETJSONArray() throws PortalException {
		InstanceSettingsClientExtensionsConfiguration
			instanceSettingsClientExtensionsConfiguration =
				_configurationProvider.getCompanyConfiguration(
					InstanceSettingsClientExtensionsConfiguration.class,
					CompanyThreadLocal.getCompanyId());

		String[] externalReferenceCodes =
			instanceSettingsClientExtensionsConfiguration.
				getJSClientExtensionsExternalReferenceCodes();

		JSONArray globalJSCETJSONArray = JSONFactoryUtil.createJSONArray();

		for (String externalReferenceCode : externalReferenceCodes) {
			GlobalJSCET globalJSCET = (GlobalJSCET)_cetManager.getCET(
				CompanyThreadLocal.getCompanyId(), externalReferenceCode);

			globalJSCETJSONArray.put(
				JSONUtil.put(
					"cetExternalReferenceCode",
					globalJSCET.getExternalReferenceCode()
				).put(
					"name", globalJSCET.getName(_themeDisplay.getLocale())
				));
		}

		return globalJSCETJSONArray;
	}

	private PortletURL _getCETItemSelectorURL(
		boolean multipleSelection, String selectEventName, String type) {

		CETItemSelectorCriterion cetItemSelectorCriterion =
			new CETItemSelectorCriterion();

		cetItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new CETItemSelectorReturnType());
		cetItemSelectorCriterion.setMultipleSelection(multipleSelection);
		cetItemSelectorCriterion.setType(type);

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
			selectEventName, cetItemSelectorCriterion);
	}

	private final CETManager _cetManager;
	private final ConfigurationProvider _configurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final ThemeDisplay _themeDisplay;

}