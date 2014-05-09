package be.nabu.libs.resources.api;

public interface Archive<T extends Resource> extends Wrapper, ResourceContainer<T> {
	public void setSource(Resource resource);
}
