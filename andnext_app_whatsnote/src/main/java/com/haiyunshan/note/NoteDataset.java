package com.haiyunshan.note;

public class NoteDataset extends FileDataset<NoteDataset.NoteEntity> {

    /**
     *
     */
    public static class NoteEntity extends FileDataset.FileEntity {

        public NoteEntity(String id) {
            super(id);
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

    }
}
