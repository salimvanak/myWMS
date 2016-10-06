package uk.ltd.mediamagic.mywms.inventory;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.LOSGoodsReceiptPositionQueryRemote;
import uk.ltd.mediamagic.fx.concurrent.FTask;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;
import uk.ltd.mediamagic.mywms.common.FileOutputPane;
import uk.ltd.mediamagic.mywms.common.PDFConcat;
import uk.ltd.mediamagic.util.Files;

public class PrintGoodsReceiptLabel implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		LOSGoodsReceiptFacade facade = context.getBean(LOSGoodsReceiptFacade.class);
		LOSGoodsReceiptPositionQueryRemote query = context.getBean(LOSGoodsReceiptPositionQueryRemote.class);
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());
		FTask<File> task = context.getExecutor().fileGenerator(context, "Stock unit labels", p -> {

			p.setSteps(ids.size());
			File file = Files.createTempFile("stock-unit", ".pdf");
			try (PDFConcat concat = new PDFConcat(file)) {
				for (Long id: ids) {
					LOSGoodsReceiptPosition pos = query.queryById(id);
					StockUnitLabel doc = facade.createStockUnitLabel(pos, null);
					concat.add(doc);
					p.step();
				}
				return file;
			}
		});
		FileOutputPane.show("Stock unit labels", context, task);
		
	}

}
