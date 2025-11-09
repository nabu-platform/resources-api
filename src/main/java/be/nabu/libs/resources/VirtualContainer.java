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

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.nabu.libs.resources.api.LocatableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public class VirtualContainer<T extends Resource> implements ResourceContainer<T>, LocatableResource, Closeable {

	private URI uri;
	private Map<String, T> children = new HashMap<String, T>();
	private ResourceContainer<?> parent;
	private String name;
	private List<String> ignoreRules = new ArrayList<String>();

	public VirtualContainer(ResourceContainer<?> parent, String name) {
		this.parent = parent;
		this.name = name;
		if (parent instanceof LocatableResource) {
			uri = URIUtils.getChild(((LocatableResource) parent).getUri(), name);
		}
	}
	
	public VirtualContainer(URI uri) {
		this.uri = uri;
		this.name = URIUtils.getName(uri);
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
	
	public void clear() {
		children.clear();
	}

	@Override
	public List<String> getIgnoreRules() {
		return ignoreRules;
	}
	
}