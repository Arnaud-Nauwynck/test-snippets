package fr.an.fssync.depencymgr;

import fr.an.fssync.model.FsPath;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FsStoreAndPath {
    public final FsStoreName fsName;
    public final FsPath path;
}