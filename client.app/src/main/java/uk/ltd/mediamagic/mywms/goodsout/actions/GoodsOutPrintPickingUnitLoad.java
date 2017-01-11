package uk.ltd.mediamagic.mywms.goodsout.actions;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.mywms.model.Document;

import de.linogistix.los.inventory.facade.LOSOrderFacade;
import de.linogistix.los.inventory.model.LOSPickingUnitLoad;
import de.linogistix.los.inventory.query.LOSPickingUnitLoadQueryRemote;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.concurrent.FTask;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithMultiSelection;
import uk.ltd.mediamagic.mywms.common.FileOutputPane;
import uk.ltd.mediamagic.mywms.common.PDFConcat;
import uk.ltd.mediamagic.util.Files;

public class GoodsOutPrintPickingUnitLoad implements WithMultiSelection<Object> {
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, Collection<TableKey> key) {
		boolean ok = MDialogs.create(context.getRootNode(), "Finish Picking Unit Load")
				.masthead("Finish the picking unit load.\n")
			.showOkCancel();
		
		if (!ok) return; // user canceled

		LOSOrderFacade facade = context.getBean(LOSOrderFacade.class);
		LOSPickingUnitLoadQueryRemote query = context.getBean(LOSPickingUnitLoadQueryRemote.class);
		List<Long> ids = key.stream().map(k -> (Long) k.get("id")).collect(Collectors.toList());
		FTask<File> task = context.getExecutor().fileGenerator(context, "Unit load labels", p -> {

			p.setSteps(ids.size());
			File file = Files.createTempFile("unit-loads", ".pdf");
			try (PDFConcat concat = new PDFConcat(file)) {
				for (Long id: ids) {
					LOSPickingUnitLoad ul = query.queryById(id);
					Document doc = facade.generateUnitLoadLabel(ul.getUnitLoad().getLabelId(), false);
					concat.add(doc);
					p.step();
				}
				return file;
			}
		});
		FileOutputPane.show("Unit load labels", context, task);
		
	}

}
