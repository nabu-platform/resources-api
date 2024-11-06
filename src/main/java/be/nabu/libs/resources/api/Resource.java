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
