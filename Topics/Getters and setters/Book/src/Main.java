class Book {
    private String title;
    private int yearOfPublishing;
    private String[] authors;
    public void setTitle(String newTitle) {this.title = newTitle;}
    public void setYearOfPublishing(int newYear) {this.yearOfPublishing=newYear;}
    public void setAuthors(String[] newAuthors) {this.authors=newAuthors;}
    public String getTitle() {return this.title;}
    public int getYearOfPublishing() {return this.yearOfPublishing;}
    public String[] getAuthors() {return this.authors;}
}