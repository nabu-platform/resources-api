package be.nabu.libs.resources.api;

import java.util.List;

public interface ArchiveResolver {
	public <T extends Resource> Archive<T> newInstance();
	public List<String> getSupportedContentTypes();
}
