package uk.ltd.mediamagic.mywms.common;

import de.linogistix.los.query.LOSResultList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LOSResultListProperty<T> extends ObjectPropertyBase<LOSResultList<T>>{

	private final Object bean;
	private final String name;
	
	private ListBinding<T> wrappedListBinding = new ListBinding<T>() {
		{ bind(LOSResultListProperty.this); }
		@Override
		protected ObservableList<T> computeValue() {
			if (LOSResultListProperty.this.get() == null) 
				return FXCollections.emptyObservableList();
			else {
				return FXCollections.observableList(LOSResultListProperty.this.get());
			}
		}
	};

	private LongBinding startIndex = Bindings.createLongBinding(this::computeStartIndex, this);
	private LongBinding resultSize = Bindings.createLongBinding(this::computeSizeIndex, this);

	public LOSResultListProperty(Object bean, String name) {
		super();
		this.bean = bean;
		this.name = name;
	}
	
	@Override
	protected void invalidated() {
		super.invalidated();
		wrappedListBinding.get();
	}
	
	public LOSResultListProperty() {
		this(null, null);
	}

	@Override
	public Object getBean() {
		return bean;
	}

	@Override
	public String getName() {
		return name;
	}
	
	private long computeStartIndex() {
		LOSResultList<T> r = get();
		if (r == null) return 0;
		return r.getStartResultIndex();
	}

	private long computeSizeIndex() {
		LOSResultList<T> r = get();
		if (r == null) return 0;
		return r.getResultSetSize();
	}

	public long computeStartPage(long pageSize) {		
		//		System.out.println("@("+startIndex+") "+currentPage+" Max " + list.getResultSetSize() + "(" + pageMax + ") pagesize :" + pageSize);

		LOSResultList<?> r = get();
		if (r == null) return 1;

		long startIndex = r.getStartResultIndex();

		long currentPage = (startIndex / pageSize)+1;
		return currentPage;
	}

	public long computeMaxPage(long pageSize) {		
		LOSResultList<?> r = get();
		if (r == null) return 0;

		long totalSize = r.getResultSetSize(); 
		
		long pageMax = (totalSize / pageSize) 
				+ (((totalSize % pageSize) == 0) ? 0 : 1);

		return pageMax;
	}

	public LongBinding resultSizeProperty() {
		return resultSize;
	}
	
	public long getResultSize() {
		return resultSize.get();
	}
	
	public LongBinding startIndexProperty() {
		return startIndex;
	}
	
	public long getStartIndex() {
		return startIndex.get();
	}	

	public ListBinding<T> wrappedListProperty() {
		return wrappedListBinding;
	}
	
	public ObservableList<T> getWrappedList() {
		return wrappedListBinding.get();
	}
}
