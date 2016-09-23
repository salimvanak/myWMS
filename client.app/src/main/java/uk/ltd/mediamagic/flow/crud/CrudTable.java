package uk.ltd.mediamagic.flow.crud;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mywms.model.BasicEntity;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.beans.binding.ObjectBinding;
import uk.ltd.mediamagic.common.data.RowCriterion;
import uk.ltd.mediamagic.fx.binding.ListContentBinding;
import uk.ltd.mediamagic.fx.binding.MBindings;
import uk.ltd.mediamagic.fx.control.TableKeySelectable;
import uk.ltd.mediamagic.fx.controller.FxTableController;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.table.ProjectPanelBase;

public class CrudTable<D extends BasicEntity> extends FxTableController<D> implements TableKeySelectable {

	private final ObjectBinding<QueryDetail> queryDetail;
	private final ObjectBinding<TemplateQuery> templateQuery;

	public CrudTable() {
		super();
		queryDetail = MBindings.get(getProjectPanel().filterProperty(), f -> createQueryDetail(getProjectPanel()));
		templateQuery = MBindings.get(getProjectPanel().filterProperty(), f -> createQueryTemplate(getProjectPanel()));
	}

	public CrudTable(String saveSettingsUID) {
		super(saveSettingsUID);
		queryDetail = MBindings.get(getProjectPanel().filterProperty(), f -> createQueryDetail(getProjectPanel()));
		templateQuery = MBindings.get(getProjectPanel().filterProperty(), f -> createQueryTemplate(getProjectPanel()));
	}
	

	public ObjectBinding<QueryDetail> queryDetailProperty() {
		return queryDetail;
	}

	public ObjectBinding<TemplateQuery> queryTemplateProperty() {
		return templateQuery;
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
		
	
	@Override
	protected ProjectPanelBase createProjectPanel() {
		ProjectPanelBase pp = new ProjectPanelBase();
		ListContentBinding.bind(pp.columnsProperty(), getTable().getVisibleLeafColumns(), ProjectPanelBase::toPair);

		// the identity of the first argument, as we test all matching operations with a string.
		pp.setValueParser((s,c) -> s);

		return pp;
	}

	public QueryDetail createQueryDetail(ProjectPanelBase pp) {
		QueryDetail q = new QueryDetail(0, 100);
		return q;
	}

	public TemplateQuery createQueryTemplate(ProjectPanelBase pp) {
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