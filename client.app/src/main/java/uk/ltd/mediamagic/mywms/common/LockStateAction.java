package uk.ltd.mediamagic.mywms.common;

import org.mywms.model.BasicEntity;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLock;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import uk.ltd.mediamagic.fx.MDialogs;
import uk.ltd.mediamagic.fx.data.TableKey;
import uk.ltd.mediamagic.fx.flow.FXErrors;
import uk.ltd.mediamagic.fx.flow.Flow;
import uk.ltd.mediamagic.fx.flow.ViewContext;
import uk.ltd.mediamagic.fx.flow.actions.WithSelection;
import uk.ltd.mediamagic.mywms.BeanDirectory;

public class LockStateAction<T extends BasicEntity> implements WithSelection<Object> {
	final private Class<? extends BusinessObjectCRUDRemote<T>> crudClass;
	@SuppressWarnings("rawtypes")
	final Class lockType;

	public <L extends Enum<L> & BusinessObjectLock>
	LockStateAction(Class<T> entityClass, Class<L> lockClass) {
		this.crudClass = BeanDirectory.getCRUD(entityClass);
		this.lockType = lockClass;
	}
	
	@Override
	public void execute(Object source, Flow flow, ViewContext context, TableKey key) {
		@SuppressWarnings("unchecked")
		ComboBox<Integer> lockStateField = QueryUtils.lockStateCombo(lockType);
		TextArea causeField = new TextArea();
		causeField.setPromptText("Reason");
		
		boolean ok = MDialogs.create(context.getRootNode(), "Lock Unit load")
			.input("Lock State",lockStateField)
			.input("Cause", causeField)
			.showOkCancel();
		
		if (!ok) return;
		
		Integer lock = lockStateField.getValue();
		String lockCause = causeField.getText();
		if (lock == null) {
			FXErrors.error(context.getRootNode(), "Lock state was empty.");
			return;
		}
		
		BusinessObjectCRUDRemote<T> crud = context.getBean(crudClass);
		long id = key.get("id");
		context.getExecutor().run(() -> {
			T ul = crud.retrieve(id);
			crud.lock(ul, lock, lockCause);
		})
		.thenAcceptAsync(x -> flow.executeCommand(Flow.REFRESH_ACTION), Platform::runLater);
	}

}
