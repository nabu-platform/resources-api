package be.nabu.libs.resources.api;

// allow reattaching to different parent
public interface ReattachableResource extends Resource {
	public void reattach(ResourceContainer<?> parent);
}
