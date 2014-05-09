package be.nabu.libs.resources.api;

import java.io.IOException;

import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public interface ReadableResource extends Resource {
	public ReadableContainer<ByteBuffer> getReadable() throws IOException;
}
