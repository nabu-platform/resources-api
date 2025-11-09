package be.nabu.libs.resources.impl;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import be.nabu.libs.resources.api.ResourceContainer;

// based on the rules for .gitignore
public class ResourceIgnoreCalculator {
	/**
	 * Checks if a file should be accepted (i.e., NOT ignored) based on ignore rules
	 * from this directory and all parent directories.
	 *
	 * @param directory The directory containing the file.
	 * @param file      The file's name (e.g., "App.java").
	 * @return true if the file is accepted (not ignored), false if it is ignored.
	 */
	public static boolean accept(ResourceContainer<?> directory, String file) {
		// We need to check rules from the root down to the current directory.

		// 1. Build a map of [Directory -> relative/path/to/file]
		// We use LinkedHashMap to preserve insertion order (child-to-root)
		Map<ResourceContainer<?>, String> pathsToTest = new LinkedHashMap<>();
		ResourceContainer<?> currentDir = directory;
		String currentPath = file;

		while (currentDir != null) {
			pathsToTest.put(currentDir, currentPath);

			// For the next loop (parent dir), prepend the current dir's name
			// Check for parent != null to avoid prepending root's name (if it's "")
			if (currentDir.getParent() != null) {
				currentPath = currentDir.getName() + "/" + currentPath;
			}
			currentDir = currentDir.getParent();
		}

		// 2. Get the directories in ROOT-TO-CHILD order (lowest to highest precedence)
		List<ResourceContainer<?>> orderedDirs = new ArrayList<>(pathsToTest.keySet());
		Collections.reverse(orderedDirs); // Now in [root, ..., src, main] order

		boolean isIgnored = false; // Default: not ignored

		// 3. Process rules from root-down
		for (ResourceContainer<?> dir : orderedDirs) {
			String path = pathsToTest.get(dir); // Get the path relative to this dir
			isIgnored = processRules(dir.getIgnoreRules(), path, isIgnored);
		}

		// 'accept' means 'NOT ignored'
		return !isIgnored;
	}

	/**
	 * Processes a list of rules against a path, updating the ignore status.
	 *
	 * @param rules               List of raw rule strings.
	 * @param path                The relative path to test (e.g.,
	 *                            "src/main/App.java").
	 * @param currentIgnoreStatus The ignore status from the parent directory.
	 * @return The new ignore status.
	 */
	private static boolean processRules(List<String> rules, String path, boolean currentIgnoreStatus) {
		boolean isIgnored = currentIgnoreStatus;
		for (String rawRule : rules) {
			String rule = rawRule.trim();

			// Skip comments and empty lines
			if (rule.isEmpty() || rule.startsWith("#")) {
				continue;
			}

			boolean isNegation = rule.startsWith("!");
			if (isNegation) {
				rule = rule.substring(1); // Remove "!"
			}

			if (matches(rule, path)) {
				// A matching rule updates the status.
				// If it's a negation, it's NOT ignored.
				// If it's an ignore, it IS ignored.
				isIgnored = !isNegation;
			}
		}
		return isIgnored;
	}

	/**
	 * Checks if a .gitignore pattern matches a given path.
	 *
	 * NOTE: This is a SIMPLIFIED matcher. A full .gitignore implementation is very
	 * complex. This handles basic wildcards and directory matching. It uses the
	 * Java NIO PathMatcher.
	 *
	 * @param pattern e.g., "*.log", "build/", "src/**.java"
	 * @param path    e.g., "Main.java", "build/output.txt"
	 */
	private static boolean matches(String pattern, String path) {
		String originalPattern = pattern;
		// Handle directory-only patterns (e.g., "build/")
		if (pattern.endsWith("/")) {
			pattern = pattern.substring(0, pattern.length() - 1); // Remove /
			// Matches "build" exactly or "build/..."
			if (path.equals(pattern) || path.startsWith(pattern + "/")) {
				return true;
			}
		}

		// Handle patterns with no slashes (e.g., "*.log")
		// These match any file in any directory.
		if (!originalPattern.contains("/")) {
			if (!originalPattern.contains("*") && !originalPattern.contains("?") && !originalPattern.contains("{")) {
				return Paths.get(path).getFileName().toString().equals(originalPattern);
			}
			// We check against the file's name OR the full path
			// 'glob:**/' matches 0 or more directories
			pattern = "**/" + pattern;
		}
		// Handle anchored paths (e.g., "/src")
		// These only match from the root of *this* .gitignore file
		else if (pattern.startsWith("/")) {
			pattern = pattern.substring(1);
		}

		// Use Java's built-in glob matcher
		try {
			// We must use "/" as the separator, even on Windows
			String pathForMatcher = path.replace("\\", "/");
			String patternForMatcher = pattern.replace("\\", "/");

			PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + patternForMatcher);
			if (matcher.matches(Paths.get(pathForMatcher))) {
				return true;
			}
			if (!originalPattern.contains("/") && originalPattern.contains("*")) {
				PathMatcher nameMatcher = FileSystems.getDefault().getPathMatcher("glob:" + originalPattern);
				return nameMatcher.matches(Paths.get(pathForMatcher).getFileName());
			}
			return false;
		}
		catch (Exception e) {
			// Invalid pattern syntax
			return false;
		}
	}
}
