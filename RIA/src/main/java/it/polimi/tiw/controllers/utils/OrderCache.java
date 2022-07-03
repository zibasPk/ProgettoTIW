package it.polimi.tiw.controllers.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;

public class OrderCache {
	private static final Map<Integer, List<Integer>> nameToAlbumOrder = new HashMap<>();

	/**
	 * Saves the order of the given album list in cache and DB
	 */
	public static boolean saveOrder(User user, List<Integer> albumOrder, Connection connection) throws SQLException {
		AlbumDAO albumService = new AlbumDAO(connection);
		// check if the saved order contains albums of other users or non existing ones
		for (Integer albumId : albumOrder) {
			Album album = albumService.findAlbum(albumId);
			if (album == null || album.getOwnerID() != user.getId())
				return false;
		}
		albumService.saveAlbumOrder(user.getId(), albumOrder);
		nameToAlbumOrder.put(user.getId(), albumOrder);
		return true;
	}

	/**
	 * Adds an album to the currently saved order
	 * @param user
	 * @param albumId
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static boolean addToOrder(User user, int albumId, Connection connection) throws SQLException {
		AlbumDAO albumService = new AlbumDAO(connection);
		Album album = albumService.findAlbum(albumId);
		if (album == null || album.getOwnerID() != user.getId())
			return false;
		List<Integer> order = nameToAlbumOrder.get(user.getId());
		order.add(null);
		int position = order.size() + 1;
		albumService.addToAlbumOrder(user.getId(), albumId, position);
		return true;
	}

	/**
	 * Returns and ordered list of the albums of the given user loading order from
	 * cache or DB<br>
	 * If there is no saved order it returns the default order.
	 */
	public List<Album> getOrderedAlbums(User user, Connection connection) throws SQLException {
		List<Integer> albumOrder = nameToAlbumOrder.get(user.getId());
		List<Album> albumsInOrder = new ArrayList<>();
		AlbumDAO albumService = new AlbumDAO(connection);
		// if there no entry in the cache look for it in the DB
		if (albumOrder == null) {
			albumOrder = albumService.getAlbumOrder(user.getId());
		}
		// if and order was found
		if (albumOrder != null) {
			if (albumOrder.size() != albumService.findOwnedAlbums(user.getId()).size()) {
				Logger.debug("album amount changed invalidating cache entry");
				nameToAlbumOrder.remove(user.getId());
				albumService.deleteAlbumOrder(user.getId());
				return this.getOrderedAlbums(user, connection);
			}
			for (Integer albumID : albumOrder) {
				Album album = albumService.findAlbum(albumID);
				if (album != null) {
					albumsInOrder.add(album);
				} else {
					Logger.debug("one of the ordered albums no longer exist invalidating cache entry");
					nameToAlbumOrder.remove(user.getId());
					albumService.deleteAlbumOrder(user.getId());
					return this.getOrderedAlbums(user, connection);
				}
			}
			return albumsInOrder;
		}

		Logger.debug("no saved order found returning standard order");
		return albumService.findOwnedAlbums(user.getId());
	}

}
