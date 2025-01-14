package spotify.bot.config.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wrapper.spotify.enums.AlbumGroup;

import spotify.bot.util.BotUtils;
import spotify.bot.util.data.AlbumGroupExtended;

public class PlaylistStoreConfig {

	private Map<AlbumGroupExtended, PlaylistStore> playlistStoreMap;

	public PlaylistStoreConfig(Map<AlbumGroupExtended, PlaylistStore> playlistStoreMap) {
		setPlaylistStoreMap(playlistStoreMap);
	}

	public void setPlaylistStoreMap(Map<AlbumGroupExtended, PlaylistStore> playlistStoreMap) {
		this.playlistStoreMap = playlistStoreMap;
	}

	/////////////////////////
	// PLAYLIST STORE READERS

	/**
	 * Returns the playlist stores as a map
	 * 
	 * @return
	 */
	private Map<AlbumGroupExtended, PlaylistStore> getPlaylistStoreMap() {
		return playlistStoreMap;
	}

	/**
	 * Returns all set playlist stores.
	 * 
	 * @return
	 */
	public Collection<PlaylistStore> getAllPlaylistStores() {
		return getPlaylistStoreMap().values();
	}

	/**
	 * 
	 * Returns the stored playlist store by the given album group.
	 * 
	 * @param albumGroup
	 * @return
	 */
	public PlaylistStore getPlaylistStore(AlbumGroup albumGroup) {
		return getPlaylistStore(AlbumGroupExtended.fromAlbumGroup(albumGroup));
	}

	/**
	 * 
	 * Returns the stored playlist store by the given album group.
	 * 
	 * @param albumGroup
	 * @return
	 */
	public PlaylistStore getPlaylistStore(AlbumGroupExtended albumGroupExtended) {
		PlaylistStore ps = getPlaylistStoreMap().get(albumGroupExtended);
		return ps;
	}

	/**
	 * Fetch all album groups that are set in the config
	 * 
	 * @param albumGroups
	 */
	public List<AlbumGroup> getEnabledAlbumGroups() {
		List<AlbumGroup> setAlbumGroups = new ArrayList<>();
		for (AlbumGroup age : AlbumGroup.values()) {
			PlaylistStore ps = getPlaylistStore(age);
			if (ps != null) {
				if ((ps.getPlaylistId() != null && !ps.getPlaylistId().trim().isEmpty())) {
					setAlbumGroups.add(age);
				}
			}
		}
		return setAlbumGroups;
	}

	/**
	 * Fetch all album groups that are set in the config
	 * 
	 * @param albumGroups
	 */
	public List<AlbumGroupExtended> getEnabledSpecialAlbumGroups() {
		List<AlbumGroupExtended> setAlbumGroups = new ArrayList<>();
		for (AlbumGroupExtended age : AlbumGroupExtended.values()) {
			PlaylistStore ps = getPlaylistStore(age);
			if (ps != null) {
				if ((ps.getPlaylistId() != null && !ps.getPlaylistId().trim().isEmpty())) {
					setAlbumGroups.add(age);
				}
			}
		}
		return setAlbumGroups;
	}

	public static class PlaylistStore implements Comparable<PlaylistStore> {
		private final AlbumGroupExtended albumGroupExtended;
		private String playlistId;
		private Date lastUpdate;

		public PlaylistStore(AlbumGroupExtended albumGroupExtended) {
			this.albumGroupExtended = albumGroupExtended;
		}

		/////////////

		public AlbumGroupExtended getAlbumGroupExtended() {
			return albumGroupExtended;
		}

		public String getPlaylistId() {
			return playlistId;
		}

		public Date getLastUpdate() {
			return lastUpdate;
		}

		/////////////

		public void setPlaylistId(String playlistId) {
			this.playlistId = playlistId;
		}

		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
		}

		/////////////

		@Override
		public String toString() {
			return String.format("PlaylistStore<%s>", albumGroupExtended.toString());
		}

		/////////////

		@Override
		public int compareTo(PlaylistStore o) {
			return BotUtils.DEFAULT_PLAYLIST_GROUP_ORDER_COMPARATOR.compare(this.getAlbumGroupExtended(), o.getAlbumGroupExtended());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((albumGroupExtended == null) ? 0 : albumGroupExtended.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PlaylistStore other = (PlaylistStore) obj;
			if (albumGroupExtended != other.albumGroupExtended)
				return false;
			return true;
		}
	}
}
