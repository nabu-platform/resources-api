package be.nabu.libs.resources.api;

/**
 * This interface implements Iterable instead of using collections in order to allow lazy access and local optimization.
 * For example when browsing a large file directory in java 7 you can actually iterate over all the files instead of waiting ages for the list to build.
 * An additional consideration is that it is easier to dynamically wrap around an iterator (e.g. for transformations) 
 * 
 * @author alex
 *
 */
public interface ResourceContainer<T extends Resource> extends Resource, Iterable<T> {
	
	/**
	 * Allows direct access to a named child instead of having to loop over the existing children.
	 * This can speed up random access.
	 * If this is not feasible, you can always use the utility method which will loop over all the children to get the correct one
	 * With java 8 this will likely be implemented as a default method.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public T getChild(String name);
	
}
