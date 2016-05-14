package view.javaFxUI.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FxObject {
	ArrayList<Tag> tags = new ArrayList<>();
	public static enum Tag {
		START_X,
		START_Y,
		START_Z,
		END_X,
		END_Y,
		END_Z,
		STAR_RIGHT,
		STAR_LEFT,
		START_RED,
		START_YELLOW,
		START_GREEN,
		END_RED,
		END_YELLOW,
		END_GREEN,
	}

	public FxObject(Tag... tags) {
		this.tags.addAll(Arrays.asList(tags));
	}

	public void addTag(Tag... tags){
		this.tags.addAll(Arrays.asList(tags));
	}

	public void removeTag(Tag tag){
		tags.remove(tag);
	}

	public boolean containsTag(Tag tag){
		return tags.contains(tag);
	}

	public List<Tag> getTags(){
		return new ArrayList<>(tags);
	}
}
