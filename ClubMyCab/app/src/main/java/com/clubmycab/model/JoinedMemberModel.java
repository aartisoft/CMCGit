package com.clubmycab.model;

/**
 * Created by newpc on 29/4/16.
 */
public class JoinedMemberModel {
    private String memberId;
    private String memeberName;
    private String memberNumber;

    public String getHasBoarded() {
        return hasBoarded;
    }

    public void setHasBoarded(String hasBoarder) {
        this.hasBoarded = hasBoarder;
    }

    private String hasBoarded;

    public String getMemberImageName() {
        return memberImageName;
    }

    public void setMemberImageName(String memberImageName) {
        this.memberImageName = memberImageName;
    }

    private String memberImageName;

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    private String memberStatus;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemeberName() {
        return memeberName;
    }

    public void setMemeberName(String memeberName) {
        this.memeberName = memeberName;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

}
