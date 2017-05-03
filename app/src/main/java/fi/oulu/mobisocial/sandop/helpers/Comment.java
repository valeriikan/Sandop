package fi.oulu.mobisocial.sandop.helpers;

public class Comment {
    private String name;
    private String content;

    public Comment() {
        name = "";
        content = "";
    }

    public Comment(String cName, String cContent) {
        name = cName;
        content = cContent;
    }

    public String getName() {
        return name;
    }
    public String getContent() {
        return content;
    }
    public void setName(String cName) {
        name = cName;
    }
    public void setContent(String cContent) {
        content = cContent;
    }

    public boolean isEqual(Comment comment) {
        boolean state = false;
        if (comment.getName().equals(name)) {
            if (comment.getContent().equals(content)) state = true;
        }
        return state;
    }


}