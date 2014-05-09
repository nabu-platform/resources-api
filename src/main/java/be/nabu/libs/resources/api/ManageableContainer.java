package be.nabu.libs.resources.api;

import java.io.IOException;

public interface ManageableContainer<T extends Resource> extends ResourceContainer<T> {
	public T create(String name, String contentType) throws IOException;
	public void delete(String name) throws IOException;
}
