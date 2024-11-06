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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.Transformer;
import be.nabu.libs.resources.api.TransformerResolver;

public class TransformerFactory {

	private static TransformerFactory instance;
	
	public static TransformerFactory getInstance() {
		if (instance == null)
			instance = new TransformerFactory();
		return instance;
	}
	
	private Map<String, TransformerResolver> resolvers = new LinkedHashMap<String, TransformerResolver>();
	
	public void setTransformerResolver(String contentType, TransformerResolver resolver, boolean overrideExisting) {
		if (overrideExisting || !resolvers.containsKey(contentType))
			resolvers.put(contentType, resolver);
	}
	
	public void addTransformerResolver(TransformerResolver resolver) {
		for (String contentType : resolver.getSupportedContentTypes())
			setTransformerResolver(contentType, resolver, false);
	}
	
	public void removeTransformerResolver(TransformerResolver resolver) {
		for (String contentType : resolvers.keySet())
			resolvers.remove(contentType);
	}
	
	public TransformerResolver getResolver(String scheme) {
		return getResolvers().get(scheme);
	}
	
	public Set<String> getSchemes() {
		return getResolvers().keySet();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, TransformerResolver> getResolvers() {
		if (resolvers.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				for (TransformerResolver resolver : (List<TransformerResolver>) declaredMethod.invoke(null, TransformerResolver.class)) {
					addTransformerResolver(resolver);
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
				ServiceLoader<TransformerResolver> serviceLoader = ServiceLoader.load(TransformerResolver.class);
				for (TransformerResolver resolver : serviceLoader) {
					addTransformerResolver(resolver);
				}
			}
		}
		return resolvers;
	}
	
	public Transformer newArchive(Resource resource) throws IOException {
		if (getResolvers().containsKey(resource.getContentType())) {
			Transformer transformer = resolvers.get(resource.getContentType()).newInstance();
			transformer.setSource(resource);
			return transformer;
		}
		else
			throw new IllegalArgumentException("The content type " + resource.getContentType() + " has no registered archive handler");
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
