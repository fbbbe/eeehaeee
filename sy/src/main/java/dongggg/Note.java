package dongggg;

// 이 코드는 노트 이름, 내용, 수정날짜 등을 가져와서 저장하는 코드임.

public class Note {

    private int id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Note(int id, String title, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 새 노트 생성용 생성자 (id, 날짜는 DB가 채움)
    public Note(String title, String content) {
        this(0, title, content, null, null);
    }

    // getter / setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}