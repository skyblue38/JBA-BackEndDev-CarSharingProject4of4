class Account {

    private long balance;
    private String ownerName;
    private boolean locked;
    public void setBalance(long newBalance) {this.balance=newBalance;}
    public void setOwnerName(String newOwnerName) {this.ownerName=newOwnerName;}
    public void setLocked(boolean newLocked) {this.locked=newLocked;}
    public long getBalance() {return this.balance;}
    public String getOwnerName() {return this.ownerName;}
    public boolean isLocked() {return this.locked;}
}