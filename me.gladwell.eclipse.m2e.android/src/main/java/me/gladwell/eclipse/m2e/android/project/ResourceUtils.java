package me.gladwell.eclipse.m2e.android.project;

import static org.apache.commons.io.FilenameUtils.normalizeNoEndSeparator;
import static org.apache.commons.io.FilenameUtils.separatorsToUnix;
import static org.apache.commons.io.FilenameUtils.separatorsToWindows;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 
 * Code found here :
 * http://stackoverflow.com/questions/204784/how-to-construct-a
 * -relative-path-in-java-from-two-absolute-paths-or-urls
 * 
 */
public class ResourceUtils {
	public static final String UNIX_SEPARATOR = "/";
	public static final String WINDOWS_SEPARATOR = "\\";

	/**
	 * Get the relative path from one file to another, specifying the
	 * directory separator. If one of the provided resources does not exist,
	 * it is assumed to be a file unless it ends with '/' or '\'.
	 * 
	 * @param target
	 *                targetPath is calculated to this file
	 * @param base
	 *                basePath is calculated from this file
	 * @param separator
	 *                directory separator. The platform default is not
	 *                assumed so that we can test Unix behaviour when
	 *                running on Windows (for example)
	 * @return
	 */
	public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

		// Normalize the paths
		String normalizedTargetPath = normalizeNoEndSeparator(targetPath);
		String normalizedBasePath = normalizeNoEndSeparator(basePath);

		// Undo the changes to the separators made by normalization
		if (pathSeparator.equals(UNIX_SEPARATOR)) {
			normalizedTargetPath = separatorsToUnix(normalizedTargetPath);
			normalizedBasePath = separatorsToUnix(normalizedBasePath);

		} else if (pathSeparator.equals(WINDOWS_SEPARATOR)) {
			normalizedTargetPath = separatorsToWindows(normalizedTargetPath);
			normalizedBasePath = separatorsToWindows(normalizedBasePath);

		} else {
			throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
		}

		String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
		String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer common = new StringBuffer();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + pathSeparator);
			commonIndex++;
		}

		if (commonIndex == 0) {
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and
			// D:.
			// These paths cannot be relativized.
			throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '"
					+ normalizedBasePath + "'");
		}

		// The number of directories we have to backtrack depends on
		// whether the base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		//
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers
		// to a file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist,
		// but it's the best I can do
		boolean baseIsFile = true;

		File baseResource = new File(normalizedBasePath);

		if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();

		} else if (basePath.endsWith(pathSeparator)) {
			baseIsFile = false;
		}

		StringBuffer relative = new StringBuffer();

		if (base.length != commonIndex) {
			int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + pathSeparator);
			}
		}
		relative.append(normalizedTargetPath.substring(common.length()));
		return relative.toString();
	}

	static class PathResolutionException extends RuntimeException {

		private static final long serialVersionUID = 3419478959897841170L;

		PathResolutionException(String msg) {
			super(msg);
		}
	}
}
