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
