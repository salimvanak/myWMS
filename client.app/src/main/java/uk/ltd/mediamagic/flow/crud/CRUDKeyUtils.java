package uk.ltd.mediamagic.flow.crud;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import uk.ltd.mediamagic.fx.data.TableKey;

public class CRUDKeyUtils {
	
	public static <T extends BasicEntity> TableKey createKey(T entity) {
		BODTO<T> sel = new BODTO<T>(entity);
		TableKey key = new TableKey();
		key.put("id", sel.getId());
		key.put("name", sel.getName());
		key.put("version", sel.getVersion());
		key.put("bodto", sel);
		return key;
	}

	public static <T extends BasicEntity> TableKey createKey(BODTO<T> entity) {
		TableKey key = new TableKey();
		key.put("id", entity.getId());
		key.put("name", entity.getName());
		key.put("version", entity.getVersion());
		key.put("bodto", entity);
		return key;
	}

	public static <T extends BasicEntity> BODTO<T> getBOTO(TableKey key) {
		return (key == null) ? null : key.get("bodto");
	}

	public static Integer getID(TableKey key) {
		return (key == null) ? null : key.get("id");
	}

}
