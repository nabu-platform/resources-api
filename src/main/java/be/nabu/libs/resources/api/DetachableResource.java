package be.nabu.libs.resources.api;

public interface DetachableResource extends Resource {
	/**
	 * Create a detached version of this resource
	 */
	public Resource detach();
}
