/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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

	public ManageableContainer<?> getMainContainer() {
		return mainContainer;
	}
	
}
