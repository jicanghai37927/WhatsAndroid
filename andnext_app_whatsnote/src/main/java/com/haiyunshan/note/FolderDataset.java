package com.haiyunshan.note;

public class FolderDataset extends FileDataset<FolderDataset.FolderEntity> {

    /**
     *
     */
    public static class FolderEntity extends FileDataset.FileEntity {

        public FolderEntity(String id) {
            super(id);
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

    }
}
