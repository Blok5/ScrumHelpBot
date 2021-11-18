package com.example.scrumhelp.scrum.enums;

public enum Emoji {
    Memo("\uD83D\uDCDD"),
    YawningFace("\uD83E\uDD71"),
    PartyingFace("\uD83E\uDD73"),
    OkHad("\uD83D\uDC4C");

    private String text;

    Emoji(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
