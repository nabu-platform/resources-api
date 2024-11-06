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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLConnection;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import be.nabu.libs.resources.api.Archive;
import be.nabu.libs.resources.api.ArchiveResolver;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceResolver;

public class ResourceFactory {

	private static ResourceFactory instance;
	
	private String defaultScheme = System.getProperty("be.nabu.libs.resources.defaultScheme", "file");
	
	public static ResourceFactory getInstance() {
		if (instance == null) {
			synchronized(ResourceFactory.class) {
				if (instance == null) {
					instance = new ResourceFactory();
				}
			}
		}
		return instance;
	}
	
	private Map<String, ResourceResolver> resolvers = new LinkedHashMap<String, ResourceResolver>();
	
	public void setSchemeResolver(String scheme, ResourceResolver resolver, boolean overrideExisting) {
		if (overrideExisting || !resolvers.containsKey(scheme))
			resolvers.put(scheme, resolver);
	}
	
	public void addResourceResolver(ResourceResolver resolver) {
		for (String scheme : resolver.getDefaultSchemes())
			setSchemeResolver(scheme, resolver, false);
	}
	
	public void removeResourceResolver(ResourceResolver resolver) {
		for (String scheme : resolvers.keySet())
			resolvers.remove(scheme);
	}
	
	public ResourceResolver getResolver(String scheme) {
		return getResolvers().get(scheme);
	}
	
	public Set<String> getSchemes() {
		return getResolvers().keySet();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ResourceResolver> getResolvers() {
		if (resolvers.isEmpty()) {
			synchronized(this) {
				if (resolvers.isEmpty()) {
					Map<String, ResourceResolver> resolvers = new LinkedHashMap<String, ResourceResolver>();
					try {
						// let's try this with custom service loading based on a configuration
						Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
						Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
						for (ResourceResolver resolver : (List<ResourceResolver>) declaredMethod.invoke(null, ResourceResolver.class)) {
							for (String scheme : resolver.getDefaultSchemes()) {
								resolvers.put(scheme, resolver);
							}
						}
					}
					catch (ClassNotFoundException e) {
						// ignore, the framework is not present
					}
					catch (NoSuchMethodException e) {
						// corrupt framework?
						throw new RuntimeException(e);
					}
					catch (SecurityException e) {
						throw new RuntimeException(e);
					}
					catch (IllegalAccessException e) {
						// ignore
					}
					catch (InvocationTargetException e) {
						// ignore
					}
					if (resolvers.isEmpty()) {
						ServiceLoader<ResourceResolver> serviceLoader = ServiceLoader.load(ResourceResolver.class);
						for (ResourceResolver resolver : serviceLoader) {
							for (String scheme : resolver.getDefaultSchemes()) {
								resolvers.put(scheme, resolver);
							}
						}
					}
					this.resolvers.putAll(resolvers);
				}
			}
		}
		return resolvers;
	}
	
	public Resource resolve(URI uri, Principal principal) throws IOException {
		Resource result;
		// it is possible to return null so for instance you might want to check if something exists and if not, create it
		// the "mkdir()" functionality has the ability to scan further up the tree to find something
		if (getResolvers().containsKey(uri.getScheme()))
			result = getResolvers().get(uri.getScheme()).getResource(uri, principal);
		else if (getResolvers().containsKey(defaultScheme)) 
			result = getResolvers().get(defaultScheme).getResource(uri, principal);
		else
			throw new IllegalArgumentException("The scheme " + uri.getScheme() + " has no registered handler");
		URI archiveUri = uri;
		String childPath = null;
		// we could not resolve it, let's check if we have an archive along the way
		// to detect archives, we need at least a content type, which in turn requires an extension to guesstimate
		try {
			// TODO: put this in a while loop, move the archiveURI to test multiple paths
			if (result == null && archiveUri != null && archiveUri.getPath() != null && archiveUri.getPath().matches(".*/[^/]+\\.[^/]+/.*")) {
				String resourceName = uri.getPath().replaceFirst("^.*/[^/]+\\.[^/]+/(.*)$", "$1");
				String parentPath = uri.getPath().replaceFirst("^(.*/[^/]+\\.[^/]+)/.*$", "$1");
				uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), parentPath, uri.getQuery(), uri.getFragment());
				childPath = childPath == null ? resourceName : childPath + "/" + resourceName;
				String contentType = URLConnection.guessContentTypeFromName(parentPath);
				ArchiveResolver resolver = ArchiveFactory.getInstance().getResolver(contentType);
				if (resolver != null) {
					Resource archiveResource = resolve(uri, principal);
					Archive<Resource> archive = resolver.newInstance();
					archive.setSource(archiveResource);
					result = ResourceUtils.resolve(archive, childPath);
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private void activate() {
		instance = this;
	}
	@SuppressWarnings("unused")
	private void deactivate() {
		instance = null;
	}
}
