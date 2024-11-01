package com.liferay.style.book.internal.exporter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.style.book.exporter.StyleBookEntryExporter;
import com.liferay.style.book.model.StyleBookEntry;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import java.io.File;
import java.util.List;

@Component( service = StyleBookEntryExporter.class)
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
