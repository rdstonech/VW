package com.voxelwind.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voxelwind.api.server.LevelCreator;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@ToString
public class VoxelwindConfiguration
{
	/**
	 * Configuration for MCPE network listener.
	 */
	private McpeListenerConfiguration mcpeListener;
	/**
	 * The maximum number of players Voxelwind will accept before rejecting further connections. By default, this is set
	 * to -1 (unlimited).
	 */
	private int maximumPlayerLimit;
	/**
	 * Configuration for the RCON service.
	 */
	private RconConfiguration rcon;
	/**
	 * Configuration for the Chunk GC.
	 */
	private ChunkGCConfiguration chunkGC;
	/**
	 * Allows the user to configure some performance-related settings.
	 */
	private FineTuning fineTuning;
	/**
	 * The maximum view distance permitted. This has an upper limit of 16. By default, it is 8.
	 */
	private int maximumViewDistance;
	private Map<String, LevelConfiguration> levels;
	
	@Getter
	@ToString
	public static class McpeListenerConfiguration
	{
		/**
		 * The host name or IP address Voxelwind will bind to. By default, Voxelwind will bind to 0.0.0.0.
		 */
		private String host;
		/**
		 * The port number Voxelwind will bind to. By default, Voxelwind will bind to port 19132.
		 */
		private int port;
	}
	
	@Getter
	@ToString
	public static class RconConfiguration
	{
		/**
		 * Whether or not RCON is enabled.
		 */
		private boolean enabled;
		/**
		 * The host name or IP address Voxelwind will bind to. By default, Voxelwind will bind to 0.0.0.0.
		 */
		private String host;
		/**
		 * The port number Voxelwind will bind to. By default, Voxelwind will bind to port 19132.
		 */
		private int port;
		/**
		 * The password for the RCON server.
		 */
		private String password;
		
		public void clearPassword ()
		{
			password = null;
		}
	}
	
	@Getter
	@ToString
	public static class ChunkGCConfiguration
	{
		/**
		 * Should we GC?
		 */
		private boolean enabled;
		/**
		 * When should we release a newly loaded chunk? (atlest this time the chunk will stay loaded after read from disk)
		 */
		private int releaseAfterLoadSeconds;
		/**
		 * Release the chunk after x seconds after the last access was on it
		 */
		private int releaseAfterLastAccess;
		/**
		 * Spawn radius to keep in memory
		 */
		private int spawnRadiusToKeep;
	}
	
	@Getter
	@ToString
	public static class LevelConfiguration
	{
		/**
		 * Directory of the world.
		 */
		@JsonProperty (required = false)
		private String directory;
		/**
		 * Storage to use.
		 */
		private LevelCreator.WorldStorage storage;
		/**
		 * Whether or not this is the default world.
		 */
		@JsonProperty ("default")
		private boolean isDefault;
		/**
		 * Whether or not to load spawn chunks. A 5x5 radius around the spawn will be loaded.
		 */
		private boolean loadSpawnChunks;
	}
	
	@Getter
	@ToString
	public static class FineTuning
	{
		/**
		 * Specifies how many threads should be used to load chunks. By default, two threads are used.
		 */
		private int chunkLoadThreads;
		/**
		 * Whether or not Voxelwind should try to use SO_REUSEPORT. This is only supported under Linux 3.9+. This increases
		 * the potential throughput of the server.
		 */
		private boolean useSoReuseport;
	}
	
	public boolean addMissingFields ()
	{
		boolean needToSave = false;
		
		if (mcpeListener == null)
		{
			mcpeListener = new McpeListenerConfiguration ();
			mcpeListener.host = "0.0.0.0";
			mcpeListener.port = 19132;
			needToSave = true;
		}
		
		if (rcon == null)
		{
			rcon = new RconConfiguration ();
			rcon.enabled = false;
			rcon.host = "127.0.0.1";
			rcon.port = 27015;
			rcon.password = generateRandomPassword ();
			needToSave = true;
		}
		
		if (chunkGC == null)
		{
			chunkGC = new ChunkGCConfiguration ();
			chunkGC.enabled = true;
			chunkGC.releaseAfterLastAccess = 30;
			chunkGC.releaseAfterLoadSeconds = 120;
			chunkGC.spawnRadiusToKeep = 6;
			needToSave = true;
		}
		
		if (maximumViewDistance < 5 || maximumViewDistance > 16)
		{
			maximumViewDistance = 8;
			needToSave = true;
		}
		
		if (levels == null || levels.isEmpty ())
		{
			LevelConfiguration wc = new LevelConfiguration ();
			wc.directory = "test-world";
			wc.isDefault = true;
			wc.storage = LevelCreator.WorldStorage.NULL;
			wc.loadSpawnChunks = true;
			levels = new HashMap<> ();
			levels.put ("world", wc);
			needToSave = true;
		}
		
		if (fineTuning == null)
		{
			fineTuning = new FineTuning ();
			fineTuning.chunkLoadThreads = 2;
			fineTuning.useSoReuseport = false;
			needToSave = true;
		}
		
		return needToSave;
	}
	
	public static VoxelwindConfiguration load (Path path) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader (path))
		{
			return VoxelwindServer.MAPPER.readValue (reader, VoxelwindConfiguration.class);
		}
	}
	
	public static void save (Path path, VoxelwindConfiguration configuration) throws IOException
	{
		try (BufferedWriter writer = Files.newBufferedWriter (path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
		{
			VoxelwindServer.MAPPER.writerWithDefaultPrettyPrinter ().writeValue (writer, configuration);
		}
	}
	
	public static VoxelwindConfiguration defaultConfiguration ()
	{
		VoxelwindConfiguration configuration = new VoxelwindConfiguration ();
		configuration.addMissingFields ();
		return configuration;
	}
	
	private static String generateRandomPassword ()
	{
		BigInteger integer = new BigInteger (130, new Random ());
		return integer.toString (36);
	}
}
