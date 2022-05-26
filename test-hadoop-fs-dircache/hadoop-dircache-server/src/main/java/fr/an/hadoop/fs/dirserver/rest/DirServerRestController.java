package fr.an.hadoop.fs.dirserver.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.an.hadoop.fs.dirserver.api.converter.HadoopFsDTOConverter;
import fr.an.hadoop.fs.dirserver.dto.FileStatusDTO;
import fr.an.hadoop.fs.dirserver.dto.MountedDirDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyCreateDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyDeleteDTO;
import fr.an.hadoop.fs.dirserver.dto.NotifyRenameDTO;
import fr.an.hadoop.fs.dirserver.service.DirService;

@RequestMapping(path="/api/fs")
public class DirServerRestController {

	@Autowired
	private DirService delegate;
	
	// ------------------------------------------------------------------------
	
	@GetMapping("/dir-mounts")
	public List<MountedDirDTO> getMountedDirs() {
		return delegate.getMountedDirs();
	}

	@PutMapping("/dir-mount")
	public MountedDirDTO addMountedDir(@RequestBody MountedDirDTO req) {
		return delegate.addMountedDir(req);
	}

	@DeleteMapping("/dir-mount/{name}")
	public MountedDirDTO removeMountedDir(@PathVariable("name") String name) {
		return delegate.removeMountedDir(name);
	}

	
	
//	@GetMapping("/getNode/{path}")
//	public NodeDTO getNode(
//			@PathVariable("path") String path) {
//		val res = delegate.getFileStatus(path);
//		return res;
//	}
//
//	@GetMapping("/listStatus/{path}")
//	public FileStatusDTO[] listStatus(
//			@PathVariable("path") String path) {
//		Path hadoopPath = new Path(path);
//		FileStatus[] tmpres = delegate.listStatus(hadoopPath);
//		return HadoopFsDTOConverter.toFileStatusesDTO(tmpres);
//	}
//	
//	@PutMapping("/query-listStatuses")
//	public FileStatusDTO[] listStatuses(
//			@RequestBody List<String> req) {
//		List<Path> pathes = new ArrayList<Path>();
//		for(String path: req) {
//			pathes.add(new Path(path));
//		}
//		List<FileStatus> tmpres = delegate.listStatus(pathes);
//		return HadoopFsDTOConverter.toFileStatusesDTO(tmpres);
//	}
//
//
//	@PutMapping("/notify-create")
//	public void notifyCreate(
//			@RequestBody NotifyCreateDTO req) {
//		Path hadoopPath = new Path(req.getPath());
//		FsPermission permission = FsPermission.createImmutable(req.getPerm());
//		boolean overwrite = req.isOverwrite();
//		int bufferSize = req.getBufferSize();
//		short block_replication = req.getRepl();
//		long blockSize = req.getBlock();
//		delegate.notifyCreate(hadoopPath, permission, overwrite, bufferSize, block_replication, blockSize);
//	}
//
//	@PutMapping("/notify-rename")
//	public void notifyCreate(
//			@RequestBody NotifyRenameDTO req) {
//		Path hadoopSrcPath = new Path(req.getSrc());
//		Path hadoopDstPath = new Path(req.getDst());
//		delegate.notifyRename(hadoopSrcPath, hadoopDstPath);
//	}
//
//	@PutMapping("/notify-delete")
//	public void notifyDelete(
//			@RequestBody NotifyDeleteDTO req) {
//		Path hadoopPath = new Path(req.getPath());
//		boolean recursive = req.isRecursive();
//		delegate.notifyDelete(hadoopPath, recursive);
//	}

}
