package spotify.bot.filter.remapper;

import spotify.bot.util.data.AlbumGroupExtended;
import spotify.bot.util.data.AlbumTrackPair;

public interface Remapper {
	/**
	 * Fetch the AlbumGroupExtended of this remapper
	 * 
	 * @return
	 */
	AlbumGroupExtended getAlbumGroup();

	/**
	 * Returns true if the given AlbumGroupExtended is allowed for this remapper
	 * (e.g. EPs only allow Singles)
	 * 
	 * @param albumGroupExtended
	 * @return
	 */
	boolean isAllowedAlbumGroup(AlbumGroupExtended albumGroupExtended);

	/**
	 * Returns true if the given release qualifies as a remappable candidate. This
	 * is the main logic of the remappers and largely implementation-specific; see
	 * the respective implementation Javadocs for more details.
	 * 
	 * @param atp
	 * @return
	 */
	boolean qualifiesAsRemappable(AlbumTrackPair atp);
}