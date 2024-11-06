/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.libs.resources;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URIUtils {
	
	private static boolean useJavaURLEncoding = Boolean.parseBoolean(System.getProperty("nabu.url.useJavaEncoding", "true"));
	
	public static String encodeURL(String uri) {
		// the standard (since 2005) says that we should use UTF-8 for this
		// before that it is a bit vague as to which encoding we should use
		// if you need a specific encoding you can always call it directly
		try {
			return URLEncoder.encode(uri, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String decodeURL(String uri) {
		try {
			// if you run this code:
			// System.out.println(URLDecoder.decode("abc%00de", "UTF-8"));
			// it will output "abc"
			if (uri.contains("%00")) {
				throw new RuntimeException("Unsafe string for decoding");
			}
			return URLDecoder.decode(uri, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	// this is aimed at encoding only enough to construct an URI object. when dealing with ascii protocols like HTTP, additional encoding might be necessary. it is advised to use new URI(...).toASCIIString();
	public static String encodeURI(String uri) {
		return encodeURI(uri, true);
	}
	
	public static String encodeURI(String uri, boolean includeEncoded) {
		if (uri != null) {
			if (includeEncoded) {
				uri = uri.replace("%", "%25");
			}
			else {
				uri = uri.replaceAll("%(?![0-9A-F]{2})", "%25");
			}
			uri = uri.replace(" ", "%20");
			uri = uri.replace("{", "%7B");
			uri = uri.replace("}", "%7D");
			uri = uri.replace("|", "%7C");
			uri = uri.replace("^", "%5E");
			uri = uri.replace("[", "%5B");
			uri = uri.replace("]", "%5D");
			// as per 2.4.3 of RFC 2396 characters "`" (%60) and "\" (%5C) should also be encoded
			uri = uri.replace("\\", "%5C");
			uri = uri.replace("`", "%60");
			uri = uri.replace(">", "%3E");
			uri = uri.replace("<", "%3C");
			uri = uri.replace("\"", "%22");
		}
		return uri;
	}
	
	public static String decodeURI(String uri) {
		if (uri != null) {
			uri = uri.replace("%20", " ");
			uri = uri.replace("%7B", "{");
			uri = uri.replace("%7D", "}");
			uri = uri.replace("%7C", "|");
			uri = uri.replace("%5E", "^");
			uri = uri.replace("%5B", "[");
			uri = uri.replace("%5D", "]");
			uri = uri.replace("%25", "%");
			uri = uri.replace("%3E", ">");
			uri = uri.replace("%3C", "<");
			uri = uri.replace("%22", "\"");
		}
		return uri;
	}
	
	public static String encodeURIComponent(String uriComponent) {
		return encodeURIComponent(uriComponent, true);
	}
	
	public static URI buildUri(String scheme, String userInfo, String authority, String host, Integer port, String path, String query, String fragment) {
		StringBuilder builder = new StringBuilder();
		if (scheme != null) {
			builder.append(scheme + ":");
		}
		if (userInfo != null || authority != null || host != null) {
			builder.append("//");
		}
		if (userInfo != null) {
			builder.append(URIUtils.encodeURIComponent(userInfo) + "@");
		}
		if (host != null) {
			builder.append(host);
			if (port != null && port >= 0) {
				builder.append(":" + port);
			}
		}
		else if (authority != null) {
			builder.append(authority);
		}
		if (path != null) {
			// already encode most of it
			path = URIUtils.encodeURI(path);
			/**
			 * @2023-09-26
			 * 
			 * if we have nothing so far, we CAN have a relative path
			 * however, that relative path must NOT have a ":" in the first part
			 * 
			 * https://www.rfc-editor.org/rfc/rfc3986.html:
			 * 
			 * In addition, a URI reference (Section 4.1) may be a relative-path reference, in which case the first path segment cannot contain a colon (":") character.
			 * 
			 * This works:
			 * 
			 * uri = new URI("test :this.pdf".replaceAll(":", "%3A").replaceAll(" ", "%20"));
			 * 
			 * This does not work:
			 * 
			 * URI uri = new URI(
					null,
					null,
					null,
					-1,
					"test :this.pdf",
					null,
					null
				);
				
				This means we need to identify when we have a relative path uri with a : in the first part and encode it explicitly
			 */
			if (builder.toString().isEmpty() && !path.startsWith("/")) {
				int index = path.indexOf(':');
				if (index >= 0) {
					int slashIndex = path.indexOf('/');
					// if we don't have a slash, we need to encode all ":"
					if (slashIndex < 0) {
						path = path.replace(":", "%3A");
					}
					// if our first slash is after the ":", we need to encode all the ":" before that first slash
					if (slashIndex > index) {
						path = path.substring(0, slashIndex).replace(":", "%3A") + path.substring(slashIndex);
					}
				}
			}
			// if we have anything BEFORE the path, we need to add a leading "/", no relative paths allowed
			else if (!builder.toString().isEmpty() && !path.startsWith("/")) {
				path = "/" + path;
			}
			builder.append(path);
		}
		if (query != null) {
			builder.append("?" + URIUtils.encodeURI(query));
		}
		if (fragment != null) {
			builder.append("#" + URIUtils.encodeURI(fragment));
		}
		try {
			return new URI(builder.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static String encodeURIComponent(String uriComponent, boolean includeEncoded) {
		if (uriComponent != null) {
			uriComponent = encodeURI(uriComponent, includeEncoded);
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
	
	public static String decodeURIComponent(String uriComponent) {
		if (uriComponent != null) {
			uriComponent = uriComponent.replace("%2F", "/");
			uriComponent = uriComponent.replace("%3A", ":");
			uriComponent = uriComponent.replace("%3F", "?");
			uriComponent = uriComponent.replace("%26", "&");
			uriComponent = uriComponent.replace("%2B", "+");
			uriComponent = uriComponent.replace("%3D", "=");
			uriComponent = uriComponent.replace("%23", "#");
			uriComponent = uriComponent.replace("%40", "@");
			uriComponent = decodeURI(uriComponent);
		}
		return uriComponent;
	}
	
	public static String decodeHTMLComponent(String htmlComponent) {
		if (htmlComponent != null) {
			htmlComponent = htmlComponent.replace("+", " ");
			htmlComponent = decodeURIComponent(htmlComponent);
		}
		return htmlComponent;
	}
	
	public static String URLEncodingToURIEncoding(String urlEncoded) {
		if (urlEncoded != null) {
			urlEncoded = urlEncoded.replace("+", "%20");
		}
		return urlEncoded;
	}
	
	public static String URIEncodingToURLEncoding(String uriEncoded) {
		if (uriEncoded != null) {
			uriEncoded = uriEncoded.replace("%20", "+");
		}
		return uriEncoded;
	}
	
	public static String relativize(String rootPath, String childPath) {
		try {
			rootPath = cleanPath(rootPath);
			childPath = cleanPath(childPath);
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
	
	private static String cleanPath(String path) {
		return path.replaceAll("[/]{2,}", "/");
	}
	
	public static Map<String, List<String>> getQueryProperties(URI uri) {
		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		if (uri.getQuery() != null) {
			for (String part : uri.getRawQuery().split("[\\s]*&[\\s]*")) {
				int index = part.indexOf('=');
				String key = null;
				String value = null;
				if (index < 0)
					key = part.trim();
				else {
					key = part.substring(0, index).trim();
					value = useJavaURLEncoding
						? decodeURL(part.substring(index + 1).trim())
						: decodeURIComponent(URLEncodingToURIEncoding(part.substring(index + 1).trim()));
				}
				// decode the key as well in case it contains special characters
				key = useJavaURLEncoding
					? decodeURL(key)
					: decodeURIComponent(URLEncodingToURIEncoding(key));
				if (!parameters.containsKey(key))
					parameters.put(key, new ArrayList<String>());
				if (value != null)
					parameters.get(key).add(value);
			}
		}
		return parameters;
	}
	
	public static URI normalize(URI uri) {
		try {
			return new URI(uri.getScheme(), uri.getAuthority(), normalize(uri.getPath()), uri.getQuery(), uri.getFragment());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String normalize(String path) {
		path = cleanPath(path);
		// remove all "." references which point to the path itself
		path = path.replaceAll("/\\.(/|$)", "/");

		int length = path.length();
		Pattern pattern = Pattern.compile("/\\.\\.(?=/|$)");
		Matcher matcher = pattern.matcher(path);
		// remove all parts that are followed by a ".." which means go back by one
		while (matcher.find()) {
			String original = path;
			path = path.replaceFirst("/[^/]+/\\.\\.(?=/|$)", matcher.regionEnd() == length ? "" : "/");
			if (original.equals(path)) {
				throw new IllegalArgumentException("Can not resolve relative path: " + original);
			}
		}
		if (path.matches("/\\.\\.(/|$)")) {
			throw new IllegalArgumentException("Can not resolve relative path");
		}
		return path;
	}
	
	public static URI getParent(URI uri) {
		// if the path is null, we assume that you are using a URL within another URI, for example test1:test2:/path/here
		// to fix this we get the scheme specific part
		if (uri.getPath() == null) {
			try {
				URI child = new URI(uri.getSchemeSpecificPart());
				URI childParent = getParent(child);
				return new URI(uri.getScheme(), childParent.toString(), uri.getFragment());
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			String path = cleanPath(uri.getPath());
			if (path.equals("/") || path.isEmpty())
				return null;
			else
				// first remove any trailing "/", then remove the last bit
				path = path.replaceAll("[/]+$", "").replaceAll("/[^/]+$", "");
			if (path.equals(""))
				path = "/";
			try {
				return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, uri.getQuery(), uri.getFragment());
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static URI getChild(URI parent, String name) {
		String path = parent.getPath() == null ? "/" : cleanPath(parent.getPath());
		if (path.equals("/") || path.isEmpty())
			path += name;
		else
			path += "/" + name.replaceAll("^[/]+", "");
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		try {
			return new URI(parent.getScheme(), parent.getAuthority(), path, parent.getQuery(), parent.getFragment());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getName(URI uri) {
		String path = uri.getPath();
		while (path == null && uri.getSchemeSpecificPart() != null) {
			try {
				uri = new URI(encodeURI(uri.getSchemeSpecificPart()));
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
			path = uri.getPath();
		}
		path = cleanPath(path);
		if (path.equals("/"))
			return null;
		else
			// first remove any trailing
			return path.replaceAll("[/]+$", "").replaceAll(".*/", "");
	}
}
