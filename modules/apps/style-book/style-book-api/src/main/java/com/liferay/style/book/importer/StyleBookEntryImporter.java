package com.liferay.style.book.importer;

import java.io.File;
import java.util.List;

public interface StyleBookEntryImporter {

	List<StyleBookEntryImporterImportResultEntry> importStyleBookEntries(
		long userId, long groupId, File file, boolean overwrite) throws Exception;
}