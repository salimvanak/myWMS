package uk.ltd.mediamagic.mywms;

import org.mywms.model.ItemData;
import org.mywms.model.ItemDataNumber;
import org.mywms.model.ItemUnit;
import org.mywms.model.UnitLoadType;
import org.mywms.model.Zone;

import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSStorageStrategy;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import uk.ltd.mediamagic.mywms.master.BOMSPlugin;
import uk.ltd.mediamagic.mywms.master.BarcodesPlugin;
import uk.ltd.mediamagic.mywms.master.FixedLocationsPlugin;
import uk.ltd.mediamagic.mywms.master.FunctionalAreasPlugin;
import uk.ltd.mediamagic.mywms.master.ItemDataPlugin;
import uk.ltd.mediamagic.mywms.master.ItemUnitPlugin;
import uk.ltd.mediamagic.mywms.master.LocationClusterPlugin;
import uk.ltd.mediamagic.mywms.master.OrderStrategyPlugin;
import uk.ltd.mediamagic.mywms.master.RacksPlugin;
import uk.ltd.mediamagic.mywms.master.StorageLocationPlugin;
import uk.ltd.mediamagic.mywms.master.StorageLocationTypesPlugin;
import uk.ltd.mediamagic.mywms.master.StorageStrategyPlugin;
import uk.ltd.mediamagic.mywms.master.UnitLoadTypesPlugin;
import uk.ltd.mediamagic.mywms.master.WorkAreasPlugin;
import uk.ltd.mediamagic.mywms.master.WorkAreasPositionsPlugin;
import uk.ltd.mediamagic.mywms.master.ZonePlugin;
import uk.ltd.mediamagic.plugin.AbstractPluginSet;
import uk.ltd.mediamagic.plugin.PluginRelation;

public class MasterDataModule extends AbstractPluginSet {

  public MasterDataModule() {
    super("Master Data");
  }
  
  @Override
  public PluginRelation getPlugins() {
    PluginRelation m = new PluginRelation();

    m.put(ItemDataPlugin.class);
    m.put(ItemData.class, ItemDataPlugin.class);

    m.put(ItemUnitPlugin.class);
    m.put(ItemUnit.class, ItemUnitPlugin.class);

    m.put(BarcodesPlugin.class);
    m.put(ItemDataNumber.class, BarcodesPlugin.class);

    m.put(BOMSPlugin.class);
    m.put(LOSBom.class, BOMSPlugin.class);

    m.put(StorageStrategyPlugin.class);
    m.put(LOSStorageStrategy.class, StorageStrategyPlugin.class);

    m.put(OrderStrategyPlugin.class);
    m.put(LOSOrderStrategy.class, OrderStrategyPlugin.class);

    m.put(ZonePlugin.class);
    m.put(Zone.class, ZonePlugin.class);

    m.put(LocationClusterPlugin.class);
    m.put(LOSLocationCluster.class, LocationClusterPlugin.class);

    m.put(FunctionalAreasPlugin.class);
    m.put(LOSArea.class, FunctionalAreasPlugin.class);
    
    m.put(WorkAreasPlugin.class);
    m.put(LOSWorkingArea.class, WorkAreasPlugin.class);
    m.put(LOSWorkingAreaPosition.class, WorkAreasPositionsPlugin.class);
    
    m.put(StorageLocationPlugin.class);
    m.put(LOSStorageLocation.class, StorageLocationPlugin.class);

    m.put(StorageLocationTypesPlugin.class);
    m.put(LOSStorageLocationType.class, StorageLocationTypesPlugin.class);

    m.put(UnitLoadTypesPlugin.class);
    m.put(UnitLoadType.class, UnitLoadTypesPlugin.class);

    m.put(RacksPlugin.class);
    m.put(LOSRack.class, RacksPlugin.class);
    
    m.put(FixedLocationsPlugin.class);
    m.put(LOSFixedLocationAssignment.class, FixedLocationsPlugin.class);
        
    return m;
  }

}
