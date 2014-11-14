package be.nabu.libs.resources;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import be.nabu.libs.resources.api.ResourceResolver;
import be.nabu.libs.resources.api.ResourceRoot;

public class ResourceFactory {

	private static ResourceFactory instance;
	
	public static ResourceFactory getInstance() {
		if (instance == null)
			instance = new ResourceFactory();
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
			try {
				// let's try this with custom service loading based on a configuration
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("be.nabu.utils.services.ServiceLoader");
				Method declaredMethod = clazz.getDeclaredMethod("load", Class.class);
				for (ResourceResolver resolver : (List<ResourceResolver>) declaredMethod.invoke(null, ResourceResolver.class)) {
					addResourceResolver(resolver);
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
					addResourceResolver(resolver);
				}
			}
		}
		return resolvers;
	}
	
	public ResourceRoot resolve(URI uri, Principal principal) throws IOException {
		// it is possible to return null so for instance you might want to check if something exists and if not, create it
		// the "mkdir()" functionality has the ability to scan further up the tree to find something
		if (getResolvers().containsKey(uri.getScheme()))
			return resolvers.get(uri.getScheme()).getResource(uri, principal);
		else
			throw new IllegalArgumentException("The scheme " + uri.getScheme() + " has no registered handler");
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
