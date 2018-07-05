package be.nabu.libs.resources.api;

import java.io.Closeable;
import java.io.IOException;

public interface Resource extends Closeable {
	
	public static final String CONTENT_TYPE_DIRECTORY = "application/directory";
	
	/**
	 * The content type of this resource
	 * @return
	 */
	public String getContentType();
	
	/**
	 * The name of this resource, it must be unique within the parent, in other words getParent().getResource(resource.getName()).equals(resource) must be true
	 * @return
	 */
	public String getName();
	
	/**
	 * The parent resource
	 * @return
	 */
	public ResourceContainer<?> getParent();
	
	public default void close() throws IOException {
		// do nothing
	}
}
