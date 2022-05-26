package fr.an.hadoop.fs.dirserver.fsdata;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import fr.an.hadoop.fs.dirserver.fsdata.hadoop.HadoopNodeFsDataProvider;

public abstract class NodeFsDataProviderFactory {
	
	public abstract NodeFsDataProvider create(String baseUrl);
	
	// TODO only Hadoop api supported yet
	public static NodeFsDataProviderFactory defaultInstance = new HadoopNodeFsDataProviderFactory(new Configuration());
	
	
	public static class HadoopNodeFsDataProviderFactory extends NodeFsDataProviderFactory {

		private Configuration hadoopConf;
		
		public HadoopNodeFsDataProviderFactory(Configuration hadoopConf) {
			this.hadoopConf = hadoopConf;
		}

		@Override
		public NodeFsDataProvider create(String baseUrl) {
			URI baseURI = createURI(baseUrl);
			FileSystem fs;
			try {
				fs = FileSystem.get(baseURI, hadoopConf);
			} catch (IOException ex) {
				throw new RuntimeException("Failed FileSystem.get(" + baseURI + ")", ex);
			}
			return new HadoopNodeFsDataProvider(baseUrl, fs);
		}
	}

	public static URI createURI(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("bad URI '" + uri + "'", ex);
		}
	}
}
