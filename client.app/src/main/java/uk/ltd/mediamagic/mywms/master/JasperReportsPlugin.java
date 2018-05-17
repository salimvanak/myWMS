package uk.ltd.mediamagic.mywms.master;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import de.linogistix.los.crud.LOSJasperReportCRUDRemote;
import de.linogistix.los.model.LOSJasperReport;
import javafx.stage.FileChooser;
import uk.ltd.mediamagic.flow.crud.CRUDKeyUtils;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.flow.crud.SubForm;
import uk.ltd.mediamagic.fx.FXUtils;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.action.RootCommand;
import uk.ltd.mediamagic.fx.controller.list.CellRenderer;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ApplicationContext;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.mywms.userlogin.LoginService;

@SubForm(title="hidden", columns=0, properties={"compiledDocument"})
public class JasperReportsPlugin extends CRUDPlugin<LOSJasperReport> {
	
	enum Action {LOAD_REPORT};
	
	public JasperReportsPlugin() {
		super(LOSJasperReport.class);
		setCreateAllowed(true);
	}
	
	@Override
	protected void createAction(CrudTable<LOSJasperReport> source, Flow flow, ViewContext context) {
		LOSJasperReportCRUDRemote crud = context.getBean(LOSJasperReportCRUDRemote.class);
		LoginService loginService = context.getBean(LoginService.class);
		MDialogs.create(context.getRootNode()).showInputDialog("Report Name", "Name")
		.ifPresent(name ->  {
			LOSJasperReport r = new LOSJasperReport();
			r.setName(name);
			r.setClient(loginService.getUsersClient());
			context.getExecutor().executeAndWait(context.getRootNode(), p -> crud.create(r));
			source.saveAndRefresh();
		});
	}
	
	@Override
	public String getPath() {
		return "{1, _System} -> {2, _Jasper Reports}";
	}
	
	@Override
	protected void configureCommands(RootCommand command) {
		super.configureCommands(command);
		command.begin(RootCommand.MENU)
			.add(AC.id(Action.LOAD_REPORT).text("Upload source file"))
		.end();
	}

	private void uploadFile(Object source, Flow flow, ViewContext context, TableKey key) {
		FXUtils.getFileOpen(context.getRootNode(), Collections.singleton(new FileChooser.ExtensionFilter("Jasper Report", ".jrxml")))
		.ifPresent(file -> {
			LOSJasperReportCRUDRemote crud = context.getBean(LOSJasperReportCRUDRemote.class);
			Long id = CRUDKeyUtils.getID(key);
			Objects.requireNonNull(id, "The entity id cannot be null");
		
			context.getExecutor().executeAndWait(context.getRootNode(), p -> {
				LOSJasperReport report = crud.retrieve(id);
				Objects.requireNonNull(id, "Jasper Report not found");
				StringBuilder sout = Files.lines(file.toPath())
						.collect(StringBuilder::new, (sb,s) -> sb.append(s).append('\n'), StringBuilder::append);
				report.setSourceDocument(sout.toString());
				crud.update(report);
				return null;
			});			
		});
	}
	
	@Override
	public Flow createNewFlow(ApplicationContext context) {
		return super.createNewFlow(context)
				.globalWithSelection()
					.withSelection(Action.LOAD_REPORT, this::uploadFile)
				.end();
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
