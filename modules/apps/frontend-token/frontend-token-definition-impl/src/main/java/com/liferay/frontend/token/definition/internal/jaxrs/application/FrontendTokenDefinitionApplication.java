/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.jaxrs.application;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.frontend.token.definition.internal.validator.FrontendTokenDefinitionValidator;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Anderson Luiz
 * @author Thiago Buarque
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/com-liferay-frontend-token-definition-impl",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=com.liferay.frontend.token.definition.internal.jaxrs.application.FrontendTokenDefinitionApplication",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=false", "liferay.oauth2=false"
	},
	service = Application.class
)
public class FrontendTokenDefinitionApplication extends Application {

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/validate-file")
	@POST
	public Response validateFrontendTokenDefinitionFile(
		@Context HttpServletRequest httpServletRequest) {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-10773")) {
			return Response.serverError(
			).build();
		}

		UploadServletRequest uploadServletRequest =
			_portal.getUploadServletRequest(httpServletRequest);

		File file = uploadServletRequest.getFile("file");

		Locale locale = _portal.getLocale(httpServletRequest);

		try {
			return _validateFrontendTokenDefinitionFile(file, locale);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return _getResponse(
				_language.get(
					locale,
					"an-unexpected-error-occurred-while-uploading-your-file"),
				Response.Status.INTERNAL_SERVER_ERROR);
		}
		catch (JSONException | JSONValidatorException exception) {
			_log.error(exception);

			return _getResponse(
				_language.get(
					locale, "invalid-frontend-token-definition-json-file"),
				Response.Status.BAD_REQUEST);
		}
	}

	private Response _getResponse(String message, Response.Status status) {
		return Response.status(
			status
		).entity(
			HashMapBuilder.put(
				"message", message
			).build()
		).type(
			MediaType.APPLICATION_JSON
		).build();
	}

	private Response _validateFrontendTokenDefinitionFile(
			File file, Locale locale)
		throws IOException, JSONException, JSONValidatorException {

		if ((file == null) || !StringUtil.endsWith(file.getName(), ".json")) {
			return _getResponse(
				_language.get(locale, "please-upload-a-json-file"),
				Response.Status.BAD_REQUEST);
		}

		String frontendTokenDefinitionJSONString = new String(
			Files.readAllBytes(file.toPath()));

		_frontendTokenDefinitionValidator.validateFrontendDefinition(
			frontendTokenDefinitionJSONString);

		FrontendTokenDefinition frontendTokenDefinition =
			new FrontendTokenDefinitionImpl(
				_jsonFactory.createJSONObject(
					frontendTokenDefinitionJSONString),
				_jsonFactory, null, "");

		return _getResponse(
			_language.format(
				locale,
				"json-uploaded-contributing-x-token-categories-x-token-sets-" +
					"and-x-tokens",
				new Object[] {
					frontendTokenDefinition.getFrontendTokenCategories(
					).size(),
					frontendTokenDefinition.getFrontendTokenSets(
					).size(),
					frontendTokenDefinition.getFrontendTokens(
					).size()
				}),
			Response.Status.OK);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionApplication.class);

	private final FrontendTokenDefinitionValidator
		_frontendTokenDefinitionValidator =
			new FrontendTokenDefinitionValidator();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}