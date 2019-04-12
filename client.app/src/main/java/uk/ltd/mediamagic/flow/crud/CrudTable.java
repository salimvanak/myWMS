package uk.ltd.mediamagic.flow.crud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import uk.ltd.mediamagic.common.data.RowCriterion;
import uk.ltd.mediamagic.fx.binding.ListContentBinding;
import uk.ltd.mediamagic.fx.binding.SimpleObservableRelay;
import uk.ltd.mediamagic.fx.control.TableKeySelectable;
import uk.ltd.mediamagic.fx.controller.FxTableController;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.table.ProjectPanelBase;

public class CrudTable<D extends BasicEntity> extends FxTableController<D> implements TableKeySelectable {

	private final SimpleObservableRelay queryChanged = new SimpleObservableRelay();
	private Pager pager;

	public CrudTable() {
		super();
		queryChanged.bind(getProjectPanel().filterProperty());
		pager.setPageSize(100);
		pager.setPageNumber(1);
		pager.pageNumberProperty().addListener(l -> queryChanged.invalidate());
	}

	public CrudTable(String saveSettingsUID) {
		super(saveSettingsUID);
		queryChanged.bind(getProjectPanel().filterProperty());
		pager.setPageSize(100);
		pager.setPageNumber(1);
		pager.pageNumberProperty().addListener(l -> queryChanged.invalidate());
	}

	public void clearTable() {
		setItems(null);
	}
	
	public void setLOSResultList(LOSResultList<D> list) {		
		queryChanged.validate();
		int pageSize = getPager().getPageSize();
		
		long startIndex = list.getStartResultIndex();
		long currentPage = (startIndex / pageSize)+1;
		long pageMax = (list.getResultSetSize() / pageSize) 
				+ (((list.getResultSetSize() % pageSize) == 0) ? 0 : 1);
		
		System.out.println("@("+startIndex+") "+currentPage+" Max " + list.getResultSetSize() + "(" + pageMax + ") pagesize :" + pageSize);

		System.out.println("@("+startIndex+") "+currentPage+" Max " + list.getResultSetSize() + "(" + pageMax + ") pagesize :" + pageSize);
		getPager().setMaxPage((int) pageMax);
		getPager().setPageNumber((int) currentPage);
		setItems(FXCollections.observableList(list));
	}

	public void addQueryListener(InvalidationListener listener) {
		queryChanged.addListener(listener);
	}

	public void removeQueryListener(InvalidationListener listener) {
		queryChanged.addListener(listener);
	}

	@Override
	public TableKey getSelectedKey() {
		D sel = getSelectedItem();
		return new TableKey("id", sel.getId());
	}
	
	@Override
	public Collection<TableKey> getSelectedKeys() {
		return getSelectedItems().stream()
				.map(BasicEntity::getId)
				.map(id -> new TableKey("id", id))
				.collect(Collectors.toList());
	}	
	
	protected Pager getPager() {
		if (pager == null) pager = new Pager();
		return pager;
	}
	
	@Override
	protected ProjectPanelBase createProjectPanel() {
		ProjectPanelBase pp = new ProjectPanelBase();
		pp.getChildren().add(getPager());
		ListContentBinding.bind(pp.columnsProperty(), getTable().getVisibleLeafColumns(), ProjectPanelBase::toPair);

		// the identity of the first argument, as we test all matching operations with a string.
		pp.setValueParser((s,c) -> s);

		return pp;
	}
	
	public QueryDetail createQueryDetail() {
		int pageSize = getPager().getPageSize();
		int pageNo = getPager().getPageNumber();
		QueryDetail q = new QueryDetail(((pageNo-1)*pageSize) + 1, pageSize);
		return q;
	}

	public TemplateQuery createQueryTemplate() {
		ProjectPanelBase pp = getProjectPanel();
		List<RowCriterion<?>> filter = pp.getFilter();
		TemplateQuery q = new TemplateQuery();
		for (RowCriterion<?> r : filter) {
			List<TemplateQueryWhereToken> tokens = criterion2Token(r);
			if (tokens.size() == 1) q.addWhereToken(tokens.get(0));
			else if (tokens.size() > 1) {
				q.addNewFilter().getWhereTokens().addAll(tokens);
			}
		}
		return q;
	}
	
	private List<TemplateQueryWhereToken> criterion2Token(RowCriterion<?> r) {
		if (r instanceof RowCriterion.BeginsWith) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, r.getColumnName(), "%"+r.getNeedleString()));
		}
		return Collections.emptyList();
	}
	

}
