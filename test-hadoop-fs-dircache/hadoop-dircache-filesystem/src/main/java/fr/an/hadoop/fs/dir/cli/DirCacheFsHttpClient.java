package fr.an.hadoop.fs.dir.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import fr.an.hadoop.fs.dirserver.api.converter.HadoopFsDTOConverter;
import fr.an.hadoop.fs.dirserver.dto.FileStatusDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyCreateDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyDeleteDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyMkdirsDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyRenameDTO;
import lombok.val;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public class DirCacheFsHttpClient {

	private Retrofit2DirApi dirRestApi;
	
	// ------------------------------------------------------------------------
	
	public DirCacheFsHttpClient(String baseUrl) {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.build();
		this.dirRestApi = retrofit.create(Retrofit2DirApi.class);
	}

	// ------------------------------------------------------------------------

	// @retrofit2.http.Headers
	public interface Retrofit2DirApi {
	
		@GET("/api/fs/getFileStatus/{path}")
		public FileStatusDTO getFileStatus(
				@retrofit2.http.Path("path") String path);

		@GET("/api/fs/listStatus/{path}")
		public FileStatusDTO[] listStatus(
				@retrofit2.http.Path("path") String path);

		@PUT("/api/fs/query-listStatuses")
		public FileStatusDTO[] listStatuses(
				@retrofit2.http.Body List<String> pathes);

		@GET("/api/fs/globStatus/{pathPattern}")
		public FileStatusDTO[] globStatus(
				@retrofit2.http.Path("pathPattern") String pathPattern);

		@PUT("/api/fs/notify-create")
		public void notifyCreate(
				@retrofit2.http.Body NotifyCreateDTO req);

		@PUT("/api/fs/notify-mkdirs")
		public void notifyMkdirs(
				@retrofit2.http.Body NotifyMkdirsDTO req);

		@PUT("/api/fs/notify-rename")
		public void notifyRename(
				@retrofit2.http.Body NotifyRenameDTO req);

		@PUT("/api/fs/notify-delete")
		public void notifyDelete(
				@retrofit2.http.Body NotifyDeleteDTO req);

	}
	
	// ------------------------------------------------------------------------
	
	public FileStatus getFileStatus(Path f) throws IOException {
		String pathStr = f.toString();
		FileStatusDTO tmpres = dirRestApi.getFileStatus(pathStr);
		return HadoopFsDTOConverter.toFileStatus(tmpres);
	}

	public FileStatus[] listStatus(Path f) {
		String pathStr = f.toString();
		FileStatusDTO[] tmpres = dirRestApi.listStatus(pathStr);
		return HadoopFsDTOConverter.toFileStatuses(tmpres);
	}

	public FileStatus[] listStatuses(Path[] files) {
		List<String> req = new ArrayList<String>();
		for(val file: files) {
			req.add(file.toString());
		}
		FileStatusDTO[] tmpres = dirRestApi.listStatuses(req);
		return HadoopFsDTOConverter.toFileStatuses(tmpres);
	}

	public FileStatus[] globStatus(Path pathPattern) {
		FileStatusDTO[] tmpres = dirRestApi.globStatus(pathPattern.toString());
		return HadoopFsDTOConverter.toFileStatuses(tmpres);
	}

	public void notifyCreate(Path f, 
			FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize) {
		NotifyCreateDTO req = new NotifyCreateDTO();
		req.setPath(f.toString());
		req.setPerm(permission.toShort());
		req.setOverwrite(overwrite);
		req.setBufferSize(bufferSize);
		req.setRepl(replication);
		req.setBlock(blockSize);
		dirRestApi.notifyCreate(req);
	}

	public void notifyMkdirs(Path f, FsPermission permission) {
		NotifyMkdirsDTO req = new NotifyMkdirsDTO();
		req.setPath(f.toString());
		req.setPerm(permission.toShort());
		dirRestApi.notifyMkdirs(req);
	}

	public void notifyRename(Path src, Path dst) {
		NotifyRenameDTO req = new NotifyRenameDTO();
		req.setSrc(src.toString());
		req.setDst(dst.toString());
		dirRestApi.notifyRename(req);
	}

	public void notifyDelete(Path f, boolean recursive) {
		NotifyDeleteDTO req = new NotifyDeleteDTO();
		req.setPath(f.toString());
		req.setRecursive(recursive);
		dirRestApi.notifyDelete(req);
	}

}
