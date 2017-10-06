package uk.ltd.mediamagic.mywms.system;

import java.util.Arrays;
import java.util.List;

import org.mywms.model.Client;
import org.mywms.model.Role;
import org.mywms.model.User;

import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.user.crud.UserCRUDRemote;
import de.linogistix.los.user.query.RoleQueryRemote;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import uk.ltd.mediamagic.common.utils.Strings;
import uk.ltd.mediamagic.flow.crud.BasicEntityEditor;
import uk.ltd.mediamagic.flow.crud.CRUDPlugin;
import uk.ltd.mediamagic.flow.crud.CrudTable;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.action.AC;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.ContextBase;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.mywms.common.MyWMSUserPermissions;

public class UsersPlugin  extends CRUDPlugin<User> {
	
	public UsersPlugin() {
		super(User.class);
		setCreateAllowed(true);
		setUserPermissions(new MyWMSUserPermissions.ForSystemData());
	}

	@Override
	public String getPath() {
		return "{1, _System} -> {1, _Users}";
	}

	@Override
	protected List<String> getTableColumns() {
		return Arrays.asList("id", "name", "firstname",	"lastname");
	}
	
	@Override
	protected MyWMSEditor<User> getEditor(ContextBase context, TableKey key) {
		MyWMSEditor<User> editor = super.getEditor(context, key);
		editor.getCommands()
			.add(AC.idText("Add Role").action(s -> addRole(editor)))
			.add(AC.idText("Remove Role").action(s -> removeRole(editor)))
		.end();
		return editor;
	}
	
	@Override
	protected void createAction(CrudTable<User> source, Flow flow, ViewContext context) {
		BasicEntityEditor<Client> clientCB = new BasicEntityEditor<>();
		clientCB.configure(context, Client.class);
		TextField username = new TextField();
		boolean ok = MDialogs.create(source.getView(), "New User")
			.input("Client", clientCB)
			.input("Username", username)
			.showOkCancel();
		
		if (!ok) return;
		if (Strings.isEmpty(username.getText())) {
			FXErrors.error(source.getView(), "Please enter a user name.");
			return;
		}
		if (clientCB.getValue() == null) {
			FXErrors.error(source.getView(), "Please select a client.");
			return;
		}
		
		UserCRUDRemote query = source.getContext().getBean(UserCRUDRemote.class);
		User user = new User();
		user.setName(username.getText());
		user.setClient(clientCB.getValue());
		source.getExecutor().call(() -> query.create(user))
					.thenAcceptUI(newUser -> edit(context, User.class, newUser.getId()));
	}
	
	public void addRole(MyWMSEditor<User> editor) {
		RoleQueryRemote query = editor.getContext().getBean(RoleQueryRemote.class);
		List<Role> roles = editor.getExecutor().executeAndWait(editor.getView(), () -> {
			return query.queryAll(new QueryDetail(0, 500));
		});
		
		MDialogs.create(editor.getView(), "Add role")
			.showChoices("Select role", roles)
			.ifPresent(r -> editor.getData().getRoles().add(r));
		editor.getContext().getBean(Flow.class).executeCommand(Flow.SAVE_ACTION);
	}

	public void removeRole(MyWMSEditor<User> editor) {
		ListView<Role> rolesView = editor.findNodeById("roles");
		Role selRole = rolesView.getSelectionModel().getSelectedItem();
		if (selRole == null) {
			FXErrors.selectionError(editor.getView(), "Please select a role to remove");
		}
		editor.getData().getRoles().remove(selRole);
		editor.getContext().getBean(Flow.class).executeCommand(Flow.SAVE_ACTION);
	}

}
