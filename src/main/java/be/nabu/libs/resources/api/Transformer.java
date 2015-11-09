package be.nabu.libs.resources.api;

public interface Transformer extends Wrapper, ReadableResource, WritableResource {
	public void setSource(Resource resource);
}
