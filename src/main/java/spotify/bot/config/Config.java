package spotify.bot.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.wrapper.spotify.enums.AlbumGroup;

import spotify.bot.config.database.DatabaseService;
import spotify.bot.config.dto.PlaylistStore;
import spotify.bot.config.dto.SpotifyApiConfig;
import spotify.bot.config.dto.StaticConfig;
import spotify.bot.config.dto.UserOptions;
import spotify.bot.util.data.AlbumGroupExtended;

@Configuration
public class Config {

	@Autowired
	private DatabaseService databaseService;

	private SpotifyApiConfig spotifyApiConfig;
	private StaticConfig staticConfig;
	private UserOptions userOptions;
	private Map<AlbumGroupExtended, PlaylistStore> playlistStoreMap;

	/**
	 * Sets up or refreshes the configuration for the Spotify bot from the database
	 * 
	 * @throws SQLException
	 */
	@PostConstruct
	private void init() throws SQLException, IOException {
		this.spotifyApiConfig = getSpotifyApiConfig();
		this.userOptions = getUserOptions();
		this.playlistStoreMap = getPlaylistStoreMap();
	}

	/**
	 * Update the access and refresh tokens, both in the config object as well as
	 * the database
	 * 
	 * @param accessToken
	 * @param refreshToken
	 * @throws IOException
	 * @throws SQLException
	 */
	public void updateTokens(String accessToken, String refreshToken) throws IOException, SQLException {
		spotifyApiConfig.setAccessToken(accessToken);
		spotifyApiConfig.setRefreshToken(refreshToken);
		databaseService.updateTokens(accessToken, refreshToken);
	}

	////////////////////
	// CONFIG DTOS

	/**
	 * Retuns the bot configuration. May be created if not present.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public SpotifyApiConfig getSpotifyApiConfig() throws SQLException, IOException {
		if (spotifyApiConfig == null) {
			spotifyApiConfig = databaseService.getSpotifyApiConfig();
		}
		return spotifyApiConfig;
	}

	/**
	 * Retuns the bot configuration. May be created if not present.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public StaticConfig getStaticConfig() throws SQLException, IOException {
		if (staticConfig == null) {
			staticConfig = databaseService.getStaticConfig();
		}
		return staticConfig;
	}

	/**
	 * Returns the user configuration. May be created if not present.
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public UserOptions getUserOptions() throws SQLException, IOException {
		if (userOptions == null) {
			userOptions = databaseService.getUserConfig();
		}
		return userOptions;
	}

	/**
	 * Returns the playlist stores as a map. May be created if not present.
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Map<AlbumGroupExtended, PlaylistStore> getPlaylistStoreMap() throws SQLException {
		if (playlistStoreMap == null) {
			playlistStoreMap = databaseService.getAllPlaylistStores();
		}
		return playlistStoreMap;
	}

	/////////////////////////
	// PLAYLIST STORE READERS

	/**
	 * Returns all set playlist stores.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Collection<PlaylistStore> getAllPlaylistStores() throws SQLException {
		return getPlaylistStoreMap().values();
	}

	/**
	 * 
	 * Returns the stored playlist store by the given album group.
	 * 
	 * @param albumGroup
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public PlaylistStore getPlaylistStore(AlbumGroup albumGroup) throws SQLException {
		return getPlaylistStore(AlbumGroupExtended.fromAlbumGroup(albumGroup));
	}

	/**
	 * 
	 * Returns the stored playlist store by the given album group.
	 * 
	 * @param albumGroup
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public PlaylistStore getPlaylistStore(AlbumGroupExtended albumGroupExtended) throws SQLException {
		PlaylistStore ps = getPlaylistStoreMap().get(albumGroupExtended);
		return ps;
	}

	/**
	 * Fetch all album groups that are set in the config
	 * 
	 * @param albumGroups
	 * @throws SQLException
	 */
	public List<AlbumGroup> getEnabledAlbumGroups() throws SQLException {
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
	 * @throws SQLException
	 */
	public List<AlbumGroupExtended> getEnabledSpecialAlbumGroups() throws SQLException {
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

	/////////////////////////
	// PLAYLIST STORE WRITERS

	/**
	 * Updates the playlist store of the given album group by the current timestamp.
	 * 
	 * @param albumGroupExtended
	 * @throws SQLException
	 */
	public void refreshPlaylistStore(AlbumGroupExtended albumGroupExtended) throws SQLException {
		databaseService.refreshPlaylistStore(albumGroupExtended.getGroup());
		invalidatePlaylistStore();
	}

	/**
	 * Removes the timestamp from the given album group's playlist store.
	 * 
	 * @param albumGroupExtended
	 * @param addedSongsCount
	 * @throws SQLException
	 */
	public void unsetPlaylistStore(AlbumGroupExtended albumGroupExtended) throws SQLException {
		databaseService.unsetPlaylistStore(albumGroupExtended);
		invalidatePlaylistStore();
	}

	/**
	 * Sets the current playlist store map to null so it gets reloaded on any new
	 * database call in {@link Config#getPlaylistStore}.
	 */
	private void invalidatePlaylistStore() {
		playlistStoreMap = null;
	}
}
