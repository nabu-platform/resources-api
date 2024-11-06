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
			if (manageResource) {
				ResourceUtils.close(resource);
			}
		}
	}

	@Override
	public long read(ByteBuffer buffer) throws IOException {
		return readable.read(buffer);
	}

}
