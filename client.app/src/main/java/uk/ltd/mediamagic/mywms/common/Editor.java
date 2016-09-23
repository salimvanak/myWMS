package uk.ltd.mediamagic.mywms.common;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.plugin.Plugin;

public interface Editor<T extends BasicEntity> extends Plugin<T> {
	
	/**
	 * Will create a new instance of an editor for the given class object and id.
	 * Usually the editor will be created independently of the current context 
	 * and will be a non blocking editor.
	 * @param context the context for fetching services.
	 * @param dataClass the class of the data object
	 * @param id the if the data row.
	 */
	void edit(ContextBase context, Class<T> dataClass, long id);
	
	/**
	 * This will create a popup control over the current flow.
	 * This should be a read only view of the object.
	 * @param context the context for fetching serices
	 * @param flow the flow over which to create the popup
	 * @param dataClass the class of the data object
	 * @param id the id of the data row;
	 */
	void view(ContextBase context, Flow flow, Class<T> dataClass, long id);

	/**
	 * A list cell renderer factory suitable for rendering the type of this editor.
	 * @param listView the list view on which this cell render will operate.
	 * @return a list cell that will renderer the type.
	 */
	Callback<ListView<T>, ListCell<T>> createListCellFactory();

	/**
	 * A list cell renderer factory suitable for rendering the 
	 * transfer objects for this type of this editor.
	 * @param listView the list view on which this cell render will operate.
	 * @return a list cell that will renderer the type.
	 */
	Callback<ListView<BODTO<T>>, ListCell<BODTO<T>>> createTOListCellFactory();
}
