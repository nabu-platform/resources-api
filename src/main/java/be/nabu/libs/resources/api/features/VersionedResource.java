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

package be.nabu.libs.resources.api.features;

import java.io.IOException;
import java.util.List;

import be.nabu.libs.resources.api.Resource;

public interface VersionedResource extends Resource {
	
	/**
	 * The available versions for this node
	 * @return
	 * @throws IOException
	 */
	public List<String> getVersions() throws IOException;
	
	/**
	 * The current version of this node, use "null" to indicate the latest
	 * @return
	 */
	public String getVersion();
}
