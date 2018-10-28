package com.haiyunshan.note;

import club.andnext.dataset.BaseDataset;

public class NoteDataset extends BaseDataset<NoteDataset.NoteEntity> {

    /**
     *
     */
    public static class NoteEntity extends FileEntity {

        public NoteEntity(String id) {
            super(id);
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

    }
}
