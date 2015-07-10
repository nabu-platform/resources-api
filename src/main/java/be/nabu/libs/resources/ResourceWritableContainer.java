package be.nabu.libs.resources;

import java.io.IOException;

import be.nabu.libs.resources.api.WritableResource;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.WritableContainer;

public class ResourceWritableContainer implements WritableContainer<ByteBuffer> {

	private WritableContainer<ByteBuffer> writable;
	private WritableResource resource;
	private boolean manageResource = true;
	
	public ResourceWritableContainer(WritableResource resource) throws IOException {
		this(resource, true);
	}
	
	ResourceWritableContainer(WritableResource resource, boolean manageResource) throws IOException {
		this.writable = resource.getWritable();
		this.resource = resource;
		this.manageResource = manageResource;
	}
	
	@Override
	public void close() throws IOException {
		try {
			writable.close();
		}
		finally {
			if (manageResource) {
				ResourceUtils.close(resource);
			}
		}
	}

	@Override
	public long write(ByteBuffer buffer) throws IOException {
		return writable.write(buffer);
	}

	@Override
	public void flush() throws IOException {
		writable.flush();
	}

}
