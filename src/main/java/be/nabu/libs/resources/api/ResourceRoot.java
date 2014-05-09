package be.nabu.libs.resources.api;

import java.io.Closeable;

/**
 * If a root resource is closed, it must release any backend resources it was using (e.g. an ftp connection)
 * In the beginning all resources were closeable but this has two problems:
 * - if you have e.g. an ftp connection and you have 5 resources open in it, they will share the connection. Closing one resource must not close the connection or all will fail
 * 			This means you have to know how many resources are still open on it
 * - if all resources are closeable, everyone using the "resource" interface is responsible for a try/finally construct to close it. And likely you will need try/finally already for the actual data streams which makes for complex code
 * 			Combined with the first problem this actually only makes it more difficult to write proper code
 * 
 * In the end it seemed better to introduce a ResourceRoot which means only the piece of code where a resource is initially located is responsible for managing the state is has built
 * 
 * If you are working with readables/writables that have their own state, it can be managed separately
 * However if the readable/writable piggybacks on the state of the resource, it should obviously not be closed
 * For example: if the resource denotes a file on an ftp server, the resourceroot should manage the ftp connection, closing the "readable" should close the stream only, NOT the connection
 */

public interface ResourceRoot extends Resource, LocatableResource, Closeable {

}
