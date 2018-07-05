package be.nabu.libs.resources.api;

import java.io.IOException;

public interface RenameableResource extends Resource {
	public void rename(String name) throws IOException;
}
