package be.nabu.libs.resources.api.features;

import java.io.IOException;

import be.nabu.libs.resources.api.Resource;

public interface LockableResource extends Resource {
	public void lock() throws IOException;
	public void unlock() throws IOException;
}
