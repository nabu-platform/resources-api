package be.nabu.libs.resources.api;

import java.util.Date;

public interface TimestampedResource extends Resource {
	public Date getLastModified();
}
