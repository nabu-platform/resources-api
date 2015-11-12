package be.nabu.libs.resources;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import be.nabu.libs.resources.api.Archive;
import be.nabu.libs.resources.api.ArchiveResolver;
import be.nabu.libs.resources.api.Resource;

public class ArchiveFactory {

	private static ArchiveFactory instance;
	
	public static ArchiveFactory getInstance() {
		if (instance == null)
			instance = new ArchiveFactory();
		return instance;
	}
	
	private Map<String, ArchiveResolver> resolvers = new LinkedHashMap<String, ArchiveResolver>();
	
	public void setArchiveResolver(String contentType, ArchiveResolver resolver, boolean overrideExisting) {
		if (overrideExisting || !resolvers.containsKey(contentType))
			resolvers.put(contentType, resolver);
	}
	
	public void addArchiveResolver(ArchiveResolver resolver) {
		for (String contentType : resolver.getSupportedContentTypes())
			setArchiveResolver(contentType, resolver, false);
	}
	
	public void removeArchiveResolver(ArchiveResolver resolver) {
		for (String contentType : resolvers.keySet())
			resolvers.remove(contentType);
	}
	
	public ArchiveResolver getResolver(String contentType) {
		return getResolvers().get(contentType);
	}
	
	public Set<String> getSchemes() {
		return getResolvers().keySet();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ArchiveResolver> getResolvers() {
		if (resolvers.isEmpty()) {
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = getClass().getClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				for (ArchiveResolver resolver : (List<ArchiveResolver>) declaredMethod.invoke(null, ArchiveResolver.class)) {
					addArchiveResolver(resolver);
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
				ServiceLoader<ArchiveResolver> serviceLoader = ServiceLoader.load(ArchiveResolver.class);
				for (ArchiveResolver resolver : serviceLoader) {
					addArchiveResolver(resolver);
				}
			}
		}
		return resolvers;
	}
	
	public <T extends Resource> Archive<T> newArchive(Resource resource) throws IOException {
		if (getResolvers().containsKey(resource.getContentType())) {
			Archive<T> archive = resolvers.get(resource.getContentType()).newInstance();
			archive.setSource(resource);
			return archive;
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
