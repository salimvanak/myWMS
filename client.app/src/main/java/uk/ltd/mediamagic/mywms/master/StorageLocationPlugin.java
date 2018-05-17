package uk.ltd.mediamagic.mywms.master;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;

import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.facade.ManageLocationFacade;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import javafx.application.Platform;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.BODTOPlugin;
import uk.ltd.mediamagic.flow.crud.BODTOTable;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.converters.PercentConverter;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.ViewContextBase;
import uk.ltd.mediamagic.mywms.common.BeanUtils;
import uk.ltd.mediamagic.mywms.common.Editor;
import uk.ltd.mediamagic.mywms.common.LockStateAction;
import uk.ltd.mediamagic.mywms.common.LockStateConverter;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;
import uk.ltd.mediamagic.mywms.common.QueryUtils;
import uk.ltd.mediamagic.mywms.transactions.UnitLoadRecordAction;

@SubForm(
		title="Main", columns=2, isRequired=true, 
		properties={"client", "name", "type"}
	)
@SubForm(
		title="Details", columns=2, isRequired=true, 
		properties={"scanCode", "area", "cluster", "zone", "rack", "field", "XPos", "YPos", "ZPos", "fieldIndex", "orderIndex"}
	)
@SubForm(
		title="Allocation", columns=2, 
		properties={"allocation", "allocationState", "currentTypeCapacityConstraint"}
	)
@SubForm(
		title="Stock Taking", columns=2, 
		properties={"stockTakingDate"}
	)

@SubForm(
		title="Hidden", columns=0, 
		properties={"age", "code", "locked", "version", "volume"}
	)

public class StorageLocationPlugin extends BODTOPlugin<LOSStorageLocation> implements Editor<LOSStorageLocation> {

	private enum Actions {RecalculateAllocations, StockTake, UnitLoadLog, Lock}
	
	public enum AllocationFilter {All_Locations, Empty_locations, Locations_with_stock, Locked_locations, All}

	public StorageLocationPlugin() {
		super(LOSStorageLocation.class);
		setUserPermissions(new MyWMSUserPermissions.ForMasterData());
	}
			
	@Override
	public String getPath() {
		return "{1, _Master Data} -> {1, _Location} -> {1, _Locations}";
	}

	
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withSelection(Actions.Lock, new LockStateAction<>(LOSStorageLocation.class, LOSStorageLocationLockState.class))
					.withMultiSelection(Actions.RecalculateAllocations, this::recalculateAllocation)
					.withMultiSelection(Actions.StockTake, this::createStockTakeOrders)
					.withSelection(Actions.UnitLoadLog, UnitLoadRecordAction.forStorageLocation())
				.end();
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.begin(RootCommand.MENU)
		.add(AC.id(Actions.RecalculateAllocations).text("Recalculate Allocation")
				.description("Recalculates the allocation of selected storage locations"))
		.add(AC.id(Actions.StockTake).text("Stock Take")
				.description("Creates a stock taking order for the selected location"))
		.add(AC.id(Actions.Lock).text("Lock location")
				.description("Locks a storage location"))
		.seperator()
		.add(AC.id(Actions.UnitLoadLog).text("Transaction Log")
				.description("Diaplays the unit load log for this location"))
		.end();
	}
	
	@Override
	protected StringConverter<?> getConverter(PropertyDescriptor property) {
		if ("lock".equals(property.getName())) {
			return new LockStateConverter<>(LOSStorageLocationLockState.class);
		}
		else if ("allocation".equals(property.getName())) {
			Column annot = BeanUtils.getAnnotation(property, Column.class);
			if (annot == null) return PercentConverter.forScaledPercent();
			else return PercentConverter.forScaledPercent(annot.precision(), annot.scale());
		}
		else {
			return super.getConverter(property);			
		}
	}

	public void createStockTakeOrders(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {		
		LOSStocktakingFacade service = context.getBean(LOSStocktakingFacade.class);
		withMultiSelectionTO(context, key, l -> service.generateOrders(true, null, null, null, null, l.getId(), null, null, null, null, true, true));
	}
		
	public void recalculateAllocation(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		List<BODTO<LOSStorageLocation>> locations = key.stream()
				.map(CRUDKeyUtils::<LOSStorageLocation>getBOTO)
				.collect(Collectors.toList());
		
		ManageLocationFacade facade = context.getBean(ManageLocationFacade.class);
		try {
			context.getExecutor().callAndWait(context, () -> {
				facade.recalculateReservations(locations);
				return null;
			});
		} 
		catch (Exception e) {
			FXErrors.exception(e);
		}
	}
	
	@Override
	protected void refresh(BODTOTable<LOSStorageLocation> source, ViewContextBase context) {
		TemplateQuery template = source.createQueryTemplate();
		QueryDetail detail = source.createQueryDetail();
		source.clearTable();
		
		AllocationFilter filterValue = QueryUtils.getFilter(source, AllocationFilter.All_Locations);
		TemplateQueryFilter filter = template.addNewFilter();
		switch (filterValue) {
		case All_Locations:
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "type.name", "System")));
			break;
		case Empty_locations:
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "allocation", BigDecimal.ZERO)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "type.name", "System")));
			break;
		case Locations_with_stock:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "allocation", BigDecimal.ZERO)));
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "type.name", "System")));
			break;
		case Locked_locations:			
			filter.addWhereToken(QueryUtils.or(new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "lock", LOSStorageLocationLockState.NOT_LOCKED.getLock())));
			break;
		case All: default:
		}
		
		getListData(context, detail, template)
			.thenAcceptAsync(source::setLOSResultList, Platform::runLater);			
	}
	
	@Override
	protected BODTOTable<LOSStorageLocation> getTable(ViewContextBase context) {
		BODTOTable<LOSStorageLocation> table = super.getTable(context);
		QueryUtils.addFilter(table, AllocationFilter.All_Locations, () -> refresh(table, context));
		return table;
	}
	
	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("name", "client", "type", "area", "zone", "lock");
	}
}
