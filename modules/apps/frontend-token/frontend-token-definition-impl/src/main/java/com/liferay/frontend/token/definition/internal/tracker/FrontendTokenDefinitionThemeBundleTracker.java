/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.tracker;

import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLUtil;

import java.io.IOException;

import java.net.URL;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Anderson Luiz
 */
@Component(service = FrontendTokenDefinitionThemeBundleTracker.class)
public class FrontendTokenDefinitionThemeBundleTracker
	implements BundleTrackerCustomizer<FrontendTokenDefinitionImpl> {

	@Override
	public FrontendTokenDefinitionImpl addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		FrontendTokenDefinitionImpl frontendTokenDefinitionImpl =
			_getFrontendTokenDefinitionImpl(bundle);

		if ((frontendTokenDefinitionImpl != null) &&
			(frontendTokenDefinitionImpl.getThemeId() != null)) {

			_frontendTokenDefinitionImpls.put(
				frontendTokenDefinitionImpl.getThemeId(),
				frontendTokenDefinitionImpl);

			return frontendTokenDefinitionImpl;
		}

		return null;
	}

	public Map<String, FrontendTokenDefinitionImpl>
		getFrontendTokenDefinitions() {

		return _frontendTokenDefinitionImpls;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		FrontendTokenDefinitionImpl frontendTokenDefinitionImpl) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		FrontendTokenDefinitionImpl frontendTokenDefinitionImpl) {

		_frontendTokenDefinitionImpls.remove(
			frontendTokenDefinitionImpl.getThemeId());
	}

	protected String getServletContextName(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String webContextPath = headers.get("Web-ContextPath");

		if (webContextPath == null) {
			return null;
		}

		if (webContextPath.startsWith(StringPool.SLASH)) {
			webContextPath = webContextPath.substring(1);
		}

		return webContextPath;
	}

	protected String getThemeId(Bundle bundle) {
		URL url = bundle.getEntry("WEB-INF/liferay-look-and-feel.xml");

		if (url == null) {
			return null;
		}

		try {
			String xml = URLUtil.toString(url);

			xml = xml.replaceAll(StringPool.NEW_LINE, StringPool.SPACE);

			Matcher matcher = _themeIdPattern.matcher(xml);

			if (!matcher.matches()) {
				return null;
			}

			String themeId = matcher.group(1);

			String servletContextName = getServletContextName(bundle);

			if (servletContextName != null) {
				themeId =
					themeId + PortletConstants.WAR_SEPARATOR +
						servletContextName;
			}

			return _portal.getJsSafePortletId(themeId);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read WEB-INF/liferay-look-and-feel.xml",
				ioException);
		}
	}

	private String _extractBundleFrontendTokenDefinitionJSON(Bundle bundle) {
		URL url = bundle.getEntry("WEB-INF/frontend-token-definition.json");

		if (url == null) {
			return null;
		}

		try {
			return URLUtil.toString(url);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read WEB-INF/frontend-token-definition.json",
				ioException);
		}
	}

	private FrontendTokenDefinitionImpl _getFrontendTokenDefinitionImpl(
		Bundle bundle) {

		String json = _extractBundleFrontendTokenDefinitionJSON(bundle);

		if (json == null) {
			return null;
		}

		String themeId = getThemeId(bundle);

		try {
			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundle.getSymbolicName());

			if (resourceBundleLoader == null) {
				resourceBundleLoader =
					ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
			}

			return new FrontendTokenDefinitionImpl(
				_jsonFactory.createJSONObject(json), _jsonFactory,
				resourceBundleLoader, themeId);
		}
		catch (JSONException | RuntimeException exception) {
			_log.error(
				"Unable to parse frontend token definitions for theme " +
					themeId,
				exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionThemeBundleTracker.class);

	private static final Pattern _themeIdPattern = Pattern.compile(
		".*<theme id=\"([^\"]*)\"[^>]*>.*");

	private final Map<String, FrontendTokenDefinitionImpl>
		_frontendTokenDefinitionImpls = new ConcurrentHashMap<>();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}