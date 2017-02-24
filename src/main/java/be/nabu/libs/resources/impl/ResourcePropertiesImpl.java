package be.nabu.libs.resources.impl;

import java.net.URI;
import java.util.Date;

import be.nabu.libs.resources.api.ResourceProperties;

public class ResourcePropertiesImpl implements ResourceProperties {
	private Date lastModified, lastAccessed;
	private Long size;
	private String name, contentType;
	private URI uri;
	private boolean readable, writable, appendable, listable;
	
	@Override
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	@Override
	public Date getLastAccessed() {
		return lastAccessed;
	}
	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	
	@Override
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	@Override
	public boolean isReadable() {
		return readable;
	}
	public void setReadable(boolean readable) {
		this.readable = readable;
	}
	
	@Override
	public boolean isWritable() {
		return writable;
	}
	public void setWritable(boolean writable) {
		this.writable = writable;
	}
	
	@Override
	public boolean isAppendable() {
		return appendable;
	}
	public void setAppendable(boolean appendable) {
		this.appendable = appendable;
	}
	
	@Override
	public boolean isListable() {
		return listable;
	}
	public void setListable(boolean listable) {
		this.listable = listable;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}