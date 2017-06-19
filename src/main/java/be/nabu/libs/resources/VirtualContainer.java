package be.nabu.libs.resources;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.nabu.libs.resources.api.LocatableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public class VirtualContainer<T extends Resource> implements ResourceContainer<T>, LocatableResource, Closeable {

	private URI uri;
	private Map<String, T> children = new HashMap<String, T>();
	private ResourceContainer<?> parent;
	private String name;

	public VirtualContainer(ResourceContainer<?> parent, String name) {
		this.parent = parent;
		this.name = name;
		if (parent instanceof LocatableResource) {
			uri = URIUtils.getChild(((LocatableResource) parent).getUri(), name);
		}
	}
	
	public VirtualContainer(URI uri) {
		this.uri = uri;
	}
	
	@Override
	public String getContentType() {
		return Resource.CONTENT_TYPE_DIRECTORY;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer<?> getParent() {
		return parent;
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
	public URI getUri() {
		return uri;
	}
	
	public void addChild(String name, T resource) {
		children.put(name, resource);
	}
	
	public void removeChild(String name) {
		children.remove(name);
	}

	@Override
	public void close() throws IOException {
		for (Resource child : children.values()) {
			ResourceUtils.close(child);
		}
	}

	@Override
	public String toString() {
		return "virtual:" + getUri();
	}
}