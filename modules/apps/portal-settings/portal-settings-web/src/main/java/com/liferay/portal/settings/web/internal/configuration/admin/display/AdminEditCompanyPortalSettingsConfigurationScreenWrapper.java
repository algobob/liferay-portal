/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.web.internal.configuration.admin.display;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenContributor;
import com.liferay.portal.settings.configuration.admin.display.PortalSettingsConfigurationScreenFactory;
import com.liferay.portal.settings.web.internal.constants.PortalSettingsWebKeys;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = ConfigurationScreen.class)
public class AdminEditCompanyPortalSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _portalSettingsConfigurationScreenFactory.create(
			new AdminEditCompanyPortalSettingsConfigurationScreenContributor());
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private PortalSettingsConfigurationScreenFactory
		_portalSettingsConfigurationScreenFactory;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.settings.web)"
	)
	private ServletContext _servletContext;

	private class AdminEditCompanyPortalSettingsConfigurationScreenContributor
		implements PortalSettingsConfigurationScreenContributor {

		@Override
		public String getCategoryKey() {
			return "instance-configuration";
		}

		@Override
		public String getJspPath() {
			return "/add_js_client_extension.jsp";
		}

		@Override
		public String getKey() {
			return "admin";
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/portal_settings/edit_company";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			httpServletRequest.setAttribute(CETManager.class.getName(),
				_cetManager);
			httpServletRequest.setAttribute(
				ItemSelector.class.getName(), _itemSelector);
			httpServletRequest.setAttribute(
				ConfigurationProvider.class.getName(), _configurationProvider);
		}

	}

}