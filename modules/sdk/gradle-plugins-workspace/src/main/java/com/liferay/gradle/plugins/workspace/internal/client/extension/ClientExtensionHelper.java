package com.liferay.gradle.plugins.workspace.internal.client.extension;

import com.liferay.gradle.plugins.workspace.internal.util.FileUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.GradleException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientExtensionHelper {

	public ClientExtensionHelper() {

	}
	public boolean isWildcardValue(String value) {
		if (value.contains(StringUtil.STAR)) {
			return true;
		}

		return false;
	}

	public void expandWildcards(Path dirPath, Map<String, Object> typeSettings) {
		for (Map.Entry<String, Object> entry : typeSettings.entrySet()) {
			Object currentValue = entry.getValue();

			if (currentValue instanceof String) {
				String currentValueString = (String)currentValue;

				if (StringUtil.containsIgnoreCase(entry.getKey(), "url") &&
					this.isWildcardValue(currentValueString)) {

					entry.setValue(FileUtil.getMatchingPaths(dirPath, (String)currentValue));
				}
			}

			if (currentValue instanceof List) {
				List<String> values = new ArrayList<>();

				for (String value : (List<String>)currentValue) {
					if (isWildcardValue(value)) {
						values.addAll(FileUtil.getMatchingPaths(dirPath, value));
					}
					else {
						values.add(value);
					}
				}

				entry.setValue(values);
			}
		}
	}

}
