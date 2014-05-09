# Description

The resources API is a low level api that manages access to singular file-like entities or filesystem-like hierarchies.
Resources must be accessible through URIs that pinpoint their location and optionally a principal to manage authentication/authorization.

# Releasing backend resources

A resource hierarchy may contain state that needs to be properly handled (e.g. an ftp connection).
However there are two things to consider:

- If you have an FTP connection for an entire hierarchy, you don't want to close it because one resource in it was closed
- Resources are mainly used to manage input/output to them which has to be managed separately, we'd rather not end up with code like:

```java
public void doSomething(ResourceContainer container, String name) {
	ReadableResource resource = container.getChild(name);
	try {
		ReadableContainer<ByteBuffer> readable = resource.getReadable();
		try {
			...
		}
		finally {
			readable.close();
		}
	}
	finally {
		resource.close();
	}
}
```

To this end the ResourceRoot interface was added which implements Closeable. 
This means the onerous logic to manage overall state of backend resources was delegated to the root of the hierarchy.
Only the one who actually looks up a resource through its URI will have to close it, passing along that resource (or child resources) does not force everyone to manage them.
As with streams the best practice is that the one who originally resolved the resource, closes it.