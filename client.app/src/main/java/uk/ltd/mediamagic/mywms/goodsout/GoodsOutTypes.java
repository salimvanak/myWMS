package uk.ltd.mediamagic.mywms.goodsout;

import de.linogistix.los.model.Prio;
import de.linogistix.los.model.State;
import uk.ltd.mediamagic.common.data.Pair;
import uk.ltd.mediamagic.fx.misc.BiMap;

public class GoodsOutTypes {

	public static BiMap<Integer, String> state = BiMap.unmodifiable(
			new Pair<>(State.RAW, "Raw (" +State.RAW + ")"),
			new Pair<>(State.RELEASED, "Released (" +State.RELEASED + ")"),
			new Pair<>(State.ASSIGNED, "Assigned (" +State.ASSIGNED + ")"),
			new Pair<>(State.PROCESSABLE, "Processable (" +State.PROCESSABLE + ")"),
			new Pair<>(State.RESERVED, "Reserved (" +State.RESERVED + ")"),
			new Pair<>(State.STARTED, "Started (" +State.STARTED + ")"),
			new Pair<>(State.PENDING, "Pending (" +State.PENDING + ")"),
			new Pair<>(State.PICKED, "Picked (" +State.PICKED + ")"),
			new Pair<>(State.FINISHED, "Finished (" +State.FINISHED + ")"),
			new Pair<>(State.CANCELED, "Cancelled (" +State.CANCELED + ")"),
			new Pair<>(State.POSTPROCESSED, "Postprocessed (" +State.POSTPROCESSED + ")"),
			new Pair<>(State.DELETED, "Deleted (" +State.DELETED + ")")
			);

	public static BiMap<Integer, String> priority = BiMap.unmodifiable(
			new Pair<>(Prio.HIGHEST, "Highest (" + Prio.HIGHEST + ")"),
			new Pair<>(Prio.HIGH, "High (" + Prio.HIGH + ")"),
			new Pair<>(Prio.NORMAL, "Normal (" + Prio.NORMAL + ")"),
			new Pair<>(Prio.LOW, "Low (" + Prio.LOW + ")"),
			new Pair<>(Prio.LOWEST, "Lowest (" + Prio.LOWEST + ")")
			);

}
