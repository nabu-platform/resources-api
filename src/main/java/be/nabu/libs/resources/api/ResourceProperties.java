package be.nabu.libs.resources.api;

import java.net.URI;
import java.util.Date;

public interface ResourceProperties {
	public Date getLastModified();
	public Date getLastAccessed();
	public Long getSize();
	public String getName();
	public String getContentType();
	public URI getUri();
	public boolean isReadable();
	public boolean isWritable();
	public boolean isListable();
	public boolean isAppendable();
}
