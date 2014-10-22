package be.nabu.libs.resources;

import java.io.IOException;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

/**
 * This readable container will close the resource if necessary
 */
public class ResourceReadableContainer implements ReadableContainer<ByteBuffer> {

	private ReadableContainer<ByteBuffer> readable;
	private ReadableResource resource;
	private boolean manageResource = true;
	
	public ResourceReadableContainer(ReadableResource resource) throws IOException {
		this(resource, true);
	}
	
	ResourceReadableContainer(ReadableResource resource, boolean manageResource) throws IOException {
		this.readable = resource.getReadable();
		this.resource = resource;
		this.manageResource = manageResource;
	}

	@Override
	public void close() throws IOException {
		try {
			readable.close();
		}
		finally {
			if (manageResource)
				ResourceUtils.getRoot(resource).close();
		}
	}

	@Override
	public long read(ByteBuffer buffer) throws IOException {
		return readable.read(buffer);
	}

}
