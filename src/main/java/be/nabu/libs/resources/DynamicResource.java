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
import java.util.Date;

import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.TimestampedResource;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.Container;
import be.nabu.utils.io.api.DuplicatableContainer;
import be.nabu.utils.io.api.MarkableContainer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.buffers.bytes.DynamicByteBuffer;
import be.nabu.utils.io.containers.ReadableContainerDuplicator;

/**
 * This "dynamic" resource takes a readable container as original content
 * If you first request a readable, you get the original but everything you read is duplicated in memory
 * The next you get a readable, you will get the memory duplicate
 * Requesting the readable again will also close the original one though
 */
public class DynamicResource implements ReadableResource, Closeable, TimestampedResource {

	private ReadableContainer<ByteBuffer> originalContent;
	private String contentType;
	private String name;
	private Container<ByteBuffer> content;
	private boolean alreadyRequested = false;
	private boolean shouldClose = false;
	private Date lastModified;
	
	@SuppressWarnings("unchecked")
	public <T extends DuplicatableContainer<ByteBuffer, ? extends ReadableContainer<ByteBuffer>> & Container<ByteBuffer>> DynamicResource(ReadableContainer<ByteBuffer> originalContent, T backend, String name, String contentType, boolean shouldClose) {
		this.name = name;
		this.contentType = contentType;
		this.shouldClose = shouldClose;
		this.content = backend;
		if (backend instanceof MarkableContainer)
			((MarkableContainer<ByteBuffer>) backend).mark();
		this.originalContent = new ReadableContainerDuplicator<ByteBuffer>(originalContent, this.content);
	}
	
	@SuppressWarnings("unchecked")
	public DynamicResource(ReadableContainer<ByteBuffer> originalContent, String name, String contentType, boolean shouldClose) {
		this(originalContent, new DynamicByteBuffer(), name, contentType, shouldClose);
		this.name = name;
		this.contentType = contentType;
		this.shouldClose = shouldClose;
		DynamicByteBuffer buffer = new DynamicByteBuffer();
		buffer.mark();
		this.content = buffer;
		this.originalContent = new ReadableContainerDuplicator<ByteBuffer>(originalContent, this.content);
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

	@SuppressWarnings("unchecked")
	@Override
	public ReadableContainer<ByteBuffer> getReadable() throws IOException {
		if (!alreadyRequested) {
			alreadyRequested = true;
			return shouldClose ? originalContent : new UncloseableReadableContainer(originalContent);
		}
		else {
			// if the last modified was not set, fix it
			// the data can no longer be updated
			if (lastModified == null) {
				lastModified = new Date();
			}
			return ((DuplicatableContainer<ByteBuffer, ? extends ReadableContainer<ByteBuffer>>) content).duplicate(true);
		}
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

	@Override
	public void close() throws IOException {
		content.close();
	}

	@Override
	public Date getLastModified() {
		return lastModified == null ? new Date() : lastModified;
	}
}
