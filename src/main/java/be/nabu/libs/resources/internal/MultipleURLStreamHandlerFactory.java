package be.nabu.libs.resources.internal;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Collection;

public class MultipleURLStreamHandlerFactory implements URLStreamHandlerFactory {

	private Collection<URLStreamHandlerFactory> factories;

	private static boolean registered;

	public static void register(Collection<URLStreamHandlerFactory> factories) {
		if (!registered) {
			synchronized(VFSURLStreamHandlerFactory.class){
				if (!registered) {
					URL.setURLStreamHandlerFactory(new MultipleURLStreamHandlerFactory(factories));
					registered = true;
				}
			}
		}
	}
	
	public MultipleURLStreamHandlerFactory(Collection<URLStreamHandlerFactory> factories) {
		this.factories = factories;
	}
	
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		for (URLStreamHandlerFactory factory : factories) {
			URLStreamHandler streamHandler = factory.createURLStreamHandler(protocol);
			if (streamHandler != null) {
				return streamHandler;
			}
		}
		return null;
	}

}
