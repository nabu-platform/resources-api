package be.nabu.libs.resources.api;

/**
 * A provider-supplied opaque content revision, comparable by equality only.
 * During a known mutation, a provider may return the last completed revision;
 * it publishes a new revision after completion, including failed partial writes.
 * Null means that no completed revision is available. A revision is not a
 * snapshot lock: content read during mutation may differ from that revision.
 */
public interface RevisionedResource extends Resource {
	String getRevision();
}
