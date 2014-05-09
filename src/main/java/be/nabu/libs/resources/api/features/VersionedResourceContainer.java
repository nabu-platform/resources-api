package be.nabu.libs.resources.api.features;
import java.io.IOException;

import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public interface VersionedResourceContainer<T extends Resource> extends ResourceContainer<T>, VersionedResource {
	public Iterable<T> getChildren(String version) throws IOException;
	public T getChild(String name, String version) throws IOException;
}
