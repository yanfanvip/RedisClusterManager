package com.newegg.redis.leveldb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;
import com.newegg.redis.util.FileUtil;

/**
 * LevelDB 存储数据
 */
public class LevelTable {
	static Log log = LogFactory.getLog(LevelTable.class);
	static final String workhome = "database";
	
	static Options options = new Options()
			.maxOpenFiles(100)
			.compressionType(CompressionType.SNAPPY)
			.createIfMissing(true)
			.logger(new org.iq80.leveldb.Logger() {
				@Override
				public void log(String message) {
					log.debug(message);
				}
			});
	
	static Map<String, DB> databaseCache = new HashMap<String, DB>();
	
	static DB getDB(String path) throws IOException{
		if(databaseCache.containsKey(path)){
			return databaseCache.get(path);
		}
		DB db = null;
		File home = new File(path);
		try {
			db = JniDBFactory.factory.open(home, options);
		} catch (Exception e) {
			if(checkLock(home)){
				JniDBFactory.factory.repair(home, options);
			}
			db = JniDBFactory.factory.open(home, options);
		}
		databaseCache.put(path, db);
		return db;
	}
	
	public static <T extends D_Level> String path(String cluster, Class<T> clazz){
		return workhome + File.separator + cluster + File.separator + clazz.getSimpleName();
	}
	
	private static boolean checkLock(File home) throws IOException{
		File current = new File(home, "CURRENT");
		String currentName = FileUtil.readText(current);
		current = new File(home, currentName);
		return current.canRead();
	}
	
	public static <T extends D_Level> void put(String cluster, T data) throws IOException {
		DB db = getDB(path(cluster, data.getClass()));
		db.put(data.key().getBytes(), data.value(), new WriteOptions().sync(true));
	}
	
	public static <T extends D_Level> void put(String cluster, Class<T> clazz, List<T> data) throws IOException {
		DB db = getDB(path(cluster, clazz));
		WriteBatch bitch = db.createWriteBatch();
		try {
			for (T t : data) {
				bitch.put(t.key().getBytes(), t.value());
			}
			db.write(bitch, new WriteOptions().sync(true));
		} finally {
			bitch.close();
		}
	}
	
	public static <T extends D_Level> void replace(String cluster, Class<T> clazz, List<T> data) throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		WriteBatch write = db.createWriteBatch();
		try {
			iterator.seekToFirst();
			iterator.forEachRemaining(entry ->{
				byte[] key = entry.getKey();
				write.delete(key);
			});
			data.forEach(t->{
				write.put(t.key().getBytes(), t.value());
			});
			db.write(write, new WriteOptions().sync(true));
		} finally {
			iterator.close();
			write.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_Level> T get(String cluster, Class<T> clazz, String key) throws IOException {
		DB db = getDB(path(cluster, clazz));
		byte[] v = db.get(key.getBytes());
		if(v == null){
			return null;
		}
		return (T)D_Level.deserialize(clazz, v);
	}
	
	public static <T extends D_Level> void delete(String cluster, Class<T> clazz, String key) throws IOException {
		DB db = getDB(path(cluster, clazz));
		db.delete(key.getBytes());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_TimeLine> T last(String cluster, Class<T> clazz) throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		try {
			iterator.seekToLast();
			if(!iterator.hasNext()){
				return null;
			}
			Entry<byte[], byte[]> entry = iterator.next();
			byte[] v = entry.getValue();
			return (T)D_Level.deserialize(clazz, v);
		} finally {
			iterator.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_Level> void iterator(String cluster, Class<T> clazz, Consumer<T> action) throws IOException{
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		try {
			iterator.seekToFirst();
			iterator.forEachRemaining(entry ->{
				byte[] v = entry.getValue();
				action.accept((T)D_Level.deserialize(clazz, v));
			});
		} finally {
			iterator.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_Level> List<T> getAll(String cluster, Class<T> clazz)throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		List<T> list = new ArrayList<T>();
		try {
			iterator.seekToFirst();
			iterator.forEachRemaining(entry ->{
				byte[] v = entry.getValue();
				list.add((T)D_Level.deserialize(clazz, v));
			});
		} finally {
			iterator.close();
		}
		return list;
	}
	
	public static <T extends D_Level> void destroy(String cluster, Class<T> clazz) throws IOException {
		String path = path(cluster, clazz);
		DB db = getDB(path);
		databaseCache.remove(path);
		db.close();
		File f = new File(path);
		JniDBFactory.factory.destroy(f, options);
		f.delete();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_TimeLine> void startWith(String cluster, Class<T> clazz, Long start, Consumer<T> action) throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		try {
			iterator.seek((start + "").getBytes());
			iterator.forEachRemaining(entry ->{
				byte[] v = entry.getValue();
				action.accept((T)D_Level.deserialize(clazz, v));
			});
		} finally {
			iterator.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends D_TimeLine> void prevWith(String cluster, Class<T> clazz, Long start, Consumer<T> action) throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		try {
			iterator.seek((start + "").getBytes());
			while(iterator.hasPrev()){
				Entry<byte[], byte[]> entry = iterator.prev();
				byte[] v = entry.getValue();
				action.accept((T)D_Level.deserialize(clazz, v));
			}
		} finally {
			iterator.close();
		}
	}
	
	public static <T extends D_TimeLine> void deletePrev(String cluster, Class<T> clazz, Long start) throws IOException {
		DB db = getDB(path(cluster, clazz));
		ReadOptions readOptions = new ReadOptions().snapshot(db.getSnapshot());
		DBIterator iterator = db.iterator(readOptions);
		WriteBatch write = db.createWriteBatch();
		try {
			iterator.seek((start + "").getBytes());
			while(iterator.hasPrev()){
				byte[] key = iterator.prev().getKey();
				write.delete(key);
			}
			db.write(write, new WriteOptions().sync(true));
		} finally {
			iterator.close();
			write.close();
		}
	}

	public static void close() throws IOException {
		databaseCache.forEach((k,v)->{
			try { v.close(); } catch (IOException e) { e.printStackTrace(); }
		});
		databaseCache.clear();
	}
}