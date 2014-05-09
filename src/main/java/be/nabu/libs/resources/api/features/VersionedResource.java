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
