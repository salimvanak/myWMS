package uk.ltd.mediamagic.mywms.master;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import de.linogistix.los.model.LOSJasperReport;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;

@SubForm(title="hidden", columns=0, properties={"compiledDocument"})
public class JasperReportsPlugin extends CRUDPlugin<LOSJasperReport> {

	enum StockTakingFilter {All, Open, Waiting, Processing, Finished}
	
	public JasperReportsPlugin() {
		super(LOSJasperReport.class);
	}
	
	@Override
	public String getPath() {
		return "{1, _System} -> {2, _Jasper Reports}";
	}
		
	@Override
	public Supplier<CellRenderer<LOSJasperReport>> createCellFactory() {
		return super.createCellFactory();
	}

	@Override
	public List<String> getTableColumns() {
		return Arrays.asList("modified", "name", "client.name");
	}
			
}
