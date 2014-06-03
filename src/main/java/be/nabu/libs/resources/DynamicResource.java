package be.nabu.libs.resources;

import java.io.IOException;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.buffers.bytes.DynamicByteBuffer;
import be.nabu.utils.io.containers.ReadableContainerDuplicator;

/**
 * This "dynamic" resource takes a readable container as original content
 * If you first request a readable, you get the original but everything you read is duplicated in memory
 * The next you get a readable, you will get the memory duplicate
 * Requesting the readable again will also close the original one though
 */
public class DynamicResource implements ReadableResource {

	private ReadableContainer<ByteBuffer> originalContent;
	private String contentType;
	private String name;
	private DynamicByteBuffer content;
	private boolean alreadyRequested = false;
	private boolean shouldClose = false;
	
	@SuppressWarnings("unchecked")
	public DynamicResource(ReadableContainer<ByteBuffer> content, String name, String contentType, boolean shouldClose) {
		this.name = name;
		this.contentType = contentType;
		this.content = new DynamicByteBuffer();
		this.shouldClose = shouldClose;
		originalContent = new ReadableContainerDuplicator<ByteBuffer>(content, this.content);
		this.content.mark();
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
		if (!alreadyRequested) {
			alreadyRequested = true;
			return shouldClose ? originalContent : new UncloseableReadableContainer(originalContent);
		}
		else
			return content.clone();
	}

	private static class UncloseableReadableContainer implements ReadableContainer<ByteBuffer> {
		private ReadableContainer<ByteBuffer> parent;

		public UncloseableReadableContainer(ReadableContainer<ByteBuffer> parent) {
			this.parent = parent;
		}

		@Override
		public void close() throws IOException {
			// do nothing
		}

		@Override
		public long read(ByteBuffer buffer) throws IOException {
			return parent.read(buffer);
		}
	}
}
