package be.nabu.libs.resources.api;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;

public interface ResourceResolver {
	public ResourceRoot getResource(URI uri, Principal principal) throws IOException;
	public List<String> getDefaultSchemes();
}
