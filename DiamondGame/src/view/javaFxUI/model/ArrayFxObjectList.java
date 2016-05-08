package view.javaFxUI.model;

import java.util.ArrayList;
import java.util.Arrays;

import view.javaFxUI.model.FxObject.Tag;

public class ArrayFxObjectList<T extends FxObject> extends ArrayList<T> {
	/**
	 * AND指定でTagがついたオブジェクトを検索します
	 * @param tags
	 * @return
	 */
	public T findByTags(Tag... tags){
		for(T obj : this){
			if(obj.tags.containsAll(Arrays.asList(tags))){
				return obj;
			}
		}
		return null;
	}
}
