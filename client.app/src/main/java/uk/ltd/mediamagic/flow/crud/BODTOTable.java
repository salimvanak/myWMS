package uk.ltd.mediamagic.flow.crud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import uk.ltd.mediamagic.common.data.RowCriterion;
import uk.ltd.mediamagic.fx.binding.ListContentBinding;
import uk.ltd.mediamagic.fx.binding.SimpleObservableRelay;
import uk.ltd.mediamagic.fx.control.TableKeySelectable;
import uk.ltd.mediamagic.fx.controller.FxTableController;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.table.ProjectPanelBase;

public class BODTOTable<D extends BasicEntity> extends FxTableController<BODTO<D>> implements TableKeySelectable {

	private final SimpleObservableRelay queryChanged = new SimpleObservableRelay();
	protected final Callback<TableView<BODTO<D>>, Boolean> QUERY_SORT_POLICY = t -> { queryChanged.invalidate(); return true; };
	private Pager pager;
	
	public BODTOTable() {
		this(null);
	}

	public BODTOTable(String saveSettingsUID) {
		super(saveSettingsUID);
		getTable().setSortPolicy(QUERY_SORT_POLICY);
		getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		queryChanged.bind(getProjectPanel().filterProperty());
		getPager().setPageSize(100);
		getPager().setPageNumber(1);
		getPager().pageNumberProperty().addListener(l -> queryChanged.invalidate());
	}
	
	public void clearTable() {
		setItems(null);
	}
	
	public void setLOSResultList(LOSResultList<BODTO<D>> list) {		
		int pageSize = getPager().getPageSize();
		
		long startIndex = list.getStartResultIndex();
		long currentPage = (startIndex / pageSize)+1;
		long pageMax = (list.getResultSetSize() / pageSize) 
				+ (((list.getResultSetSize() % pageSize) == 0) ? 0 : 1);
		
		//System.out.println("@("+startIndex+") "+currentPage+" Max " + list.getResultSetSize() + "(" + pageMax + ") pagesize :" + pageSize);
		getPager().setMaxPage((int) pageMax);
		getPager().setPageNumber((int) currentPage);
		setItems(FXCollections.observableList(list));
		queryChanged.validate();
	}
	
	public void setMaxPages(int max) {
		pager.setMaxPage(max);
	}
	
	public void addQueryListener(InvalidationListener listener) {
		queryChanged.addListener(listener);
	}

	public void removeQueryListener(InvalidationListener listener) {
		queryChanged.addListener(listener);
	}

	@Override
	public TableKey getSelectedKey() {
		BODTO<D> sel = getSelectedItem();
		return CRUDKeyUtils.createKey(sel);
	}
	
	@Override
	public Collection<TableKey> getSelectedKeys() {
		return getSelectedItems().stream()
				.map(CRUDKeyUtils::createKey)
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
		int pageSize = pager.getPageSize();
		int start = pageSize * (pager.getPageNumber()-1);
		System.out.println("Start " + start + " pagesize :" + pageSize);
		QueryDetail q = new QueryDetail(start, pageSize);
		for (TableColumn<?,?> col : getTable().getSortOrder()) {
			SortType type = col.getSortType();
			q.addOrderByToken(col.getId(), type == SortType.ASCENDING);
		}
		if (q.getOrderBy().size() == 0) {
			q.addOrderByToken("id", false);			
		}
		return q;
	}
	
	public TemplateQuery createQueryTemplate() {
		List<RowCriterion<?>> filter = getProjectPanel().getFilter();
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
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, r.getColumnName(), r.getNeedleString()+"%"));
		}
		else if (r instanceof RowCriterion.EndsWith) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, r.getColumnName(), "%"+r.getNeedleString()));
		}
		else if (r instanceof RowCriterion.Contains) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, r.getColumnName(), "%"+r.getNeedleString()+"%"));
		}
		else if (r instanceof RowCriterion.After) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_AFTER, r.getColumnName(), r.getNeedleString()));
		}
		else if (r instanceof RowCriterion.Before) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_BEFORE, r.getColumnName(), r.getNeedleString()));
		}
		else if (r instanceof RowCriterion.Equals) {
			return Collections.singletonList(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, r.getColumnName(), r.getNeedleString()));
		}
		return Collections.emptyList();
	}
	
}
