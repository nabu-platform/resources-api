package be.nabu.libs.resources.api.features;

import java.io.IOException;
import java.util.Map;

import be.nabu.libs.resources.api.Resource;

public interface MetaDataResource extends Resource {
	public Map<String, String> getMetaData() throws IOException;
}
