package be.nabu.libs.resources;

import java.io.IOException;
import java.util.LinkedHashMap;
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
	
	public void addResourceResolver(ArchiveResolver resolver) {
		for (String contentType : resolver.getSupportedContentTypes())
			setArchiveResolver(contentType, resolver, false);
	}
	
	public void removeResourceResolver(ArchiveResolver resolver) {
		for (String contentType : resolvers.keySet())
			resolvers.remove(contentType);
	}
	
	public ArchiveResolver getResolver(String scheme) {
		return getResolvers().get(scheme);
	}
	
	public Set<String> getSchemes() {
		return getResolvers().keySet();
	}
	
	private Map<String, ArchiveResolver> getResolvers() {
		if (resolvers.isEmpty()) {
			ServiceLoader<ArchiveResolver> serviceLoader = ServiceLoader.load(ArchiveResolver.class);
			for (ArchiveResolver resolver : serviceLoader)
				addResourceResolver(resolver);
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
