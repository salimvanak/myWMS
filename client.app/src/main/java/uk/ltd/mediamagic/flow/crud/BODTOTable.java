package uk.ltd.mediamagic.flow.crud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.beans.InvalidationListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import uk.ltd.mediamagic.common.data.RowCriterion;
import uk.ltd.mediamagic.fx.binding.ListContentBinding;
import uk.ltd.mediamagic.fx.binding.SimpleObservable;
import uk.ltd.mediamagic.fx.control.TableKeySelectable;
import uk.ltd.mediamagic.fx.controller.FxTableController;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.table.ProjectPanelBase;

public class BODTOTable<D extends BasicEntity> extends FxTableController<BODTO<D>> implements TableKeySelectable {

	private final SimpleObservable queryChanged = new SimpleObservable();
	protected final Callback<TableView<BODTO<D>>, Boolean> QUERY_SORT_POLICY = t -> { queryChanged.invalidate(); return true; };
	
	public BODTOTable() {
		this(null);
		getTable().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public BODTOTable(String saveSettingsUID) {
		super(saveSettingsUID);
		queryChanged.bind(getProjectPanel().filterProperty());
		getTable().setSortPolicy(QUERY_SORT_POLICY);
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
		return new TableKey("id", sel.getId());
	}
	
	@Override
	public Collection<TableKey> getSelectedKeys() {
		return getSelectedItems().stream()
				.map(BODTO::getId)
				.map(id -> new TableKey("id", id))
				.collect(Collectors.toList());
	}
		
	
	@Override
	protected ProjectPanelBase createProjectPanel() {
		ProjectPanelBase pp = new ProjectPanelBase();
		ListContentBinding.bind(pp.columnsProperty(), getTable().getVisibleLeafColumns(), ProjectPanelBase::toPair);

		// the identity of the first argument, as we test all matching operations with a string.
		pp.setValueParser((s,c) -> s);

		return pp;
	}

	public QueryDetail createQueryDetail() {
		QueryDetail q = new QueryDetail(0, 100);
		for (TableColumn<?,?> col : getTable().getSortOrder()) {
			SortType type = col.getSortType();
			q.addOrderByToken(col.getId(), type == SortType.ASCENDING);
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
		return Collections.emptyList();
	}
	
}
