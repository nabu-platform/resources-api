package be.nabu.libs.resources.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.List;

import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.api.FiniteResource;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.TimestampedResource;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.utils.io.IOUtils;

/**
 * Instead of supporting all the schemes supported by the VFS, it expects
 */
public class VFSURLStreamHandlerFactory implements URLStreamHandlerFactory {

	public static List<String> defaultSchemes = Arrays.asList(new String [] { "http", "https", "ftp", "jar" });
	
	private static boolean registered;

	public static void register() {
		if (!registered) {
			synchronized(VFSURLStreamHandlerFactory.class){
				if (!registered) {
					URL.setURLStreamHandlerFactory(new VFSURLStreamHandlerFactory());
					registered = true;
				}
			}
		}
	}
	
	public VFSURLStreamHandlerFactory() {
		// make sure the factory is instantiated and has performed the lookup
		// the SPI implementation can not (apparently) handle the lookup during URL resolving...
		ResourceFactory.getInstance().getSchemes();
	}
	
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (!defaultSchemes.contains(protocol) && ResourceFactory.getInstance().getSchemes().contains(protocol)) {
			return new URLStreamHandler() {
				@Override
				protected URLConnection openConnection(final URL url) throws IOException {
					try {
						final Resource resource = ResourceFactory.getInstance().resolve(url.toURI(), null);
						if (resource == null) {
							throw new FileNotFoundException("Can not find: " + url);
						}
						return new URLConnection(url) {
							@Override
							public void connect() throws IOException {
								// do nothing
							}
							@Override
							public int getContentLength() {
								return resource instanceof FiniteResource ? (int) ((FiniteResource) resource).getSize() : -1;
							}
							@Override
							public String getContentType() {
								return resource.getContentType();
							}
							@Override
							public long getLastModified() {
								return resource instanceof TimestampedResource ? ((TimestampedResource) resource).getLastModified().getTime() : 0;
							}
							@Override
							public InputStream getInputStream() throws IOException {
								System.out.println(resource);
								if (!(resource instanceof ReadableResource)) {
									throw new IOException("The resource can not be read from: " + url);
								}
								return IOUtils.toInputStream(((ReadableResource) resource).getReadable());
							}

							@Override
							public OutputStream getOutputStream() throws IOException {
								if (!(resource instanceof WritableResource)) {
									throw new IOException("The resource can not be written to: " + url);
								}
								return IOUtils.toOutputStream(((WritableResource) resource).getWritable());
							}
						};
					}
					catch (URISyntaxException e) {
						throw new IOException("Invalid uri: " + url, e);
					}
				}
			};
		}
		return null;
	}

}
