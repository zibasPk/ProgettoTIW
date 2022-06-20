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
	private static final Map<User, List<Integer>> nameToAlbumOrder = new HashMap<>();

	/**
	 * Saves the order of the given album list
	 */
	public static void saveOrder(User user, List<Album> albumsInOrder) {
		List<Integer> albumOrder = new ArrayList<>();
		albumsInOrder.forEach(album -> albumOrder.add(album.getID()));
		nameToAlbumOrder.put(user, albumOrder);
	}
	
	/**
	 * Returns and ordered list of the albums of the given user loading order from cache
	 */
	public List<Album> getOrderedAlbums(User user, Connection connection) throws SQLException {
		List<Integer> albumOrder = nameToAlbumOrder.get(user);
		List<Album> albumsInOrder = new ArrayList<>();
		AlbumDAO albumService = new AlbumDAO(connection);
		// if there is and order load from cache
		if (albumOrder != null) {
			for (Integer albumID : albumOrder) {
				Album album = albumService.findAlbum(albumID);
				if (album != null) {
					albumsInOrder.add(album);
				} else {
					Logger.debug("one of the orderes albums no longer exist invalidating entry");
					nameToAlbumOrder.remove(user);
					return this.getOrderedAlbums(user, connection);
				}
			}
			return albumsInOrder;
		} else {
			Logger.debug("no saved order found returning standard order");
			return albumService.findOwnedAlbums(user.getId());
		}
	}

}
