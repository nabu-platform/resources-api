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
