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

import be.nabu.libs.resources.api.Resource;

public interface CacheableResource extends Resource {
	
	/**
	 * This clears the cached value(s). You can either simply reset and lazily reload it on the next request
	 * Or you can proactively reload it after the value is reset
	 * 
	 * @throws IOException
	 */
	public void resetCache() throws IOException;
	
	/**
	 * Toggles whether or not the object should cache.
	 * In most cases the caching is implemented to speed up slow IO systems
	 * This means in most cases the default should be set to "true" if the interface is implemented
	 * 
	 * @param cache
	 */
	public void setCaching(boolean cache);
	
	/**
	 * This indicates whether the object has caching enabled or not
	 * @return
	 */
	public boolean isCaching();
}
