package be.nabu.libs.resources;

import java.io.IOException;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public class ReadableByteResource implements ReadableResource {

	private byte [] bytes;
	private int offset, length;
	private String contentType;
	private String name = "unnamed.bin";
	
	public ReadableByteResource(byte [] bytes) {
		this(bytes, 0, bytes.length);
	}
	
	public ReadableByteResource(byte [] bytes, int offset, int length) {
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}
	
	public ReadableByteResource setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	public ReadableByteResource setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer<?> getParent() {
		return null;
	}

	@Override
	public ReadableContainer<ByteBuffer> getReadable() throws IOException {
		return IOUtils.wrap(bytes, offset, length, true);
	}

}
