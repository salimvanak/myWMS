package uk.ltd.mediamagic.mywms;

import org.jboss.util.Strings;
import org.mywms.model.BasicEntity;

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextInputControl;
import javafx.util.Pair;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.fx.binding.AutoFill;
import uk.ltd.mediamagic.fx.concurrent.MExecutor;
import uk.ltd.mediamagic.fx.converters.ToStringConverter;
import uk.ltd.mediamagic.fx.flow.AutoInject;
import uk.ltd.mediamagic.fx.flow.ContextBase;

public class MyWMSAutoFill<T extends BasicEntity> implements AutoFill<BODTO<T>> {
 
	private ContextBase context;
	public ContextBase getContext() {
		return context;
	}

	final Class<BusinessObjectQueryRemote<T>> queryClass;
	
	@AutoInject
	public void setContext(ContextBase context) {
		this.context = context;
	}

	public MyWMSAutoFill(Class<BusinessObjectQueryRemote<T>> query) {
		super();
		this.queryClass = query;
	}

	@Override
	public void installCommands(TextInputControl textInput) {
	}

	@Override
	public void uninstallCommands(TextInputControl textInput) {
	}

	@Override
	public void cancel() {
		
	}

	@Override
	public void updateData(String text, ListProperty<BODTO<T>> list, BooleanProperty busy) {
		if (!Strings.isEmpty(text)) {
			final BusinessObjectQueryRemote<T> query = getContext().getBean(queryClass);
			busy.set(true);
			getContext().getBean(MExecutor.class)
				.call(() -> query.autoCompletion(text))
				.whenCompleteUI((l,e) -> {
					busy.set(false);
					list.addAll(l);					
				});
		}
	}

	@Override
	public Pair<IndexRange, String> getReplacement(String oldValue, BODTO<T> userSelection) {
		return new Pair<IndexRange, String>(new IndexRange(0, oldValue.length()), userSelection.getName());
	}

	@Override
	public StringConverter<BODTO<T>> getStringConverter() {
		return ToStringConverter.of(BODTO::getName);
	}
}
