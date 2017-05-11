package be.nabu.libs.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;

public class EditableCombinedContainer<T extends Resource> extends CombinedContainer<T> implements ManageableContainer<T> {

	private ManageableContainer<?> mainContainer;

	public EditableCombinedContainer(ResourceContainer<?> parent, String name, ManageableContainer<?> mainContainer, ResourceContainer<?>...containers) {
		super(parent, name, merge(mainContainer, containers));
		this.mainContainer = mainContainer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T create(String name, String contentType) throws IOException {
		return (T) mainContainer.create(name, contentType);
	}

	@Override
	public void delete(String name) throws IOException {
		mainContainer.delete(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ResourceContainer[] merge(ManageableContainer<?> mainContainer, ResourceContainer<?>...containers) {
		List<ResourceContainer<?>> result = new ArrayList<ResourceContainer<?>>(Arrays.asList(mainContainer));
		result.addAll(Arrays.asList(containers));
		return result.toArray(new ResourceContainer[0]);
	}
}
