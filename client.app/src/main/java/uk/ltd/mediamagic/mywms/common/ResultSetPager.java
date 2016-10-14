package uk.ltd.mediamagic.mywms.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ResultSetPager extends HBox {

	private final IntegerProperty startIndex = new SimpleIntegerProperty();
	private final IntegerProperty resultSize = new SimpleIntegerProperty();

	
	private ComboBox<Integer> pageSizeCombo = new ComboBox<>();
	
	private ComboBox<Integer> pageNo = new ComboBox<>();
	private Label pageTotalLabel = new Label();
	private Label resultSizeLabel = new Label();

	private final LongBinding pageTotal = Bindings.createLongBinding(this::calculatePageTotal, 
			resultSize, pageNo.valueProperty(), pageNo.itemsProperty());

	public ResultSetPager() {
		super();
		pageSizeCombo.setItems(FXCollections.observableArrayList(25, 50, 100, 200, 500));
		pageSizeCombo.getSelectionModel().selectFirst();
		
		pageTotalLabel.textProperty().bind(Bindings.format(" / %d  ", pageTotal));
		resultSizeLabel.textProperty().bind(Bindings.format("  Total: %d", resultSize));
		
		startIndex.addListener(o -> this.updatePageNo());
		pageSizeCombo.valueProperty().addListener(o -> updatePageNo());
		
		setAlignment(Pos.BASELINE_LEFT);
		getChildren().addAll(pageNo, pageTotalLabel, pageSizeCombo, resultSizeLabel);
	}
	
	private void updatePageNo() {
		long pageSize = pageSizeCombo.getValue();
		List<Integer> pages = Stream.iterate(1, x -> x + 1)
			.limit(pageTotal.get())
			.collect(Collectors.toList());
		
		pageNo.setItems(FXCollections.observableList(pages));
		pageNo.setValue((int) (startIndex.get() / pageSize));
	}

	private long calculatePageTotal() {
		long total = resultSize.get();
		Integer size = pageSizeCombo.getValue();
		if (size == null || size == 0) return 0;
		if (total % size == 0) 
			return total / size;
		else
			return (total / size) + 1;
	}
	
	public final IntegerProperty startIndexProperty() {
		return this.startIndex;
	}
	

	public final int getStartIndex() {
		return this.startIndexProperty().get();
	}
	

	public final void setStartIndex(final int startIndex) {
		this.startIndexProperty().set(startIndex);
	}
	

	public final IntegerProperty resultSizeProperty() {
		return this.resultSize;
	}
	

	public final int getResultSize() {
		return this.resultSizeProperty().get();
	}
	

	public final void setPageSize(final int pageSize) {
		this.pageSizeCombo.setValue(pageSize);
	}	
	
	public final ObjectProperty<Integer> pageSizeProperty() {
		return this.pageSizeCombo.valueProperty();
	}
	

	public final int getPageSize() {
		return this.pageSizeCombo.getValue();
	}
	

	public final void setResultSize(final int resultSize) {
		this.resultSizeProperty().set(resultSize);
	}	
}
