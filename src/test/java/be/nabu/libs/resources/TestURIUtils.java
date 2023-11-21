package be.nabu.libs.resources;

import java.net.URI;
import java.net.URISyntaxException;

import be.nabu.libs.resources.URIUtils;
import junit.framework.TestCase;


public class TestURIUtils extends TestCase {

	public void testGetName() throws URISyntaxException {
		URI uri = new URI("file:/this/is/a/file.txt");
		assertEquals("file.txt", URIUtils.getName(uri));
	}
	
	public void testGetParent() throws URISyntaxException {
		URI uri = new URI("file:/this/is/a/file.txt");
		assertEquals(new URI("file:/this/is/a"), URIUtils.getParent(uri));
		
		assertNull(URIUtils.getParent(new URI("/")));
		assertNull(URIUtils.getParent(new URI("file:/")));
	}
	
	public void testSpecialChars() throws URISyntaxException {
		URI uri = new URI(URIUtils.encodeURI("file:/this/isé/a/file%+ testé~)°.txt"));
		assertEquals(new URI("file:/this/isé/a"), URIUtils.getParent(uri));
		assertEquals("file%+ testé~)°.txt", URIUtils.getName(uri));
		assertEquals("My spécial name.txt", URIUtils.getName(URIUtils.getChild(uri, "My spécial name.txt")));
	}
	
	public void testRelativize() throws URISyntaxException {
		URI uri = new URI("file:/this/is/a/file.txt");
		URI parent = new URI("file:/this/is");
		assertEquals("a/file.txt", URIUtils.relativize(parent.getPath(), uri.getPath()));
		
		URI fakeParent = new URI("file:/this/is/not");
		assertNull(URIUtils.relativize(fakeParent.getPath(), uri.getPath()));
	}
	
	public void testGetChild() throws URISyntaxException {
		URI parent = new URI("file:/this/is/a");
		assertEquals(new URI("file:/this/is/a/file.txt"), URIUtils.getChild(parent, "file.txt"));
	}
	
	public void testElementInRoot() throws URISyntaxException {
		assertNotNull(URIUtils.getParent(new URI("file:/randomelement")));
		assertEquals(new URI("file:/"), URIUtils.getParent(new URI("file:/randomelement")));
	}
	
	public void testUriBuilding() {
		assertEquals("test :this.pdf", URIUtils.buildUri(null, null, null, null, null, "test :this.pdf", null, null).getPath());
		assertEquals("http://www.google.com", URIUtils.buildUri("http", null, null, "www.google.com", null, null, null, null).toString());
		assertEquals("http://www.google.com:80/check/this?q=test", URIUtils.buildUri("http", null, null, "www.google.com", 80, "/check/this", "q=test", null).toString());
	}
}
