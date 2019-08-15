# Summary

This is a version control system is implemented from scratch and mimics the main functionality of the well-known Git, such as:<br />
  - Committing: saving the contents of entire directories of files, which are called commits.<br />
  - Checking out: restoring a version of one or more files or entire commits. <br>
  - Log: viewing the history of your backups. <br />
  - Branches: maintaining related sequences of commits.<br/>
  - Merging changes made in one branch into another.<br />

Simple Visualization of Commits (snapshots of your files): 
![img](1.png)

Current Commits:
![img](2.png)

Revert last Commit:
![img](3.png)

Multiple Commits(Commit Tree):
![img](4.png)

Internal structures:<br />
  - Blobs: Essentially the contents of files.<br />
  - Trees: Directory structures mapping names to references to blobs and other trees (subdirectories).<br>
  - Commits: Combinations of log messages, other metadata (commit date, author, etc.), a reference to a tree, and references to parent commits. The repository also maintains a mapping from branch heads to references to commits, so that certain important commits have symbolic names.<br />
  
![img](5.png)
