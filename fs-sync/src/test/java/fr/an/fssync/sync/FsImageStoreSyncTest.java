package fr.an.fssync.sync;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.sync.FsImageStoreSync;
import fr.an.fssync.sync.StoreSyncTask;
import fr.an.fssync.sync.SyncTaskPollingCallback;
import fr.an.fssync.sync.SyncTaskStatistics;
import lombok.val;

public class FsImageStoreSyncTest {

    FsImageStoreSync sut;
    SyncTaskStatistics sutTaskStatistics;
    SyncTaskPollingCallback sutPollingCallback;

    FsImageKeyStore srcStore = new InMemFsImageKeyStore();
    FsImageKeyStore destStore = new InMemFsImageKeyStore();
    
    static final FsPath dir1Path = FsPath.of("/dir1");
    static final FsPath subdir1Path = dir1Path.child("subdir1"); // "/dir1/subdir1"
    static final FsPath subdir2Path = dir1Path.child("subdir2"); // "/dir1/subdir2"
    static final FsPath newsubdir1Path = dir1Path.child("newsubdir1"); // "/dir1/newsubdir1"
    static final FsPath newsubdir1File1Path = newsubdir1Path.child("file1"); // "/dir1/newsubdir1/file1"
    
    @Before
    public void setup() {
	FsEntryInfo dir1Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123000)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	srcStore.writePathInfo(dir1Path, dir1Info, null);
	destStore.writePathInfo(dir1Path, dir1Info, null);

	FsEntryInfo subdir1Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123000)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	srcStore.writePathInfo(subdir1Path, subdir1Info, null);
	destStore.writePathInfo(subdir1Path, subdir1Info, null);

	FsPath file1Path = dir1Path.child("file1");
	FsEntryInfo file1Info = FsEntryInfo.builder()
		.iNodeType(INodeType.FILE)
		.ctime(123001).mtime(123002)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.fileSize(10)
		.md5("abc1")
		.build();
	srcStore.writePathInfo(file1Path, file1Info, null);
	//?? destStore.writePathInfo(file1Path, file1Info, null);
    }
    
    protected void initAfterSetup() {
	sut = new FsImageStoreSync(srcStore, destStore);
	sutTaskStatistics = sut.getTaskStatistics();
	sutPollingCallback = sut.getSyncTaskPollingCallback();
    }

    // ------------------------------------------------------------------------


    @Test
    public void test_createDir_poll_destCallback_taskDone() {
	// Given
	initAfterSetup();

	FsEntryInfo subdir2Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123003)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	
	// When
	srcStore.writePathInfo(subdir2Path, subdir2Info, null);
	
	// Then
	assertStatsTasks(1, 0, 0);
	assertStatsMetadataTasks(1, 0, 0);

	// Step 2: poll task
	// When
	StoreSyncTask task = sutPollingCallback.pollTask(null);
	
	// Then
	Assert.assertNotNull(task);
	assertStatsTasks(0, 1, 0);
	assertStatsMetadataTasks(0, 1, 0);
	assertEqualDiffEntryPathes("/dir1", "/dir1/subdir2");
	
	// Step 3: task result before task callback (!)
	// When
	destStore.writePathInfo(subdir2Path, subdir2Info, null);

	// Then
	assertEqualDiffEntryPathes();

	// Step 4: task callback
	sutPollingCallback.taskDone(task);
	
	// Then
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);
    }

    @Test
    public void test_createDir_poll_taskDone_destCallback() {
	// Given
	initAfterSetup();

	FsEntryInfo subdir2Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123003)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	
	// When
	srcStore.writePathInfo(subdir2Path, subdir2Info, null);
	
	// Then
	assertEqualDiffEntryPathes("/dir1", "/dir1/subdir2");
	assertStatsTasks(1, 0, 0);
	assertStatsMetadataTasks(1, 0, 0);

	// Step 2: poll task
	// When
	StoreSyncTask task = sutPollingCallback.pollTask(null);
	
	// Then
	Assert.assertNotNull(task);
	assertStatsTasks(0, 1, 0);
	assertStatsMetadataTasks(0, 1, 0);

	// When
	sutPollingCallback.taskDone(task);

	// Then
	assertEqualDiffEntryPathes();
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);

	// Step 3: mock execute task callback
	// When
	destStore.writePathInfo(subdir2Path, subdir2Info, null);

	// Then
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);
    }

    @Test
    public void test_createFile_poll_destCallback_taskDone() {
	// Given
	initAfterSetup();

	FsEntryInfo subdir2Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123003)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	
	// When
	srcStore.writePathInfo(subdir2Path, subdir2Info, null);
	
	// Then
	assertStatsTasks(1, 0, 0);
	assertStatsMetadataTasks(1, 0, 0);

	// Step 2: poll task
	// When
	StoreSyncTask task = sutPollingCallback.pollTask(null);
	
	// Then
	Assert.assertNotNull(task);
	assertStatsTasks(0, 1, 0);
	assertStatsMetadataTasks(0, 1, 0);
	assertEqualDiffEntryPathes("/dir1", "/dir1/subdir2");
	
	// Step 3: task result before task callback (!)
	// When
	destStore.writePathInfo(subdir2Path, subdir2Info, null);

	// Then
	assertEqualDiffEntryPathes();

	// Step 4: task callback
	sutPollingCallback.taskDone(task);
	
	// Then
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);
    }

    @Test
    public void test_createFile_poll_taskDone_destCallback() {
	// Given
	initAfterSetup();

	FsEntryInfo subdir2Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123003)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	
	// When
	srcStore.writePathInfo(subdir2Path, subdir2Info, null);
	
	// Then
	assertEqualDiffEntryPathes("/dir1", "/dir1/subdir2");
	assertStatsTasks(1, 0, 0);
	assertStatsMetadataTasks(1, 0, 0);

	// Step 2: poll task
	// When
	StoreSyncTask task = sutPollingCallback.pollTask(null);
	
	// Then
	Assert.assertNotNull(task);
	assertStatsTasks(0, 1, 0);
	assertStatsMetadataTasks(0, 1, 0);

	// When
	sutPollingCallback.taskDone(task);

	// Then
	assertEqualDiffEntryPathes();
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);

	// Step 3: mock execute task callback
	// When
	destStore.writePathInfo(subdir2Path, subdir2Info, null);

	// Then
	assertStatsTasks(0, 0, 1);
	assertStatsMetadataTasks(0, 0, 1);
    }

    /**
     * test that task on child file are locked by task on parent dir
     * 
     * move "/dir1/subdir1" "/dir1/newsubdir1" => (task1 for delete dir + task2 for create dir)
     * then create a sub file "/dir1/newsubdir1/file1" (locked task3)
     * poll task1 
     * poll => task2    (this could also return task3, cf other test)
     * poll => null : task3 not pollable (while still locked by running task2)
     * finish task1 & task2
     * => task3 now poolable
     */
    @Test
    public void test_moveDir_createMovedSubFile_pollTask1Delete_pollTask2CreateDir_pollNull_finishTask12_pollTask3() {
	// Given
	initAfterSetup();

	val subdir1Info = srcStore.readPathInfo(subdir1Path);
	FsEntryInfo dir2Info = FsEntryInfo.builder()
		.iNodeType(INodeType.DIRECTORY)
		.ctime(123003)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.build();
	FsEntryInfo newFile1Info = FsEntryInfo.builder()
		.iNodeType(INodeType.FILE)
		.ctime(123001).mtime(123002)
		.ownerName("owner1").groupName("group1").perms(FsPermission.valueOf("-rw-rw----"))
		.fileSize(100)
		.md5("abc1")
		.build();
	
	// When
	srcStore.writePathInfo(subdir1Path, null, subdir1Info);
	srcStore.writePathInfo(newsubdir1Path, dir2Info, null);
	srcStore.writePathInfo(newsubdir1File1Path, newFile1Info, null);
	
	// Then
	assertEqualDiffEntryPathes("/dir1", "/dir1/subdir1", 
		"/dir1/newsubdir1", "/dir1/newsubdir1/file1");
	assertStatsTasks(3, 0, 0);

	// Step 2: poll task1 and task2
	// When
	StoreSyncTask task1 = sutPollingCallback.pollTask(null);

	// Then
	Assert.assertNotNull(task1);
	Assert.assertEquals(FsPath.of("/dir1/subdir1"), task1.getPath());

	// When
	StoreSyncTask task2 = sutPollingCallback.pollTask(null);
	
	// Then
	Assert.assertNotNull(task2);
	Assert.assertEquals(FsPath.of("/dir1/newsubdir1"), task2.getPath());
	assertStatsTasks(1, 2, 0);

	// Step 3: task3 should not be pollable
	// When
	StoreSyncTask taskNull = sutPollingCallback.pollTask(null);

	// Then
	Assert.assertNull(taskNull);
	
	// Step 4: finish task1 and task2
	sutPollingCallback.taskDone(task1);
	sutPollingCallback.taskDone(task2);

	// Then
	assertStatsTasks(1, 0, 2);
	assertEqualDiffEntryPathes("/dir1", "/dir1/newsubdir1", "/dir1/newsubdir1/file1");

	// Step 4: now task2 should be pollable
	// When
	StoreSyncTask task3 = sutPollingCallback.pollTask(null);

	// Then
	Assert.assertNotNull(task3);
	assertStatsTasks(0, 1, 2);
	assertEqualDiffEntryPathes("/dir1", "/dir1/newsubdir1", "/dir1/newsubdir1/file1");

	// Step 5: mock execute task3
	sutPollingCallback.taskDone(task3);
	// Then
	assertStatsTasks(0, 0, 3);
	assertEqualDiffEntryPathes();

	// Step 6: dest filesystem callbacks .. already resolved
	// When
	destStore.writePathInfo(newsubdir1Path, dir2Info, null);
	// Then
	assertStatsTasks(0, 0, 3);
	assertEqualDiffEntryPathes();

	// When
	destStore.writePathInfo(newsubdir1File1Path, newFile1Info, null);
	// Then
	assertStatsTasks(0, 0, 3);
	assertEqualDiffEntryPathes();
    }

    
    // ------------------------------------------------------------------------
    
    private void assertStatsTasks(
	    int expectedPendingTasks,
	    int expectedRunningTasks,
	    int expectedDoneTasks
	    ) {
	Assert.assertEquals(expectedPendingTasks, sutTaskStatistics.getPendingTasks());
	Assert.assertEquals(expectedRunningTasks, sutTaskStatistics.getRunningTasks());
	Assert.assertEquals(expectedDoneTasks, sutTaskStatistics.getDoneTasks());
    }

    private void assertStatsMetadataTasks(
	    int expectedPendingMetadataTasks,
	    int expectedRunningMetadataTasks,
	    int expectedDoneMetadataTasks
	    ) {
	Assert.assertEquals(expectedPendingMetadataTasks, sutTaskStatistics.getPendingMetadataTasks());
	Assert.assertEquals(expectedRunningMetadataTasks, sutTaskStatistics.getRunningMetadataTasks());
	Assert.assertEquals(expectedDoneMetadataTasks, sutTaskStatistics.getDoneMetadataTasks());
    }

    private void assertEqualDiffEntryPathes(String... pathes) {
	List<String> actualPathes = sut._getDiffEntryPathes();
	Set<String> expectedPathes = new HashSet<>(Arrays.asList(pathes));
	for(val actualPath : actualPathes) {
	    if (!expectedPathes.contains(actualPath)) {
		Assert.fail("got unexpected entry path " + actualPath);
	    }
	}
	if (expectedPathes.size() != actualPathes.size()) {
	    for(val expectedPath : expectedPathes) {
		if (!actualPathes.contains(expectedPath)) {
		    Assert.fail("expected entry path " + expectedPath);
		}
	    }
	}
    }
    
}
