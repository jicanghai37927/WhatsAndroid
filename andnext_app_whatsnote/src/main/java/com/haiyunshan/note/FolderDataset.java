package com.haiyunshan.note;

import club.andnext.dataset.BaseDataset;

public class FolderDataset extends BaseDataset<FolderDataset.FolderEntity> {

    /**
     *
     */
    public static class FolderEntity extends FileEntity {

        public FolderEntity(String id) {
            super(id);
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

    }
}
