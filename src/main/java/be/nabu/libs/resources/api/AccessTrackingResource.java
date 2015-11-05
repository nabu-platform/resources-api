package be.nabu.libs.resources.api;

import java.util.Date;

public interface AccessTrackingResource extends Resource {
	public Date getLastAccessed();
}
