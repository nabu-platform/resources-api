package be.nabu.libs.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.nabu.libs.resources.api.LocatableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public class VirtualContainer<T extends Resource> implements ResourceContainer<T>, LocatableResource {

	private URI uri;
	private Map<String, T> children = new HashMap<String, T>();

	public VirtualContainer(URI uri) {
		this.uri = uri;
	}
	
	@Override
	public String getContentType() {
		return Resource.CONTENT_TYPE_DIRECTORY;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ResourceContainer<?> getParent() {
		return null;
	}

	@Override
	public Iterator<T> iterator() {
		return children.values().iterator();
	}

	@Override
	public T getChild(String name) {
		return children.get(name);
	}

	@Override
	public URI getURI() {
		return uri;
	}
	
	public void addChild(String name, T resource) {
		children.put(name, resource);
	}
	
	public void removeChild(String name) {
		children.remove(name);
	}
	
}