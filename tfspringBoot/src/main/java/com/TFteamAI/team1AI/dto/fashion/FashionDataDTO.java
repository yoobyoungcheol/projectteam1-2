package com.TFteamAI.team1AI.dto.fashion;

public class FashionDataDTO {

    private int coat;
    private int jacket;
    private int jumper;
    private int padding;
    private int vest;
    private int cardigan;
    private int blouse;
    private int shirt;
    private int sweater;

    private int outerRatio;
    private int mixedRatio;
    private int innerRatio;


    public FashionDataDTO(int outerRatio, int mixedRatio, int innerRatio) {
        // 비율 받는 생성자

        this.outerRatio = outerRatio;
        this.mixedRatio = mixedRatio;
        this.innerRatio = innerRatio;
    }

    public FashionDataDTO(int coat, int jacket, int jumper, int padding, int vest,
    int cardigan, int blouse, int shirt, int sweater) {
        // 개별 의류들을 받아서 비율을 자동 계산하는 생성자

        this.coat = coat;
        this.jacket = jacket;
        this.jumper = jumper;
        this.padding = padding;
        this.vest = vest;
        this.cardigan = cardigan;
        this.blouse = blouse;
        this.shirt = shirt;
        this.sweater = sweater;


        int outer = coat + jacket + jacket + jumper + padding;  // 아우터 계산
        int mixed = vest + cardigan + blouse;   // 이너&아우터 계산
        int inner = sweater + shirt;// 이너 계산
        int total = outer + mixed + inner;  // total 변수에다가 모든 항목 합치기
        if (total == 0) total = 1;  // 0으로 나누기 방지

        this.outerRatio = (outer * 100) / total;
        this.mixedRatio = (mixed * 100) / total;
        this.innerRatio = (inner * 100) / total;
    }

    public int getOuterRatio() {
        return outerRatio;
    }

    public int getMixedRatio() {
        return mixedRatio;
    }

    public int getInnerRatio() {
        return innerRatio;
    }

    public int getCoat() {
        return coat;
    }

    public int getJacket() {
        return jacket;
    }

    public int getJumper() {
        return jumper;
    }

    public int getPadding() {
        return padding;
    }

    public int getVest() {
        return vest;
    }

    public int getCardigan() {
        return cardigan;
    }

    public int getBlouse() {
        return blouse;
    }

    public int getShirt() {
        return shirt;
    }

    public int getSweater() {
        return sweater;
    }
}

