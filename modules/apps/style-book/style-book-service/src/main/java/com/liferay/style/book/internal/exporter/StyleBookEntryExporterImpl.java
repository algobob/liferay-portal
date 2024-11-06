/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.exporter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.style.book.exporter.StyleBookEntryExporter;
import com.liferay.style.book.model.StyleBookEntry;

import java.io.File;

import java.util.List;

import javax.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Anderson Luiz
 */
@Component(service = StyleBookEntryExporter.class)
public class StyleBookEntryExporterImpl implements StyleBookEntryExporter {

	@Override
	public File exportStyleBookEntries(List<StyleBookEntry> styleBookEntries)
		throws PortletException {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		try {
			for (StyleBookEntry styleBookEntry : styleBookEntries) {
				styleBookEntry.populateZipWriter(zipWriter, StringPool.BLANK);
			}

			return zipWriter.getFile();
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}