package uk.ltd.mediamagic.mywms.goodsout;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.Client;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.TemplateQueryWhereToken;
import javafx.util.StringConverter;
import uk.ltd.mediamagic.flow.crud.MyWMSEditor;
import uk.ltd.mediamagic.fx.flow.AutoInject;

public class GoodsOutEditController<T extends BasicClientAssignedEntity> extends MyWMSEditor<T> {
	@AutoInject public LOSStorageLocationQueryRemote locationQuery;

	public GoodsOutEditController(BeanInfo beanInfo, Function<PropertyDescriptor, StringConverter<?>> getConverter) {
		super(beanInfo, getConverter);
		setEditorHelper(new GoodsOutEditorHelper<>(this, beanInfo, getConverter));
	}
	
	public CompletableFuture<List<BODTO<LOSStorageLocation>>> getStorageLocations(String searchString) {
		if (searchString == null) return CompletableFuture.completedFuture(Collections.emptyList());
		Client client = getData().getClient();
		TemplateQueryWhereToken token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "area.useForGoodsOut", true);
		return getExecutor().call(() -> locationQuery.autoCompletion(searchString, client, new TemplateQueryWhereToken[] {token}));
	}
}
