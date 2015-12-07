package be.nabu.libs.resources.api;

import java.io.IOException;

import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.WritableContainer;

public interface AppendableResource extends WritableResource {
	public WritableContainer<ByteBuffer> getAppendable() throws IOException;
}
