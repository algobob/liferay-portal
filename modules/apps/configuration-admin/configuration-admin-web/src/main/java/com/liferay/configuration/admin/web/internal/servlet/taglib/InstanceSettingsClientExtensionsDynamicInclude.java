/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.servlet.taglib;

import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.configuration.admin.configuration.InstanceSettingsClientExtensionsConfiguration;
import com.liferay.configuration.admin.web.internal.servlet.taglib.util.HTMLUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Anderson Luiz
 */
public class InstanceSettingsClientExtensionsDynamicInclude {

	public InstanceSettingsClientExtensionsDynamicInclude(
		CETManager cetManager, ConfigurationProvider configurationProvider,
		JSONFactory jsonFactory) {

		_cetManager = cetManager;
		_configurationProvider = configurationProvider;
		_jsonFactory = jsonFactory;
	}

	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String scriptLocation)
		throws IOException {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			InstanceSettingsClientExtensionsConfiguration
				instanceSettingsClientExtensionsConfiguration =
					_getInstanceSettingsClientExtensionsConfiguration();

			String[] globalJSClientExtensionsExternalReferenceCodes =
				instanceSettingsClientExtensionsConfiguration.
					getJSClientExtensionsExternalReferenceCodes();

			List<GlobalJSCET> globalJSCETs = _filterByScriptLocation(
				_fetchGlobalJSCETs(
					globalJSClientExtensionsExternalReferenceCodes),
				scriptLocation);

			for (GlobalJSCET globalJSCET : globalJSCETs) {
				Map<String, String> scriptAttributes = _toAttributesMap(
					globalJSCET, httpServletRequest);

				String htmlJSScript = HTMLUtil.toHTMLJSScript(
					scriptAttributes,
					_toScriptElementAttributes(
						globalJSCET.getScriptElementAttributesJSON()));

				printWriter.print(htmlJSScript);
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			throw new RuntimeException(configurationException);
		}
	}

	private List<GlobalJSCET> _fetchGlobalJSCETs(
		String[] globalJSClientExtensionsExternalReferenceCodes) {

		List<GlobalJSCET> globalJSCETS = new ArrayList<>();

		for (String externalRefenceCode :
				globalJSClientExtensionsExternalReferenceCodes) {

			GlobalJSCET globalJSCET = _getGlobalJSCET(externalRefenceCode);

			if (Objects.nonNull(globalJSCET)) {
				globalJSCETS.add(globalJSCET);
			}
		}

		return globalJSCETS;
	}

	private List<GlobalJSCET> _filterByScriptLocation(
		List<GlobalJSCET> globalJSCETS, String scriptLocation) {

		List<GlobalJSCET> filteredGlobalJSCETs = new ArrayList<>();

		for (GlobalJSCET globalJSCET : globalJSCETS) {
			if (Objects.equals(
					_getCETScriptLocation(globalJSCET), scriptLocation)) {

				filteredGlobalJSCETs.add(globalJSCET);
			}
		}

		return filteredGlobalJSCETs;
	}

	private String _getCETScriptLocation(GlobalJSCET cet) {
		return UnicodePropertiesBuilder.fastLoad(
			cet.getTypeSettings()
		).build(
		).get(
			"scriptLocation"
		);
	}

	private GlobalJSCET _getGlobalJSCET(String externalReferenceCode) {
		return (GlobalJSCET)_cetManager.getCET(
			CompanyThreadLocal.getCompanyId(), externalReferenceCode);
	}

	private InstanceSettingsClientExtensionsConfiguration
			_getInstanceSettingsClientExtensionsConfiguration()
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			InstanceSettingsClientExtensionsConfiguration.class,
			CompanyThreadLocal.getCompanyId());
	}

	private Map<String, String> _toAttributesMap(
		GlobalJSCET globalJSCET, HttpServletRequest httpServletRequest) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				globalJSCET.getTypeSettings()
			).build();

		return HashMapBuilder.put(
			"loadType",
			() -> {
				String loadType = typeSettingsUnicodeProperties.getProperty(
					"loadType");

				if (Objects.equals(loadType, "default")) {
					return StringPool.BLANK;
				}

				return loadType;
			}
		).put(
			"nonce",
			ContentSecurityPolicyNonceProviderUtil.getNonce(httpServletRequest)
		).put(
			"src", globalJSCET.getURL()
		).build();
	}

	private String _toScriptElementAttributes(
		String scriptElementAttributesJSON) {

		StringBuilder stringBuilder = new StringBuilder();

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				scriptElementAttributesJSON);

			Iterator<String> iterator = jsonObject.keys();

			if (!jsonObject.has("data-senna-track")) {
				stringBuilder.append("data-senna-track=\"temporary\" ");
			}

			if (!jsonObject.has("type")) {
				stringBuilder.append("type=\"text/javascript\" ");
			}

			while (iterator.hasNext()) {
				String key = iterator.next();

				if (key.equals("async") || key.equals("defer")) {
					continue;
				}

				Object value = jsonObject.get(key);

				if (value instanceof Boolean) {
					if (value == Boolean.FALSE) {
						continue;
					}

					stringBuilder.append(key);
				}
				else {
					stringBuilder.append(key);
					stringBuilder.append(StringPool.EQUAL);
					stringBuilder.append(StringPool.QUOTE);
					stringBuilder.append(
						HtmlUtil.escapeAttribute((String)value));
					stringBuilder.append(StringPool.QUOTE);
				}

				if (iterator.hasNext()) {
					stringBuilder.append(StringPool.SPACE);
				}
			}
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to parse script element attributes JSON: " +
					scriptElementAttributesJSON,
				jsonException);
		}

		return stringBuilder.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InstanceSettingsClientExtensionsDynamicInclude.class);

	private final CETManager _cetManager;
	private final ConfigurationProvider _configurationProvider;
	private final JSONFactory _jsonFactory;

}