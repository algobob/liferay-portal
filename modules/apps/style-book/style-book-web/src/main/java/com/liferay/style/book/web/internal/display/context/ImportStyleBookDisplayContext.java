/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.style.book.importer.StyleBookEntryImporterImportResultEntry;

import javax.portlet.RenderRequest;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ImportStyleBookDisplayContext {

	public ImportStyleBookDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public List<String> getStyleBookImporterResultEntryNames(
		StyleBookEntryImporterImportResultEntry.Status status) {

		List<StyleBookEntryImporterImportResultEntry>
			styleBookImporterResultEntries =
				_getStyleBookImporterResultEntryNames();

		if (ListUtil.isEmpty(styleBookImporterResultEntries)) {
			return null;
		}

		return TransformUtil.transform(
			styleBookImporterResultEntries,
			styleBookEntryImporterImportResultEntry -> {
				if (styleBookEntryImporterImportResultEntry.getStatus() !=
					status) {

					return null;
				}

				return styleBookEntryImporterImportResultEntry.getName();
			});
	}

	private List<StyleBookEntryImporterImportResultEntry>
	_getStyleBookImporterResultEntryNames() {

		if (_styleBookImporterResultEntries != null) {
			return _styleBookImporterResultEntries;
		}

		_styleBookImporterResultEntries =
			(List<StyleBookEntryImporterImportResultEntry>)
				SessionMessages.get(
					_renderRequest,
					"styleBookImporterResultEntries");

		return _styleBookImporterResultEntries;
	}

	private final RenderRequest _renderRequest;
	private List<StyleBookEntryImporterImportResultEntry>
		_styleBookImporterResultEntries;

}