/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.util;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.GradleException;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;

/**
 * @author Andrea Di Giorgi
 */
public class FileUtil extends com.liferay.gradle.util.FileUtil {

	public static File getJavaClassesDir(SourceSet sourceSet) {
		SourceDirectorySet sourceDirectorySet = sourceSet.getJava();

		Provider<Directory> provider = sourceDirectorySet.getClassesDirectory();

		Directory directory = provider.getOrNull();

		if (directory == null) {
			return null;
		}

		return directory.getAsFile();
	}

	public static List<String> getRelativePaths(
			final Path path, final String fileName, final List<String> excludes,
			final boolean childrenOnly)
		throws IOException {

		final List<String> paths = new ArrayList<>();

		Files.walkFileTree(
			path,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (childrenOnly && dirPath.equals(path)) {
						return FileVisitResult.CONTINUE;
					}

					if (_isExcludedDirName(dirPath.getFileName(), excludes)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(dirPath.resolve(fileName))) {
						Path relativePath = path.relativize(dirPath);

						paths.add(relativePath.toString());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return paths;
	}

	public static void moveTree(File sourceRootDir, File destinationRootDir) {
		try {
			_moveTree(sourceRootDir.toPath(), destinationRootDir.toPath());
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	public static String read(File file) {
		try {
			return new String(
				Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private static boolean _isExcludedDirName(
		Path path, List<String> excludes) {

		String dirName = String.valueOf(path);

		if (dirName == null) {
			return false;
		}

		if (excludes.contains(dirName)) {
			return true;
		}

		return false;
	}

	private static void _moveTree(
			final Path sourceRootDirPath, final Path destinationRootDirPath)
		throws IOException {

		Files.walkFileTree(
			sourceRootDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					Files.delete(dirPath);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path relativePath = sourceRootDirPath.relativize(dirPath);

					Path destinationDirPath = destinationRootDirPath.resolve(
						relativePath);

					Files.createDirectories(destinationDirPath);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static List<String> getMatchingPaths(Path basePath, String glob) {
		FileSystem fileSystem = basePath.getFileSystem();

		AtomicReference<String> queryStringAtomicReference =
			new AtomicReference<>("");

		int index = glob.indexOf("?");

		if (index != -1) {
			queryStringAtomicReference.set(glob.substring(index));

			glob = glob.substring(0, index);
		}

		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + glob);

		try (Stream<Path> files = Files.walk(basePath)) {
			List<String> matchingPaths = files.map(
				basePath::relativize
			).filter(
				pathMatcher::matches
			).map(
				path -> path + queryStringAtomicReference.get()
			).collect(
				Collectors.toList()
			);

			if (matchingPaths.isEmpty()) {
				throw new GradleException(
					"No paths matched the glob pattern \"" + glob + "\"");
			}

			Collections.sort(matchingPaths);

			return matchingPaths;
		}
		catch (IOException ioException) {
			throw new GradleException(
				"Unable to expand wildcard paths", ioException);
		}
	}

}