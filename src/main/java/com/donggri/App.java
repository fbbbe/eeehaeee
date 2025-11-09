package com.donggri;

import com.donggri.dao.FolderDao;
import com.donggri.model.Folder;

public class App {
    public static void main(String[] args) throws Exception {
        FolderDao dao = new FolderDao();

        int id = dao.create("중간발표-데모폴더");
        System.out.println("폴더 생성 id=" + id);

        for (Folder f : dao.findAll()) {
            System.out.printf("FOLDER #%d | %s | %s%n",
                    f.getFolderId(), f.getName(), f.getCreatedAt());
        }

        // 이름 바꾸기 예시
        if (id > 0) {
            dao.rename(id, "중간발표-수정됨");
            System.out.println("이름 변경 완료 → " + dao.findById(id).getName());
        }
    }
}