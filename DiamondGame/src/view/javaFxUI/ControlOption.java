package view.javaFxUI;

import java.util.EnumMap;
import java.util.Map;

import game.GameConfig;

public class ControlOption {
	public static enum ValueId {
		GAME_CONFIG(GameConfig.class);
		Class<?> dataClass;
		private ValueId(Class<?> dataClass) {
			this.dataClass = dataClass;
		}
		public Class<?> getDataClass() {
			return dataClass;
		}
	}

	Map<ValueId, Object> data;
	public ControlOption() {
		data = new EnumMap<>(ValueId.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(ValueId valueId, Class<T> cls){
		if(!valueId.dataClass.equals(cls)){
			return null;
		}
		if(!cls.isInstance(data.get(valueId))){
			return null;
		}
		return (T)data.get(valueId);
	}

	public boolean setValue(ValueId valueId, Object value){
		if(!valueId.getDataClass().isInstance(value)){
			return false;
		}
		data.put(valueId, value);
		return true;
	}
}
