package uk.ltd.mediamagic.flow.crud;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import uk.ltd.mediamagic.fx.Units;
import uk.ltd.mediamagic.fx.converters.Filters;
import uk.ltd.mediamagic.fx.converters.IntegerConverter;

public class Pager extends Control {

	Property<Integer> pageNumber = new SimpleObjectProperty<>(1);
	Property<Integer> pageSize = new SimpleObjectProperty<>(100);
	IntegerProperty minPage = new SimpleIntegerProperty(1);
	IntegerProperty maxPage = new SimpleIntegerProperty(Integer.MAX_VALUE) {
		protected void invalidated() {
			if (maxPage.get() < pageNumber.getValue()) pageNumber.setValue(Math.max(1, maxPage.get()));
		}
	};
	
	public Pager() {
	}
	
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MySkin(this);	
	}

	public final Property<Integer> pageNumberProperty() {
		return this.pageNumber;
	}
	

	public final int getPageNumber() {
		return this.pageNumberProperty().getValue();
	}
	

	public final void setPageNumber(final int pageNumber) {
		this.pageNumberProperty().setValue(pageNumber);
	}
	

	public final Property<Integer> pageSizeProperty() {
		return this.pageSize;
	}
	

	public final int getPageSize() {
		return this.pageSizeProperty().getValue();
	}
	

	public final void setPageSize(final int pageSize) {
		this.pageSizeProperty().setValue(pageSize);
	}
	

	public final IntegerProperty minPageProperty() {
		return this.minPage;
	}
	

	public final int getMinPage() {
		return this.minPageProperty().get();
	}
	

	public final void setMinPage(final int minPage) {
		this.minPageProperty().set(minPage);
	}
	

	public final IntegerProperty maxPageProperty() {
		return this.maxPage;
	}
	

	public final int getMaxPage() {
		return this.maxPageProperty().get();
	}
	

	public final void setMaxPage(final int maxPage) {
		this.maxPageProperty().set(maxPage);
	}
	
	
	
	public static final class MySkin extends SkinBase<Pager> {
		public MySkin(Pager pager) {
			super(pager);
			
			HBox h = new HBox();
			h.setAlignment(Pos.BASELINE_LEFT);
			IntegerSpinnerValueFactory pageValueFactory = new IntegerSpinnerValueFactory(pager.getMinPage(), pager.getMaxPage());
			pageValueFactory.maxProperty().bindBidirectional(pager.maxPage);
			pageValueFactory.minProperty().bindBidirectional(pager.minPage);
			pageValueFactory.setAmountToStepBy(1);
			pageValueFactory.valueProperty().bindBidirectional(pager.pageNumber);

			Spinner<Integer> page = new Spinner<>(pageValueFactory);
			page.setEditable(true);
			page.setPrefWidth(Units.em(8));
			page.setMinWidth(Units.em(8));
			TextFormatter<Integer> pageSize = new TextFormatter<>(new IntegerConverter(), pager.getPageSize(), Filters.numeric());
			pageSize.valueProperty().bindBidirectional(pager.pageSizeProperty());
			
			BooleanBinding showMax = pager.maxPage.isNotEqualTo(Integer.MAX_VALUE);
			Label of = new Label(" of ");
			Label max = new Label();
			of.visibleProperty().bind(showMax);
			of.managedProperty().bind(showMax);
			max.visibleProperty().bind(showMax);
			max.managedProperty().bind(showMax);
			max.textProperty().bind(pager.maxPage.asString());
						
			h.getChildren().addAll(page, of, max, new Label(" / "), Filters.of(pageSize, 4));
			getChildren().add(h);
		}
	}
	
}
