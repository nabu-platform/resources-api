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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public class CombinedContainer<T extends Resource> implements ResourceContainer<T>, Closeable {

	private List<ResourceContainer<T>> containers = new ArrayList<ResourceContainer<T>>();
	private ResourceContainer<?> parent;
	private String name;
	
	@SuppressWarnings("unchecked")
	public CombinedContainer(ResourceContainer<?> parent, String name, ResourceContainer<?>...containers) {
		this.parent = parent;
		this.name = name;
		if (containers != null) {
			for (ResourceContainer<?> container : containers) {
				this.containers.add((ResourceContainer<T>) container);
			}
		}
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
		final List<ResourceContainer<T>> containersToLoop = new ArrayList<ResourceContainer<T>>(containers);
		return new Iterator<T>() {
			private Iterator<T> current;
			@Override
			public boolean hasNext() {
				if (current == null || !current.hasNext()) {
					current = containersToLoop.size() > 0 ? containersToLoop.remove(0).iterator() : null;
				}
				return current != null;
			}

			@Override
			public T next() {
				if (current == null || !current.hasNext()) {
					current = containersToLoop.size() > 0 ? containersToLoop.remove(0).iterator() : null;
				}
				return current == null ? null : current.next();
			}

			@Override
			public void remove() {
				// do nothing
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getChild(String name) {
		List<ResourceContainer<?>> result = new ArrayList<ResourceContainer<?>>();
		for (ResourceContainer<T> container : containers) {
			T child = container.getChild(name);
			if (child != null) {
				if (child instanceof ResourceContainer) {
					result.add((ResourceContainer<?>) child);
				}
				// if theres is a file with that name, it gets precedence
				else {
					return child;
				}
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		else if (result.size() == 1) {
			return (T) result.get(0);
		}
		else {
			return (T) new CombinedContainer<Resource>(this, name, result.toArray(new ResourceContainer[result.size()]));
		}
	}
	
	public void remove(ResourceContainer<?> container) {
		if (containers.contains(container)) {
			List<ResourceContainer<T>> newContainers = new ArrayList<ResourceContainer<T>>(containers);
			newContainers.remove(container);
			containers = newContainers;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void add(ResourceContainer<?> container) {
		if (!containers.contains(container)) {
			List<ResourceContainer<T>> newContainers = new ArrayList<ResourceContainer<T>>(containers);
			newContainers.add((ResourceContainer<T>) container);
			containers = newContainers;
		}
	}
	
	public List<ResourceContainer<T>> getContainers() {
		return new ArrayList<ResourceContainer<T>>(containers);
	}

	@Override
	public void close() throws IOException {
		for (ResourceContainer<T> container : containers) {
			ResourceUtils.close(container);
		}
	}
}
