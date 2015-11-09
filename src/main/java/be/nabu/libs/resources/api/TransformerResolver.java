package be.nabu.libs.resources.api;

import java.util.List;

public interface TransformerResolver {
	public <T extends Resource> Transformer newInstance();
	public List<String> getSupportedContentTypes();
}
