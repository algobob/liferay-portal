/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.provider;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Anderson Luiz
 */
@Component(
	property = {
		"configuration.field.name=js-client-extensions-external-reference-codes",
		"configuration.pid=com.liferay.configuration.admin.configuration.InstanceSettingsConfiguration"
	},
	service = ConfigurationFieldOptionsProvider.class
)
public class InstanceSettingsClientExtensionsConfigurationProvider
	implements ConfigurationFieldOptionsProvider {

	@Override
	public List<Option> getOptions() {
		List<GlobalJSCET> globalJSCETS = _fetchJSClientExtensionByCompanyId(
			CompanyThreadLocal.getCompanyId());

		List<Option> options = new ArrayList<>();

		for (GlobalJSCET globalJSCET : globalJSCETS) {
			options.add(_toOption(globalJSCET));
		}

		return options;
	}

	private List<GlobalJSCET> _fetchJSClientExtensionByCompanyId(
		Long companyId) {

		try {
			List<CET> cets = _cetManager.getCETs(
				companyId, ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

			List<GlobalJSCET> globalJSCETs = new ArrayList<>();

			for (CET cet : cets) {
				globalJSCETs.add((GlobalJSCET)cet);
			}

			return globalJSCETs;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyList();
		}
	}

	private Option _toOption(CET cet) {
		return new Option() {

			@Override
			public String getLabel(Locale locale) {
				return cet.getName(locale);
			}

			@Override
			public String getValue() {
				return cet.getExternalReferenceCode();
			}

		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceSettingsClientExtensionsConfigurationProvider.class);

	@Reference
	private CETManager _cetManager;

}