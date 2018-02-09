package uk.ltd.mediamagic.mywms.common;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.MLogger;

/**
 * Automatically create a set of columns for the dataset with the DataSetTableView.
 * @author slim
 *
 * @param <D>
 */
public class TableColumnBinding<D> extends ListBinding<TableColumn<D, ?>> {

	private final Logger log = MLogger.log(this);
	
	private Function<PropertyDescriptor, StringConverter<?>> converterFactory;	

	private final BeanInfo beanInfo;
	private final Predicate<String> hideColumns;
	private final Iterable<String> columns;

	public TableColumnBinding(BeanInfo beanInfo, Iterable<String> columns, Predicate<String> hideColumns) {
		super();
		this.hideColumns = hideColumns;
		this.columns = columns;
		this.beanInfo = beanInfo;
	}

	public Function<PropertyDescriptor, StringConverter<?>> getConverterFactory() {
		return converterFactory;
	}

	public void setConverterFactory(Function<PropertyDescriptor, StringConverter<?>> converterFactory) {
		this.converterFactory = converterFactory;
	}

	@Override
	protected ObservableList<TableColumn<D, ?>> computeValue() {
		List<TableColumn<D,?>> cols = new ArrayList<>();
		for (String colName : columns) {
			String mapName;
			if (colName.contains(" AS ")) {
				int asIndex = colName.indexOf(" AS ");
				mapName = colName.substring(asIndex + 4);
				colName = colName.substring(0, asIndex);
			}
			else {
				mapName = null;
			}

			PropertyDescriptor col;
			if (colName.contains(".")) {
				col = BeanUtils.getNestedProperty(beanInfo.getBeanDescriptor().getBeanClass(), colName);
			}
			else {				
				col = BeanUtils.getProperty(beanInfo, colName);
			}
			if (col == null) {
				log.log(Level.WARNING, "Column {0} cannot be found", colName);
				continue;
			}
			
			TableColumn<D, ?> c = createColumn(colName, col);
			if (hideColumns.test(col.getName())) c.setVisible(false);
			if (mapName != null) c.setId(mapName);
			cols.add(c);
		}
		return FXCollections.observableList(cols);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TableColumn<D, ?> createColumn(String colName, PropertyDescriptor col) {
		Objects.requireNonNull(col);

		if (BeanUtils.isBoolean(col)) {
			TableColumn<D,?> colB = new TableColumn<>(colName);
			colB.setId(colName);
			colB.setText(BeanUtils.getDisplayName(col));
			colB.setCellFactory(CheckBoxTableCell.forTableColumn(BeanUtils.getValueFactory(col)));
			return colB;			
		}
		else {
			TableColumn colB = new TableColumn(col.getName());
			colB.setId(colName);
			colB.setText(BeanUtils.getDisplayName(col));
			colB.setCellValueFactory(
					(Callback<CellDataFeatures, ObservableValue>)
					c -> Bindings.select(c.getValue(), colName.split("\\.")));
			//colB.setCellValueFactory(BeanUtils.getCellValueFactory(col));
			StringConverter<D> converter = (StringConverter<D>) getConverter(col);
			colB.setCellFactory(TextFieldTableCell.forTableColumn(converter));
			//int size = md.getColumnSize(colName);
			//double scale = (size <= 20) ? 0.3d : 0.9d; 
			return colB;
		}
	}

	public StringConverter<?> getConverter(PropertyDescriptor col) {
		StringConverter<?> converter = null;
		if (converterFactory != null) converter =  converterFactory.apply(col);
		if (converter == null) {
			converter = BeanUtils.getConverter(col);
		}
		return converter;
	}
}
