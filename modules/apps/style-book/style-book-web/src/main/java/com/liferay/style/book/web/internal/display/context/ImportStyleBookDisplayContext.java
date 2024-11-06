/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.style.book.importer.StyleBookEntryImporterEntry;

import java.util.List;

import javax.portlet.RenderRequest;

/**
 * @author Eudaldo Alonso
 */
public class ImportStyleBookDisplayContext {

	public ImportStyleBookDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public List<String> getStyleBookEntryImporterEntryNames(
		StyleBookEntryImporterEntry.Status status) {

		List<StyleBookEntryImporterEntry> styleBookEntryImporterEntries =
			_getStyleBookEntryImporterEntries();

		if (ListUtil.isEmpty(styleBookEntryImporterEntries)) {
			return null;
		}

		return TransformUtil.transform(
			styleBookEntryImporterEntries,
			styleBookEntryImporterEntry -> {
				if (styleBookEntryImporterEntry.getStatus() != status) {
					return null;
				}

				return styleBookEntryImporterEntry.getName();
			});
	}

	private List<StyleBookEntryImporterEntry>
		_getStyleBookEntryImporterEntries() {

		if (_styleBookEntryImporterEntries != null) {
			return _styleBookEntryImporterEntries;
		}

		_styleBookEntryImporterEntries =
			(List<StyleBookEntryImporterEntry>)SessionMessages.get(
				_renderRequest, "styleBookEntryImporterEntries");

		return _styleBookEntryImporterEntries;
	}

	private final RenderRequest _renderRequest;
	private List<StyleBookEntryImporterEntry> _styleBookEntryImporterEntries;

}