package be.nabu.libs.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIUtils {
	
	public static String encodeURI(String uri) {
		if (uri != null) {
			uri = uri.replace("%", "%25");
			uri = uri.replace(" ", "%20");
			uri = uri.replace("{", "%7B");
			uri = uri.replace("}", "%7D");
			uri = uri.replace("|", "%7C");
			uri = uri.replace("^", "%5E");
			uri = uri.replace("[", "%5B");
			uri = uri.replace("]", "%5D");
			// as per 2.4.3 of RFC 2396 characters "`" (%60) and "\" (%5C) should also be encoded
			// skipping for unix/windows reasons atm until we absolutely need it
//			uri = uri.replace("\\", "%5C");
		}
		return uri;
	}
	
	public static String encodeURIComponent(String uriComponent) {
		if (uriComponent != null) {
			uriComponent = encodeURI(uriComponent);
			uriComponent = uriComponent.replace("/", "%2F");
			uriComponent = uriComponent.replace(":", "%3A");
			uriComponent = uriComponent.replace("?", "%3F");
			uriComponent = uriComponent.replace("&", "%26");
			uriComponent = uriComponent.replace("+", "%2B");
			uriComponent = uriComponent.replace("=", "%3D");
			uriComponent = uriComponent.replace("#", "%23");
			uriComponent = uriComponent.replace("@", "%40");
		}
		return uriComponent;
	}
	
	public static String URLEncodingToURIEncoding(String urlEncoded) {
		if (urlEncoded != null) {
			urlEncoded = urlEncoded.replace("+", "%20");
		}
		return urlEncoded;
	}
	
	public static String relativize(String rootPath, String childPath) {
		try {
			URI rootURI = new URI(encodeURI(rootPath));
			URI childURI = new URI(encodeURI(childPath));
			String relativePath = rootURI.relativize(childURI).getPath();
			return relativePath.equals(childPath)
				? null
				: relativePath;
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String, List<String>> getQueryProperties(URI uri) {
		Map<String, List<String>> properties = new HashMap<String, List<String>>();
		if (uri.getQuery() == null)
			return properties;
		for (String part : uri.getQuery().split("&")) {
			String [] parts = part.split("=");
			if (!properties.containsKey(parts[0]))
				properties.put(parts[0], new ArrayList<String>());
			properties.get(parts[0]).add(parts.length > 1 ? parts[1] : null);
		}
		return properties;
	}
	
	public static String normalize(String path) {
		// remove all "." references which point to the path itself
		while (path.contains("/./"))
			path = path.replaceAll("/\\./", "/");
		// remove all parts that are followed by a ".." which means go back by one
		while (path.contains("/../")) {
			if (path.startsWith("/../"))
				throw new IllegalArgumentException("An absolute path can not start by referencing a non-existent parent");
			path = path.replaceAll("[^/]+(?<!/\\.\\.)/\\.\\./", "");
		}
		return path;
	}
	
	public static URI getParent(URI uri) {
		String path = uri.getPath();
		if (path.equals("/") || path.isEmpty())
			return null;
		else
			// remove the last bit
			path = path.replaceAll("/[^/]+$", "");
		if (path.equals(""))
			path = "/";
		try {
			return new URI(uri.getScheme(), uri.getHost(), path, uri.getFragment());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static URI getChild(URI parent, String name) {
		String path = parent.getPath();
		if (path.equals("/") || path.isEmpty())
			path += name;
		else
			path += "/" + name;
		try {
			return new URI(parent.getScheme(), parent.getHost(), path, parent.getFragment());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getName(URI uri) {
		String path = uri.getPath();
		if (path.equals("/"))
			return null;
		else
			return path.replaceAll(".*/", "");
	}
}
